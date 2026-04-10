package me.sunmc.math;

/**
 * Exception thrown when an expression cannot be parsed.
 *
 * <p>Provides the original expression string and the character position
 * where the error was detected, enabling precise error reporting.</p>
 *
 * @author sun-dev
 * @since 1.0.0
 */
public class ParseException extends MathException {

    private static final long serialVersionUID = 1L;

    private final String expression;
    private final int position;

    /**
     * Creates a ParseException with a message, the expression, and position.
     *
     * @param message    the detail message
     * @param expression the expression being parsed
     * @param position   the character position where the error occurred (0-based)
     */
    public ParseException(String message, String expression, int position) {
        super("%s at position %d in expression: %s".formatted(message, position, expression));
        this.expression = expression;
        this.position = position;
    }

    /**
     * Creates a ParseException with just a message.
     *
     * @param message the detail message
     */
    public ParseException(String message) {
        super(message);
        this.expression = "";
        this.position = -1;
    }

    /**
     * Returns the expression that failed to parse.
     *
     * @return the original expression string
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Returns the character position where the parse error occurred.
     *
     * @return the 0-based position, or -1 if not applicable
     */
    public int getPosition() {
        return position;
    }
}
