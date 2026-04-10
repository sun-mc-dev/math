package me.sunmc.math.token.parser;

import me.sunmc.math.ParseException;
import me.sunmc.math.function.MathFunction;
import me.sunmc.math.operator.MathOperator;
import me.sunmc.math.token.*;
import me.sunmc.math.tokenizer.Tokenizer;

import java.util.*;

/**
 * Converts an infix expression token stream into Reverse Polish Notation (RPN)
 * using Dijkstra's Shunting Yard algorithm.
 *
 * <p>This parser takes tokens produced by the {@link Tokenizer} and reorders
 * them into a postfix sequence suitable for stack-based evaluation. It handles
 * operator precedence, associativity, function calls, and parenthesized
 * sub-expressions.</p>
 *
 * <p>The implementation uses Java 21 pattern matching on the sealed
 * {@link Token} hierarchy for clean, exhaustive dispatch logic.</p>
 *
 * @author sun-dev
 * @since 1.0.0
 */
public final class ShuntingYardParser {

    private ShuntingYardParser() {
        throw new AssertionError("Utility class — do not instantiate");
    }

    /**
     * Converts an infix expression into RPN token order.
     *
     * @param expression             the expression string
     * @param userFunctions          user-defined functions
     * @param userOperators          user-defined operators
     * @param variableNames          declared variable names
     * @param implicitMultiplication whether to support implicit multiplication
     * @return an unmodifiable list of tokens in RPN order
     * @throws ParseException if the expression has syntax errors
     */
    public static List<Token> toRPN(String expression,
                                    Map<String, MathFunction> userFunctions,
                                    Map<String, MathOperator> userOperators,
                                    Set<String> variableNames,
                                    boolean implicitMultiplication) {

        Deque<Token> stack = new ArrayDeque<>();
        List<Token> output = new ArrayList<>();

        Tokenizer tokenizer = new Tokenizer(
                expression, userFunctions, userOperators,
                variableNames, implicitMultiplication);

        while (tokenizer.hasNext()) {
            Token token = tokenizer.nextToken();

            switch (token) {
                case NumberToken nt -> output.add(token);
                case VariableToken vt -> output.add(token);

                case OperatorToken(var op) -> {
                    while (!stack.isEmpty() && stack.peek() instanceof OperatorToken(var stackOp)) {
                        if (shouldPopOperator(op, stackOp)) {
                            output.add(stack.pop());
                        } else {
                            break;
                        }
                    }
                    stack.push(token);
                }

                case FunctionToken ft -> stack.push(token);

                case OpenParenToken() -> stack.push(token);

                case CloseParenToken() -> {
                    boolean foundOpen = false;
                    while (!stack.isEmpty()) {
                        Token top = stack.peek();
                        if (top instanceof OpenParenToken) {
                            stack.pop();
                            foundOpen = true;
                            break;
                        }
                        output.add(stack.pop());
                    }
                    if (!foundOpen) {
                        throw new ParseException("Mismatched parentheses: no matching opening parenthesis");
                    }
                    // If a function token is on top, pop it to output
                    if (!stack.isEmpty() && stack.peek() instanceof FunctionToken) {
                        output.add(stack.pop());
                    }
                }

                case SeparatorToken() -> {
                    boolean foundOpen = false;
                    while (!stack.isEmpty()) {
                        if (stack.peek() instanceof OpenParenToken) {
                            foundOpen = true;
                            break;
                        }
                        output.add(stack.pop());
                    }
                    if (!foundOpen) {
                        throw new ParseException(
                                "Misplaced function separator ',' or mismatched parentheses");
                    }
                }
            }
        }

        // Pop remaining operators from the stack
        while (!stack.isEmpty()) {
            Token top = stack.pop();
            if (top instanceof OpenParenToken || top instanceof CloseParenToken) {
                throw new ParseException("Mismatched parentheses detected in expression");
            }
            output.add(top);
        }

        return Collections.unmodifiableList(output);
    }

    /**
     * Determines whether the operator on the stack should be popped
     * before pushing the current operator.
     */
    private static boolean shouldPopOperator(MathOperator current, MathOperator onStack) {
        // Unary operators with right associativity should not pop binary operators
        if (current.operands() == 1 && onStack.operands() == 2) {
            return false;
        }
        if (current.isLeftAssociative()) {
            return current.precedence() <= onStack.precedence();
        }
        return current.precedence() < onStack.precedence();
    }
}
