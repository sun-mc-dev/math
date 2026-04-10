package me.sunmc.math.function;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Registry of 35+ built-in mathematical functions.
 *
 * <p>All standard mathematical functions are registered here, organized
 * by category. The registry is immutable and thread-safe.</p>
 *
 * <h2>Categories</h2>
 * <ul>
 *   <li><b>Trigonometric</b>: sin, cos, tan, cot, sec, csc</li>
 *   <li><b>Inverse trigonometric</b>: asin, acos, atan, atan2</li>
 *   <li><b>Hyperbolic</b>: sinh, cosh, tanh, coth, sech, csch</li>
 *   <li><b>Inverse hyperbolic</b>: asinh, acosh, atanh</li>
 *   <li><b>Exponential/Logarithmic</b>: exp, expm1, log, log2, log10, log1p, logb</li>
 *   <li><b>Power/Root</b>: pow, sqrt, cbrt</li>
 *   <li><b>Rounding</b>: ceil, floor, round, rint</li>
 *   <li><b>Sign/Absolute</b>: abs, signum</li>
 *   <li><b>Conversion</b>: toradian, todegree</li>
 *   <li><b>Min/Max</b>: min, max</li>
 *   <li><b>Clamping</b>: clamp</li>
 *   <li><b>Combinatorics</b>: factorial, gcd, lcm</li>
 *   <li><b>Miscellaneous</b>: hypot, fma</li>
 * </ul>
 *
 * @author sun-dev
 * @since 1.0.0
 */
public final class BuiltInFunctions {

    /**
     * Immutable map of all built-in functions, keyed by name.
     */
    private static final Map<String, MathFunction> FUNCTIONS;

    static {
        Map<String, MathFunction> map = new LinkedHashMap<>(48);

        map.put("sin", MathFunction.of("sin", Math::sin));
        map.put("cos", MathFunction.of("cos", Math::cos));
        map.put("tan", MathFunction.of("tan", Math::tan));
        map.put("cot", MathFunction.of("cot", x -> {
            double t = Math.tan(x);
            if (t == 0.0) throw new ArithmeticException("Division by zero in cotangent");
            return 1.0 / t;
        }));
        map.put("sec", MathFunction.of("sec", x -> {
            double c = Math.cos(x);
            if (c == 0.0) throw new ArithmeticException("Division by zero in secant");
            return 1.0 / c;
        }));
        map.put("csc", MathFunction.of("csc", x -> {
            double s = Math.sin(x);
            if (s == 0.0) throw new ArithmeticException("Division by zero in cosecant");
            return 1.0 / s;
        }));

        map.put("asin", MathFunction.of("asin", Math::asin));
        map.put("acos", MathFunction.of("acos", Math::acos));
        map.put("atan", MathFunction.of("atan", Math::atan));
        map.put("atan2", MathFunction.of("atan2", Math::atan2));

        map.put("sinh", MathFunction.of("sinh", Math::sinh));
        map.put("cosh", MathFunction.of("cosh", Math::cosh));
        map.put("tanh", MathFunction.of("tanh", Math::tanh));
        map.put("coth", MathFunction.of("coth", x -> {
            double sh = Math.sinh(x);
            if (sh == 0.0) throw new ArithmeticException("Division by zero in coth");
            return Math.cosh(x) / sh;
        }));
        map.put("sech", MathFunction.of("sech", x -> 1.0 / Math.cosh(x)));
        map.put("csch", MathFunction.of("csch", x -> {
            double sh = Math.sinh(x);
            if (sh == 0.0) throw new ArithmeticException("Division by zero in csch");
            return 1.0 / sh;
        }));

        map.put("asinh", MathFunction.of("asinh", x -> Math.log(x + Math.sqrt(x * x + 1))));
        map.put("acosh", MathFunction.of("acosh", x -> Math.log(x + Math.sqrt(x * x - 1))));
        map.put("atanh", MathFunction.of("atanh", x -> 0.5 * Math.log((1 + x) / (1 - x))));

        map.put("exp", MathFunction.of("exp", Math::exp));
        map.put("expm1", MathFunction.of("expm1", Math::expm1));
        map.put("log", MathFunction.of("log", Math::log));
        map.put("log2", MathFunction.of("log2", x -> Math.log(x) / Math.log(2.0)));
        map.put("log10", MathFunction.of("log10", Math::log10));
        map.put("log1p", MathFunction.of("log1p", Math::log1p));
        map.put("logb", MathFunction.of("logb", (value, base) -> Math.log(value) / Math.log(base)));

        map.put("pow", MathFunction.of("pow", Math::pow));
        map.put("sqrt", MathFunction.of("sqrt", Math::sqrt));
        map.put("cbrt", MathFunction.of("cbrt", Math::cbrt));

        map.put("ceil", MathFunction.of("ceil", Math::ceil));
        map.put("floor", MathFunction.of("floor", Math::floor));
        map.put("round", MathFunction.of("round", x -> (double) Math.round(x)));
        map.put("rint", MathFunction.of("rint", Math::rint));

        map.put("abs", MathFunction.of("abs", Math::abs));
        map.put("signum", MathFunction.of("signum", Math::signum));

        map.put("toradian", MathFunction.of("toradian", Math::toRadians));
        map.put("todegree", MathFunction.of("todegree", Math::toDegrees));

        map.put("min", MathFunction.of("min", Math::min));
        map.put("max", MathFunction.of("max", Math::max));

        map.put("clamp", MathFunction.of("clamp", 3, args -> Math.clamp(args[0], args[1], args[2])));

        map.put("factorial", MathFunction.of("factorial", x -> {
            int n = (int) x;
            if (n != x) throw new IllegalArgumentException("factorial requires an integer argument");
            if (n < 0) throw new IllegalArgumentException("factorial requires a non-negative argument");
            double result = 1;
            for (int i = 2; i <= n; i++) result *= i;
            return result;
        }));
        map.put("gcd", MathFunction.of("gcd", (a, b) -> {
            long la = Math.abs((long) a);
            long lb = Math.abs((long) b);
            while (lb != 0) {
                long temp = lb;
                lb = la % lb;
                la = temp;
            }
            return la;
        }));
        map.put("lcm", MathFunction.of("lcm", (a, b) -> {
            long la = Math.abs((long) a);
            long lb = Math.abs((long) b);
            if (la == 0 || lb == 0) return 0.0;
            long g = la;
            long temp = lb;
            while (temp != 0) {
                long t = temp;
                temp = g % temp;
                g = t;
            }
            return (double) (la / g * lb);
        }));

        map.put("hypot", MathFunction.of("hypot", Math::hypot));
        map.put("fma", MathFunction.of("fma", 3, args -> Math.fma(args[0], args[1], args[2])));

        FUNCTIONS = Collections.unmodifiableMap(map);
    }

    private BuiltInFunctions() {
        throw new AssertionError("Utility class — do not instantiate");
    }

    /**
     * Retrieves a built-in function by name.
     *
     * @param name the function name (case-sensitive)
     * @return an {@link Optional} containing the function, or empty if not found
     */
    public static Optional<MathFunction> get(String name) {
        return Optional.ofNullable(FUNCTIONS.get(name));
    }

    /**
     * Returns an unmodifiable view of all built-in functions.
     *
     * @return map of function name to function definition
     */
    public static Map<String, MathFunction> getAll() {
        return FUNCTIONS;
    }

    /**
     * Checks whether a built-in function with the given name exists.
     *
     * @param name the function name
     * @return {@code true} if a built-in function with that name exists
     */
    public static boolean contains(String name) {
        return FUNCTIONS.containsKey(name);
    }
}
