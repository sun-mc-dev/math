package me.sunmc.math.function;

import java.util.Objects;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

/**
 * An immutable definition of a mathematical function.
 *
 * <p>Functions are defined by their name, the number of arguments they
 * accept, and the body that computes the result. Factory methods are
 * provided for convenient creation of common function arities.</p>
 *
 * <p>Example:</p>
 * <pre>{@code
 * // Single-argument function
 * MathFunction myAbs = MathFunction.of("myabs", Math::abs);
 *
 * // Two-argument function
 * MathFunction logBase = MathFunction.of("logb", (value, base) ->
 *     Math.log(value) / Math.log(base));
 *
 * // Multi-argument function
 * MathFunction avg = MathFunction.of("avg", 4, args -> {
 *     double sum = 0;
 *     for (double arg : args) sum += arg;
 *     return sum / args.length;
 * });
 * }</pre>
 *
 * @param name         the function name (must start with a letter or underscore)
 * @param numArguments the number of arguments (0 or more; -1 indicates variadic)
 * @param body         the function body that computes the result
 * @author sun-dev
 * @since 1.0.0
 */
public record MathFunction(
        String name,
        int numArguments,
        FunctionBody body
) {

    /**
     * Compact constructor with validation.
     */
    public MathFunction {
        Objects.requireNonNull(name, "Function name must not be null");
        Objects.requireNonNull(body, "Function body must not be null");
        if (!isValidFunctionName(name)) {
            throw new IllegalArgumentException(
                    "Invalid function name: '%s'. Names must start with a letter or underscore "
                            .formatted(name)
                            + "and contain only letters, digits, or underscores.");
        }
        if (numArguments < -1) {
            throw new IllegalArgumentException(
                    "Number of arguments must be >= -1 (use -1 for variadic), got: %d"
                            .formatted(numArguments));
        }
    }

    /**
     * Creates a single-argument function.
     *
     * @param name the function name
     * @param fn   the unary computation
     * @return a new {@code MathFunction}
     */
    public static MathFunction of(String name, DoubleUnaryOperator fn) {
        return new MathFunction(name, 1, args -> fn.applyAsDouble(args[0]));
    }

    /**
     * Creates a two-argument function.
     *
     * @param name the function name
     * @param fn   the binary computation
     * @return a new {@code MathFunction}
     */
    public static MathFunction of(String name, DoubleBinaryOperator fn) {
        return new MathFunction(name, 2, args -> fn.applyAsDouble(args[0], args[1]));
    }

    /**
     * Creates a function with the specified number of arguments.
     *
     * @param name         the function name
     * @param numArguments the number of arguments (-1 for variadic)
     * @param body         the function body
     * @return a new {@code MathFunction}
     */
    public static MathFunction of(String name, int numArguments, FunctionBody body) {
        return new MathFunction(name, numArguments, body);
    }

    /**
     * Checks whether the given name is a valid function name.
     *
     * <p>A valid name starts with a letter or underscore, followed by
     * zero or more letters, digits, or underscores.</p>
     *
     * @param name the name to validate
     * @return {@code true} if valid
     */
    public static boolean isValidFunctionName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        char first = name.charAt(0);
        if (!Character.isLetter(first) && first != '_') {
            return false;
        }
        for (int i = 1; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!Character.isLetter(c) && !Character.isDigit(c) && c != '_') {
                return false;
            }
        }
        return true;
    }

    /**
     * Applies this function to the given arguments.
     *
     * @param args the function arguments
     * @return the computed result
     */
    public double apply(double... args) {
        return body.apply(args);
    }

    /**
     * A function body that computes a result from the given arguments.
     */
    @FunctionalInterface
    public interface FunctionBody {
        /**
         * Applies this function to the given arguments.
         *
         * @param args the function arguments
         * @return the computed result
         */
        double apply(double... args);
    }
}
