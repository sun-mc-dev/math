package me.sunmc.math.operator;

import java.util.Optional;

/**
 * Registry of built-in mathematical operators.
 *
 * <p>Provides the standard arithmetic operators: addition, subtraction,
 * multiplication, division, modulo, exponentiation, and unary plus/minus.</p>
 *
 * @author sun-dev
 * @since 1.0.0
 */
public final class BuiltInOperators {

    /**
     * Addition operator: {@code a + b}
     */
    public static final MathOperator ADDITION = MathOperator.binary(
            "+", Associativity.LEFT, Precedence.ADDITION, Double::sum);

    /**
     * Subtraction operator: {@code a - b}
     */
    public static final MathOperator SUBTRACTION = MathOperator.binary(
            "-", Associativity.LEFT, Precedence.SUBTRACTION, (a, b) -> a - b);

    /**
     * Multiplication operator: {@code a * b}
     */
    public static final MathOperator MULTIPLICATION = MathOperator.binary(
            "*", Associativity.LEFT, Precedence.MULTIPLICATION, (a, b) -> a * b);

    /**
     * Division operator: {@code a / b} (throws {@link ArithmeticException} on division by zero)
     */
    public static final MathOperator DIVISION = MathOperator.binary(
            "/", Associativity.LEFT, Precedence.DIVISION, (a, b) -> {
                if (b == 0.0) {
                    throw new ArithmeticException("Division by zero");
                }
                return a / b;
            });

    /**
     * Modulo operator: {@code a % b} (throws {@link ArithmeticException} on division by zero)
     */
    public static final MathOperator MODULO = MathOperator.binary(
            "%", Associativity.LEFT, Precedence.MODULO, (a, b) -> {
                if (b == 0.0) {
                    throw new ArithmeticException("Division by zero in modulo");
                }
                return a % b;
            });

    /**
     * Power/exponentiation operator: {@code a ^ b} (right-associative)
     */
    public static final MathOperator POWER = MathOperator.binary(
            "^", Associativity.RIGHT, Precedence.POWER, Math::pow);

    /**
     * Unary minus operator: {@code -x}
     */
    public static final MathOperator UNARY_MINUS = new MathOperator(
            "-", 1, Associativity.RIGHT, Precedence.UNARY_MINUS, args -> -args[0]);

    /**
     * Unary plus operator: {@code +x}
     */
    public static final MathOperator UNARY_PLUS = new MathOperator(
            "+", 1, Associativity.RIGHT, Precedence.UNARY_PLUS, args -> args[0]);

    private BuiltInOperators() {
        throw new AssertionError("Utility class — do not instantiate");
    }

    /**
     * Retrieves a built-in operator by its symbol character and expected
     * number of operands.
     *
     * @param symbol      the operator symbol character
     * @param numOperands the number of operands (1 for unary, 2 for binary)
     * @return an {@link Optional} containing the operator, or empty if not found
     */
    public static Optional<MathOperator> get(char symbol, int numOperands) {
        return Optional.ofNullable(switch (symbol) {
            case '+' -> numOperands == 1 ? UNARY_PLUS : ADDITION;
            case '-' -> numOperands == 1 ? UNARY_MINUS : SUBTRACTION;
            case '*' -> MULTIPLICATION;
            case '/', '÷' -> DIVISION;
            case '%' -> MODULO;
            case '^' -> POWER;
            default -> null;
        });
    }
}
