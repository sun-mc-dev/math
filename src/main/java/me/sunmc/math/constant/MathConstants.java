package me.sunmc.math.constant;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registry of built-in mathematical constants.
 *
 * <p>These constants are automatically available in every expression
 * without explicit variable declaration. They are bound as default
 * variable values during expression construction.</p>
 *
 * <table>
 *   <caption>Built-in Constants</caption>
 *   <tr><th>Name(s)</th><th>Value</th><th>Description</th></tr>
 *   <tr><td>{@code pi}, {@code π}</td><td>3.14159265…</td><td>Ratio of circumference to diameter</td></tr>
 *   <tr><td>{@code e}</td><td>2.71828182…</td><td>Euler's number</td></tr>
 *   <tr><td>{@code tau}, {@code τ}</td><td>6.28318530…</td><td>2π — full turn in radians</td></tr>
 *   <tr><td>{@code phi}, {@code φ}</td><td>1.61803398…</td><td>Golden ratio</td></tr>
 *   <tr><td>{@code gamma}, {@code γ}</td><td>0.57721566…</td><td>Euler–Mascheroni constant</td></tr>
 *   <tr><td>{@code sqrt2}</td><td>1.41421356…</td><td>Square root of 2</td></tr>
 *   <tr><td>{@code inf}, {@code ∞}</td><td>+∞</td><td>Positive infinity</td></tr>
 *   <tr><td>{@code nan}</td><td>NaN</td><td>Not-a-Number</td></tr>
 * </table>
 *
 * @author sun-dev
 * @since 1.0.0
 */
public final class MathConstants {

    /**
     * The ratio of a circle's circumference to its diameter.
     */
    public static final double PI = Math.PI;
    /**
     * Euler's number, the base of the natural logarithm.
     */
    public static final double E = Math.E;
    /**
     * Tau (τ) = 2π, the full turn in radians.
     */
    public static final double TAU = 2.0 * Math.PI;
    /**
     * The golden ratio φ = (1 + √5) / 2.
     */
    public static final double PHI = 1.6180339887498948482;
    /**
     * The Euler–Mascheroni constant γ.
     */
    public static final double GAMMA = 0.5772156649015328606;
    /**
     * The square root of 2.
     */
    public static final double SQRT2 = Math.sqrt(2.0);
    /**
     * Immutable map of all constants, keyed by name.
     */
    private static final Map<String, Double> CONSTANTS;

    static {
        Map<String, Double> map = new LinkedHashMap<>(16);
        map.put("pi", PI);
        map.put("π", PI);
        map.put("e", E);
        map.put("tau", TAU);
        map.put("τ", TAU);
        map.put("phi", PHI);
        map.put("φ", PHI);
        map.put("gamma", GAMMA);
        map.put("γ", GAMMA);
        map.put("sqrt2", SQRT2);
        map.put("inf", Double.POSITIVE_INFINITY);
        map.put("∞", Double.POSITIVE_INFINITY);
        map.put("nan", Double.NaN);
        CONSTANTS = Collections.unmodifiableMap(map);
    }

    private MathConstants() {
        throw new AssertionError("Utility class — do not instantiate");
    }

    /**
     * Returns an unmodifiable map of all built-in constants.
     *
     * @return map of constant name to value
     */
    public static Map<String, Double> getAll() {
        return CONSTANTS;
    }

    /**
     * Returns a mutable copy of all constants, suitable for use as default
     * variable bindings in expression evaluation.
     *
     * @return mutable map of constant name to value
     */
    public static Map<String, Double> asVariableMap() {
        return new LinkedHashMap<>(CONSTANTS);
    }

    /**
     * Returns the set of all constant names.
     *
     * @return unmodifiable set of constant names
     */
    public static java.util.Set<String> names() {
        return CONSTANTS.keySet();
    }
}
