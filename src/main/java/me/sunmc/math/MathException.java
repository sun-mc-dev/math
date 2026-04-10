package me.sunmc.math;

/**
 * Base exception for all errors in the SunMC Math library.
 *
 * <p>This is a {@link RuntimeException} — callers are not forced to catch it,
 * but specific subclasses ({@link ParseException}, {@link EvaluationException})
 * provide more granular error information.</p>
 *
 * @author sun-dev
 * @since 1.0.0
 */
public class MathException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new MathException with the given message.
     *
     * @param message the detail message
     */
    public MathException(String message) {
        super(message);
    }

    /**
     * Creates a new MathException with the given message and cause.
     *
     * @param message the detail message
     * @param cause   the underlying cause
     */
    public MathException(String message, Throwable cause) {
        super(message, cause);
    }
}
