package me.sunmc.math.operator;

/**
 * Standard precedence levels for mathematical operators.
 *
 * <p>Higher values indicate higher precedence (evaluated first).
 * These constants follow standard mathematical conventions and
 * provide well-defined anchor points for custom operator definitions.</p>
 *
 * <p>Precedence hierarchy (lowest to highest):</p>
 * <ol>
 *   <li>{@link #ADDITION} / {@link #SUBTRACTION} (500)</li>
 *   <li>{@link #MULTIPLICATION} / {@link #DIVISION} / {@link #MODULO} (1000)</li>
 *   <li>{@link #UNARY_MINUS} / {@link #UNARY_PLUS} (5000)</li>
 *   <li>{@link #POWER} (10000)</li>
 * </ol>
 *
 * @author sun-dev
 * @since 1.0.0
 */
public final class Precedence {

    /**
     * Precedence for addition ({@code +}). Value: 500
     */
    public static final int ADDITION = 500;
    /**
     * Precedence for subtraction ({@code -}). Value: 500
     */
    public static final int SUBTRACTION = 500;
    /**
     * Precedence for multiplication ({@code *}). Value: 1000
     */
    public static final int MULTIPLICATION = 1000;
    /**
     * Precedence for division ({@code /}). Value: 1000
     */
    public static final int DIVISION = 1000;
    /**
     * Precedence for modulo ({@code %}). Value: 1000
     */
    public static final int MODULO = 1000;
    /**
     * Precedence for unary minus ({@code -x}). Value: 5000
     */
    public static final int UNARY_MINUS = 5000;
    /**
     * Precedence for unary plus ({@code +x}). Value: 5000
     */
    public static final int UNARY_PLUS = 5000;
    /**
     * Precedence for exponentiation ({@code ^}). Value: 10000
     */
    public static final int POWER = 10000;

    private Precedence() {
        throw new AssertionError("Utility class — do not instantiate");
    }
}
