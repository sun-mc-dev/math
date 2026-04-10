package me.sunmc.math.operator;

import java.util.Objects;
import java.util.Set;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

/**
 * An immutable definition of a mathematical operator.
 *
 * <p>Operators are defined by their symbol, number of operands (1 for unary,
 * 2 for binary), associativity, precedence, and the function that computes
 * the result.</p>
 *
 * <p>Factory methods are provided for convenient creation:</p>
 * <pre>{@code
 * // Binary operator
 * MathOperator plus = MathOperator.binary("+", Associativity.LEFT, Precedence.ADDITION,
 *     (a, b) -> a + b);
 *
 * // Unary operator
 * MathOperator negate = MathOperator.unary("-", x -> -x);
 * }</pre>
 *
 * @param symbol        the operator symbol (e.g., {@code "+"}, {@code "^"})
 * @param operands      the number of operands: 1 (unary) or 2 (binary)
 * @param associativity the associativity of the operator
 * @param precedence    the precedence level (higher = binds tighter)
 * @param function      the function that computes the operator's result
 * @author sun-dev
 * @since 1.0.0
 */
public record MathOperator(
        String symbol,
        int operands,
        Associativity associativity,
        int precedence,
        OperatorFunction function
) {

    /**
     * Characters allowed in custom operator symbols.
     */
    private static final Set<Character> ALLOWED_CHARS = Set.of(
            '+', '-', '*', '/', '%', '^', '!', '#', '§', '$',
            '&', ';', ':', '~', '<', '>', '|', '=',
            '÷', '√', '∛', '⌈', '⌊'
    );

    /**
     * Compact constructor with validation.
     */
    public MathOperator {
        Objects.requireNonNull(symbol, "Operator symbol must not be null");
        Objects.requireNonNull(associativity, "Associativity must not be null");
        Objects.requireNonNull(function, "Operator function must not be null");
        if (symbol.isBlank()) {
            throw new IllegalArgumentException("Operator symbol must not be blank");
        }
        if (operands < 1 || operands > 2) {
            throw new IllegalArgumentException(
                    "Operator must have 1 or 2 operands, got: %d".formatted(operands));
        }
        if (precedence < 0) {
            throw new IllegalArgumentException(
                    "Precedence must be non-negative, got: %d".formatted(precedence));
        }
    }

    /**
     * Creates a binary operator (2 operands).
     *
     * @param symbol        the operator symbol
     * @param associativity the associativity
     * @param precedence    the precedence level
     * @param fn            the binary computation function
     * @return a new {@code MathOperator}
     */
    public static MathOperator binary(String symbol, Associativity associativity,
                                      int precedence, DoubleBinaryOperator fn) {
        return new MathOperator(symbol, 2, associativity, precedence,
                args -> fn.applyAsDouble(args[0], args[1]));
    }

    /**
     * Creates a unary operator (1 operand) with right associativity and
     * {@link Precedence#UNARY_MINUS} precedence.
     *
     * @param symbol the operator symbol
     * @param fn     the unary computation function
     * @return a new {@code MathOperator}
     */
    public static MathOperator unary(String symbol, DoubleUnaryOperator fn) {
        return new MathOperator(symbol, 1, Associativity.RIGHT, Precedence.UNARY_MINUS,
                args -> fn.applyAsDouble(args[0]));
    }

    /**
     * Checks whether the given character is allowed in an operator symbol.
     *
     * @param ch the character to check
     * @return {@code true} if the character is a valid operator character
     */
    public static boolean isAllowedOperatorChar(char ch) {
        return ALLOWED_CHARS.contains(ch);
    }

    /**
     * Checks whether this operator is left-associative.
     *
     * @return {@code true} if left-associative
     */
    public boolean isLeftAssociative() {
        return associativity == Associativity.LEFT;
    }

    /**
     * Applies this operator to the given operands.
     *
     * @param args the operands
     * @return the computed result
     */
    public double apply(double... args) {
        return function.apply(args);
    }

    /**
     * A function that applies an operator to its operand(s).
     */
    @FunctionalInterface
    public interface OperatorFunction {
        /**
         * Applies this operator to the given operands.
         *
         * @param args the operands (1 for unary, 2 for binary)
         * @return the computed result
         */
        double apply(double... args);
    }
}
