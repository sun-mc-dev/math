package me.sunmc.math;

import java.util.List;
import java.util.Objects;

/**
 * The result of validating an expression.
 *
 * <p>Contains a validity flag and, if invalid, a list of human-readable
 * error descriptions explaining why validation failed.</p>
 *
 * <p>A shared {@link #SUCCESS} instance is used for valid expressions
 * to avoid unnecessary object allocation.</p>
 *
 * @param valid  whether the expression is valid
 * @param errors the list of validation error messages (empty if valid)
 * @author sun-dev
 * @since 1.0.0
 */
public record ValidationResult(boolean valid, List<String> errors) {

    /**
     * A shared instance representing a successful validation.
     */
    public static final ValidationResult SUCCESS = new ValidationResult(true, List.of());

    /**
     * Compact constructor ensuring immutability of the errors list.
     */
    public ValidationResult {
        Objects.requireNonNull(errors, "Errors list must not be null");
        errors = List.copyOf(errors);
    }

    /**
     * Creates a failed validation result with the given errors.
     *
     * @param errors the validation error messages
     * @return a new failed {@code ValidationResult}
     */
    public static ValidationResult failure(List<String> errors) {
        return new ValidationResult(false, errors);
    }

    /**
     * Creates a failed validation result with a single error.
     *
     * @param error the validation error message
     * @return a new failed {@code ValidationResult}
     */
    public static ValidationResult failure(String error) {
        return new ValidationResult(false, List.of(error));
    }

    /**
     * Returns whether the expression is valid.
     *
     * @return {@code true} if the expression passed validation
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Returns the list of validation error messages.
     *
     * @return an unmodifiable list of error descriptions (empty if valid)
     */
    public List<String> getErrors() {
        return errors;
    }
}
