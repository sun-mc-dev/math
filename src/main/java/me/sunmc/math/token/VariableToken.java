package me.sunmc.math.token;

import java.util.Objects;

/**
 * A token representing a named variable in an expression.
 *
 * @param name the variable name, must not be null or blank
 * @author sun-dev
 * @since 1.0.0
 */
public record VariableToken(String name) implements Token {

    /**
     * Compact constructor validating the variable name.
     */
    public VariableToken {
        Objects.requireNonNull(name, "Variable name must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Variable name must not be blank");
        }
    }
}
