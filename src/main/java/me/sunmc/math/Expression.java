package me.sunmc.math;

import me.sunmc.math.constant.MathConstants;
import me.sunmc.math.function.BuiltInFunctions;
import me.sunmc.math.token.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * A compiled, evaluable mathematical expression.
 *
 * <p>Expressions are created via the {@link ExpressionBuilder} and evaluated
 * by calling {@link #evaluate()}. Variable values can be set before evaluation
 * using {@link #setVariable(String, double)} or {@link #setVariables(Map)}.</p>
 *
 * <p>Example:</p>
 * <pre>{@code
 * Expression expr = new ExpressionBuilder("3 * sin(y) - 2 / (x - 2)")
 *     .variables("x", "y")
 *     .build()
 *     .setVariable("x", 2.3)
 *     .setVariable("y", 3.14);
 *
 * double result = expr.evaluate();
 * }</pre>
 *
 * <h3>Thread Safety</h3>
 * <p>The compiled token list is immutable and shared. Variable bindings are
 * stored in a mutable map, so each thread should use its own {@code Expression}
 * instance (obtainable via {@link #copy()}).</p>
 *
 * @author sun-dev
 * @since 1.0.0
 */
public final class Expression {

    private final List<Token> rpnTokens;
    private final Map<String, Double> variables;
    private final Set<String> userFunctionNames;
    private VariableProvider variableProvider;

    /**
     * Creates a new Expression from the given RPN token list.
     *
     * @param rpnTokens         the tokens in Reverse Polish Notation order
     * @param userFunctionNames the names of user-defined functions
     */
    Expression(List<Token> rpnTokens, Set<String> userFunctionNames) {
        this.rpnTokens = List.copyOf(rpnTokens);
        this.variables = MathConstants.asVariableMap();
        this.userFunctionNames = Set.copyOf(userFunctionNames);
    }

    /**
     * Creates a deep copy of an existing Expression.
     *
     * @param other the expression to copy
     */
    public Expression(Expression other) {
        this.rpnTokens = other.rpnTokens; // Immutable, safe to share
        this.variables = new LinkedHashMap<>(other.variables);
        this.userFunctionNames = other.userFunctionNames; // Immutable
        this.variableProvider = other.variableProvider;
    }

    /**
     * Sets a variable value and returns this expression for chaining.
     *
     * @param name  the variable name
     * @param value the variable value
     * @return this expression
     * @throws IllegalArgumentException if the name conflicts with a function
     */
    public Expression setVariable(String name, double value) {
        validateVariableName(name);
        variables.put(name, value);
        return this;
    }

    /**
     * Sets multiple variable values and returns this expression for chaining.
     *
     * @param vars the variable name-value pairs
     * @return this expression
     */
    public Expression setVariables(Map<String, Double> vars) {
        vars.forEach(this::setVariable);
        return this;
    }

    /**
     * Clears all variable bindings (including built-in constants).
     *
     * @return this expression
     */
    public Expression clearVariables() {
        variables.clear();
        return this;
    }

    /**
     * Sets a {@link VariableProvider} for lazy variable resolution.
     *
     * <p>The provider is consulted during evaluation when a variable is not
     * found in the standard variable map.</p>
     *
     * @param provider the variable provider
     * @return this expression
     */
    public Expression setVariableProvider(VariableProvider provider) {
        this.variableProvider = provider;
        return this;
    }

    /**
     * Returns the names of all variables referenced in this expression.
     *
     * @return an unmodifiable set of variable names
     */
    public Set<String> getVariableNames() {
        return rpnTokens.stream()
                .filter(VariableToken.class::isInstance)
                .map(t -> ((VariableToken) t).name())
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Validates this expression, checking both syntax and variable bindings.
     *
     * @return a {@link ValidationResult} indicating validity
     */
    public ValidationResult validate() {
        return validate(true);
    }

    /**
     * Validates this expression.
     *
     * @param checkVariablesSet if {@code true}, also checks that all variables have values
     * @return a {@link ValidationResult} indicating validity
     */
    public ValidationResult validate(boolean checkVariablesSet) {
        List<String> errors = new ArrayList<>();

        // Check variable bindings
        if (checkVariablesSet) {
            for (Token token : rpnTokens) {
                if (token instanceof VariableToken(var name)) {
                    if (!variables.containsKey(name)) {
                        errors.add("Variable '%s' has not been set".formatted(name));
                    }
                }
            }
        }

        // Stack depth analysis (validate operand/operator balance)
        int stackDepth = 0;
        for (Token token : rpnTokens) {
            switch (token) {
                case NumberToken nt -> stackDepth++;
                case VariableToken vt -> stackDepth++;

                case OperatorToken(var op) -> {
                    if (op.operands() == 2) {
                        stackDepth--;
                    }
                    // Unary operators consume 1 and produce 1 → net 0
                }

                case FunctionToken(var fn) -> {
                    int args = fn.numArguments();
                    if (args > stackDepth) {
                        errors.add("Not enough arguments for function '%s'"
                                .formatted(fn.name()));
                    }
                    if (args > 1) {
                        stackDepth -= (args - 1);
                    } else if (args == 0) {
                        stackDepth++;
                    }
                }

                default -> { /* OpenParen, CloseParen, Separator won't be in RPN */ }
            }

            if (stackDepth < 1) {
                errors.add("Too many operators");
                return ValidationResult.failure(errors);
            }
        }

        if (stackDepth > 1) {
            errors.add("Too many operands");
        }

        return errors.isEmpty() ? ValidationResult.SUCCESS : ValidationResult.failure(errors);
    }

    /**
     * Evaluates this expression and returns the result.
     *
     * @return the computed value
     * @throws EvaluationException if evaluation fails (missing variables, math errors, etc.)
     */
    public double evaluate() {
        double[] stack = new double[rpnTokens.size()];
        int top = -1;

        for (Token token : rpnTokens) {
            switch (token) {
                case NumberToken(var value) -> stack[++top] = value;

                case VariableToken(var name) -> {
                    Double value = variables.get(name);
                    if (value == null && variableProvider != null) {
                        value = variableProvider.getVariable(name);
                    }
                    if (value == null) {
                        throw new EvaluationException(
                                "No value has been set for variable '%s'".formatted(name));
                    }
                    stack[++top] = value;
                }

                case OperatorToken(var op) -> {
                    if (top + 1 < op.operands()) {
                        throw new EvaluationException(
                                "Not enough operands for operator '%s'".formatted(op.symbol()));
                    }
                    try {
                        if (op.operands() == 2) {
                            double right = stack[top--];
                            double left = stack[top--];
                            stack[++top] = op.apply(left, right);
                        } else {
                            double operand = stack[top--];
                            stack[++top] = op.apply(operand);
                        }
                    } catch (ArithmeticException e) {
                        throw new EvaluationException(
                                "Arithmetic error applying operator '%s': %s"
                                        .formatted(op.symbol(), e.getMessage()), e);
                    }
                }

                case FunctionToken(var fn) -> {
                    int numArgs = fn.numArguments();
                    if (top + 1 < numArgs) {
                        throw new EvaluationException(
                                "Not enough arguments for function '%s': expected %d, got %d"
                                        .formatted(fn.name(), numArgs, top + 1));
                    }
                    double[] args = new double[numArgs];
                    for (int i = numArgs - 1; i >= 0; i--) {
                        args[i] = stack[top--];
                    }
                    try {
                        stack[++top] = fn.apply(args);
                    } catch (ArithmeticException | IllegalArgumentException e) {
                        throw new EvaluationException(
                                "Error evaluating function '%s': %s"
                                        .formatted(fn.name(), e.getMessage()), e);
                    }
                }

                // These should never appear in RPN output
                default -> throw new EvaluationException(
                        "Unexpected token in RPN output: " + token);
            }
        }

        if (top != 0) {
            throw new EvaluationException(
                    "Invalid expression: expected 1 result on the stack, found %d"
                            .formatted(top + 1));
        }

        return stack[0];
    }

    /**
     * Evaluates this expression asynchronously using a virtual thread.
     *
     * <p>Uses Java 21 virtual threads for lightweight async execution.</p>
     *
     * @return a {@link CompletableFuture} that will contain the result
     */
    public CompletableFuture<Double> evaluateAsync() {
        return CompletableFuture.supplyAsync(this::evaluate,
                runnable -> Thread.ofVirtual().name("sunmc-math-eval").start(runnable));
    }

    /**
     * Evaluates this expression asynchronously using the given executor.
     *
     * @param executor the executor to use for async evaluation
     * @return a {@link CompletableFuture} that will contain the result
     */
    public CompletableFuture<Double> evaluateAsync(Executor executor) {
        return CompletableFuture.supplyAsync(this::evaluate, executor);
    }

    /**
     * Creates a deep copy of this expression.
     *
     * @return a new independent copy
     */
    public Expression copy() {
        return new Expression(this);
    }

    /**
     * Returns a string representation of the RPN token sequence.
     * Useful for debugging.
     *
     * @return the RPN string
     */
    public String toRPNString() {
        StringBuilder sb = new StringBuilder();
        for (Token token : rpnTokens) {
            if (!sb.isEmpty()) sb.append(' ');
            switch (token) {
                case NumberToken(var value) -> sb.append(value);
                case VariableToken(var name) -> sb.append(name);
                case OperatorToken(var op) -> sb.append(op.symbol());
                case FunctionToken(var fn) -> sb.append(fn.name()).append("()");
                default -> sb.append(token);
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Expression{rpn=%s, variables=%s}".formatted(toRPNString(), variables);
    }

    private void validateVariableName(String name) {
        if (userFunctionNames.contains(name) || BuiltInFunctions.contains(name)) {
            throw new IllegalArgumentException(
                    "Variable name '%s' conflicts with a function of the same name"
                            .formatted(name));
        }
    }

    /**
     * A callback for lazily resolving variable values at evaluation time.
     */
    @FunctionalInterface
    public interface VariableProvider {
        /**
         * Returns the value of the named variable.
         *
         * @param name the variable name
         * @return the variable value, or {@code null} if not available
         */
        Double getVariable(String name);
    }
}
