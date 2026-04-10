package me.sunmc.math;

import me.sunmc.math.constant.MathConstants;
import me.sunmc.math.function.BuiltInFunctions;
import me.sunmc.math.function.MathFunction;
import me.sunmc.math.operator.MathOperator;
import me.sunmc.math.token.parser.ShuntingYardParser;
import me.sunmc.math.token.Token;

import java.util.*;

/**
 * A fluent builder for constructing {@link Expression} instances.
 *
 * <p>The builder configures the expression string, variables, custom
 * functions, custom operators, and parsing options before compiling
 * the expression via {@link #build()}.</p>
 *
 * <p>Example:</p>
 * <pre>{@code
 * Expression expr = new ExpressionBuilder("2 * x + sin(y)")
 *     .variables("x", "y")
 *     .build();
 * }</pre>
 *
 * <h2>Custom Functions and Operators</h2>
 * <pre>{@code
 * MathFunction logb = MathFunction.of("logb", (value, base) ->
 *     Math.log(value) / Math.log(base));
 *
 * Expression expr = new ExpressionBuilder("logb(8, 2)")
 *     .function(logb)
 *     .build();
 *
 * double result = expr.evaluate(); // 3.0
 * }</pre>
 *
 * @author sun-dev
 * @since 1.0.0
 */
public final class ExpressionBuilder {

    private final String expression;
    private final Map<String, MathFunction> userFunctions = new LinkedHashMap<>(4);
    private final Map<String, MathOperator> userOperators = new LinkedHashMap<>(4);
    private final Set<String> variableNames = new LinkedHashSet<>(4);
    private boolean implicitMultiplication = true;

    /**
     * Creates a new ExpressionBuilder for the given expression string.
     *
     * @param expression the mathematical expression
     * @throws IllegalArgumentException if the expression is null or blank
     */
    public ExpressionBuilder(String expression) {
        if (expression == null || expression.isBlank()) {
            throw new IllegalArgumentException("Expression must not be null or blank");
        }
        this.expression = expression;
    }

    /**
     * Registers a custom function.
     *
     * @param function the function to register
     * @return this builder
     */
    public ExpressionBuilder function(MathFunction function) {
        Objects.requireNonNull(function, "Function must not be null");
        userFunctions.put(function.name(), function);
        return this;
    }

    /**
     * Registers multiple custom functions.
     *
     * @param functions the functions to register
     * @return this builder
     */
    public ExpressionBuilder functions(MathFunction... functions) {
        for (MathFunction f : functions) {
            function(f);
        }
        return this;
    }

    /**
     * Registers multiple custom functions from a collection.
     *
     * @param functions the functions to register
     * @return this builder
     */
    public ExpressionBuilder functions(Collection<MathFunction> functions) {
        functions.forEach(this::function);
        return this;
    }

    /**
     * Registers a custom operator.
     *
     * @param operator the operator to register
     * @return this builder
     * @throws IllegalArgumentException if the operator symbol contains invalid characters
     */
    public ExpressionBuilder operator(MathOperator operator) {
        Objects.requireNonNull(operator, "Operator must not be null");
        validateOperatorSymbol(operator);
        userOperators.put(operator.symbol(), operator);
        return this;
    }

    /**
     * Registers multiple custom operators.
     *
     * @param operators the operators to register
     * @return this builder
     */
    public ExpressionBuilder operators(MathOperator... operators) {
        for (MathOperator o : operators) {
            operator(o);
        }
        return this;
    }

    /**
     * Registers multiple custom operators from a collection.
     *
     * @param operators the operators to register
     * @return this builder
     */
    public ExpressionBuilder operators(Collection<MathOperator> operators) {
        operators.forEach(this::operator);
        return this;
    }

    /**
     * Declares a variable name.
     *
     * @param name the variable name
     * @return this builder
     */
    public ExpressionBuilder variable(String name) {
        Objects.requireNonNull(name, "Variable name must not be null");
        variableNames.add(name);
        return this;
    }

    /**
     * Declares multiple variable names.
     *
     * @param names the variable names
     * @return this builder
     */
    public ExpressionBuilder variables(String... names) {
        Collections.addAll(variableNames, names);
        return this;
    }

    /**
     * Declares multiple variable names from a set.
     *
     * @param names the variable names
     * @return this builder
     */
    public ExpressionBuilder variables(Set<String> names) {
        variableNames.addAll(names);
        return this;
    }

    /**
     * Enables or disables implicit multiplication.
     *
     * <p>When enabled (default), expressions like {@code 2x} are interpreted
     * as {@code 2 * x}, and {@code 2cos(x)} as {@code 2 * cos(x)}.</p>
     *
     * @param enabled {@code true} to enable, {@code false} to disable
     * @return this builder
     */
    public ExpressionBuilder implicitMultiplication(boolean enabled) {
        this.implicitMultiplication = enabled;
        return this;
    }

    /**
     * Builds and returns the compiled {@link Expression}.
     *
     * <p>This method tokenizes and parses the expression string into
     * Reverse Polish Notation, validates function/variable name conflicts,
     * and produces an evaluable Expression.</p>
     *
     * @return the compiled expression
     * @throws ParseException           if the expression cannot be parsed
     * @throws IllegalArgumentException if variable names conflict with functions
     */
    public Expression build() {
        if (expression.isBlank()) {
            throw new IllegalArgumentException("Expression must not be blank");
        }

        // Add built-in constant names as variables
        variableNames.addAll(MathConstants.names());

        // Validate no variable-function name conflicts
        for (String name : variableNames) {
            if (BuiltInFunctions.contains(name) || userFunctions.containsKey(name)) {
                throw new IllegalArgumentException(
                        "Variable name '%s' conflicts with a function of the same name"
                                .formatted(name));
            }
        }

        // Parse to RPN
        List<Token> rpnTokens = ShuntingYardParser.toRPN(
                expression, userFunctions, userOperators,
                variableNames, implicitMultiplication);

        return new Expression(rpnTokens, userFunctions.keySet());
    }

    private void validateOperatorSymbol(MathOperator op) {
        for (char ch : op.symbol().toCharArray()) {
            if (!MathOperator.isAllowedOperatorChar(ch)) {
                throw new IllegalArgumentException(
                        "Operator symbol '%s' contains invalid character '%c'"
                                .formatted(op.symbol(), ch));
            }
        }
    }
}
