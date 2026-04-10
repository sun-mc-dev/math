package me.sunmc.math.token;

import me.sunmc.math.function.MathFunction;

import java.util.Objects;

/**
 * A token representing a mathematical function call in an expression.
 *
 * @param function the function definition, must not be null
 * @author sun-dev
 * @since 1.0.0
 */
public record FunctionToken(MathFunction function) implements Token {

    /**
     * Compact constructor validating the function.
     */
    public FunctionToken {
        Objects.requireNonNull(function, "Function must not be null");
    }
}
