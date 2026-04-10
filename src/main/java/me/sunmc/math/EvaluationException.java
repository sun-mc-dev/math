package me.sunmc.math;

/**
 * Exception thrown when an expression cannot be evaluated at runtime.
 *
 * <p>Common causes include missing variable values, division by zero,
 * and invalid function arguments.</p>
 *
 * @author sun-dev
 * @since 1.0.0
 */
public class EvaluationException extends MathException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an EvaluationException with the given message.
     *
     * @param message the detail message
     */
    public EvaluationException(String message) {
        super(message);
    }

    /**
     * Creates an EvaluationException with the given message and cause.
     *
     * @param message the detail message
     * @param cause   the underlying cause
     */
    public EvaluationException(String message, Throwable cause) {
        super(message, cause);
    }
}
