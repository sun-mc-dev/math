package me.sunmc.math.token;

/**
 * A token representing a numeric literal value in an expression.
 *
 * @param value the numeric value of this token
 * @author sun-dev
 * @since 1.0.0
 */
public record NumberToken(double value) implements Token {
}
