package me.sunmc.math.operator;

/**
 * Defines the associativity of a mathematical operator.
 *
 * <p>Associativity determines how operators of the same precedence are
 * grouped in the absence of parentheses:</p>
 * <ul>
 *   <li>{@link #LEFT} — evaluated left-to-right (e.g., {@code 5 - 3 - 1} = {@code (5 - 3) - 1})</li>
 *   <li>{@link #RIGHT} — evaluated right-to-left (e.g., {@code 2 ^ 3 ^ 2} = {@code 2 ^ (3 ^ 2)})</li>
 * </ul>
 *
 * @author sun-dev
 * @since 1.0.0
 */
public enum Associativity {

    /**
     * Left-to-right associativity. Most arithmetic operators use this.
     */
    LEFT,

    /**
     * Right-to-left associativity. The power operator typically uses this.
     */
    RIGHT
}
