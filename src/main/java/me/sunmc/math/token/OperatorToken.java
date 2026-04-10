package me.sunmc.math.token;

import me.sunmc.math.operator.MathOperator;

import java.util.Objects;

/**
 * A token representing a mathematical operator in an expression.
 *
 * @param operator the operator definition, must not be null
 * @author sun-dev
 * @since 1.0.0
 */
public record OperatorToken(MathOperator operator) implements Token {

    /**
     * Compact constructor validating the operator.
     */
    public OperatorToken {
        Objects.requireNonNull(operator, "Operator must not be null");
    }
}
