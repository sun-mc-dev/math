/**
 * SunMC Math — A modern Java 21+ mathematical expression evaluation library.
 *
 * <p>Provides a fluent API for parsing, validating, and evaluating mathematical
 * expressions using Dijkstra's Shunting Yard algorithm. Built with sealed types,
 * records, and pattern matching for type-safe, clean expression handling.</p>
 *
 * @author sun-dev
 * @see <a href="https://sunmc.in">sunmc.in</a>
 */
module me.sunmc.math {
    exports me.sunmc.math;
    exports me.sunmc.math.function;
    exports me.sunmc.math.operator;
    exports me.sunmc.math.constant;
    exports me.sunmc.math.token;
}
