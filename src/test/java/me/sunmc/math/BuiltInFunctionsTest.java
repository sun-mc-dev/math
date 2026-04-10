package me.sunmc.math;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for all built-in mathematical functions.
 *
 * @author sun-dev
 */
@DisplayName("Built-in Functions")
class BuiltInFunctionsTest {

    private static final double EPSILON = 1e-10;

    private double eval(String expr) {
        return new ExpressionBuilder(expr).build().evaluate();
    }

    @Nested
    @DisplayName("Trigonometric")
    class Trig {

        @Test
        void sin() {
            assertEquals(Math.sin(1.0), eval("sin(1)"), EPSILON);
        }

        @Test
        void cos() {
            assertEquals(Math.cos(1.0), eval("cos(1)"), EPSILON);
        }

        @Test
        void tan() {
            assertEquals(Math.tan(1.0), eval("tan(1)"), EPSILON);
        }

        @Test
        void cot() {
            assertEquals(1.0 / Math.tan(1.0), eval("cot(1)"), EPSILON);
        }

        @Test
        void sec() {
            assertEquals(1.0 / Math.cos(1.0), eval("sec(1)"), EPSILON);
        }

        @Test
        void csc() {
            assertEquals(1.0 / Math.sin(1.0), eval("csc(1)"), EPSILON);
        }
    }

    @Nested
    @DisplayName("Inverse Trigonometric")
    class InverseTrig {

        @Test
        void asin() {
            assertEquals(Math.asin(0.5), eval("asin(0.5)"), EPSILON);
        }

        @Test
        void acos() {
            assertEquals(Math.acos(0.5), eval("acos(0.5)"), EPSILON);
        }

        @Test
        void atan() {
            assertEquals(Math.atan(1.0), eval("atan(1)"), EPSILON);
        }

        @Test
        void atan2() {
            Expression expr = new ExpressionBuilder("atan2(1, 1)").build();
            assertEquals(Math.atan2(1, 1), expr.evaluate(), EPSILON);
        }
    }

    @Nested
    @DisplayName("Hyperbolic")
    class Hyperbolic {

        @Test
        void sinh() {
            assertEquals(Math.sinh(1.0), eval("sinh(1)"), EPSILON);
        }

        @Test
        void cosh() {
            assertEquals(Math.cosh(1.0), eval("cosh(1)"), EPSILON);
        }

        @Test
        void tanh() {
            assertEquals(Math.tanh(1.0), eval("tanh(1)"), EPSILON);
        }

        @Test
        void sech() {
            assertEquals(1.0 / Math.cosh(1.0), eval("sech(1)"), EPSILON);
        }

        @Test
        void coth() {
            assertEquals(Math.cosh(1.0) / Math.sinh(1.0), eval("coth(1)"), EPSILON);
        }

        @Test
        void csch() {
            assertEquals(1.0 / Math.sinh(1.0), eval("csch(1)"), EPSILON);
        }
    }

    @Nested
    @DisplayName("Inverse Hyperbolic")
    class InverseHyperbolic {

        @Test
        void asinh() {
            double x = 1.0;
            assertEquals(Math.log(x + Math.sqrt(x * x + 1)), eval("asinh(1)"), EPSILON);
        }

        @Test
        void acosh() {
            double x = 2.0;
            assertEquals(Math.log(x + Math.sqrt(x * x - 1)), eval("acosh(2)"), EPSILON);
        }

        @Test
        void atanh() {
            double x = 0.5;
            assertEquals(0.5 * Math.log((1 + x) / (1 - x)), eval("atanh(0.5)"), EPSILON);
        }
    }

    @Nested
    @DisplayName("Exponential and Logarithmic")
    class ExpLog {

        @Test
        void exp() {
            assertEquals(Math.exp(2.0), eval("exp(2)"), EPSILON);
        }

        @Test
        void expm1() {
            assertEquals(Math.expm1(1.0), eval("expm1(1)"), EPSILON);
        }

        @Test
        void log() {
            assertEquals(Math.log(10.0), eval("log(10)"), EPSILON);
        }

        @Test
        void log2() {
            assertEquals(Math.log(8.0) / Math.log(2.0), eval("log2(8)"), EPSILON);
        }

        @Test
        void log10() {
            assertEquals(Math.log10(100.0), eval("log10(100)"), EPSILON);
        }

        @Test
        void log1p() {
            assertEquals(Math.log1p(1.0), eval("log1p(1)"), EPSILON);
        }

        @Test
        void logb() {
            Expression expr = new ExpressionBuilder("logb(8, 2)").build();
            assertEquals(3.0, expr.evaluate(), EPSILON);
        }
    }

    @Nested
    @DisplayName("Power and Root")
    class PowerRoot {

        @Test
        void pow() {
            Expression expr = new ExpressionBuilder("pow(2, 10)").build();
            assertEquals(1024.0, expr.evaluate(), EPSILON);
        }

        @Test
        void sqrt() {
            assertEquals(Math.sqrt(144.0), eval("sqrt(144)"), EPSILON);
        }

        @Test
        void cbrt() {
            assertEquals(Math.cbrt(27.0), eval("cbrt(27)"), EPSILON);
        }
    }

    @Nested
    @DisplayName("Rounding")
    class Rounding {

        @Test
        void ceil() {
            assertEquals(4.0, eval("ceil(3.2)"), EPSILON);
        }

        @Test
        void floor() {
            assertEquals(3.0, eval("floor(3.8)"), EPSILON);
        }

        @Test
        void round() {
            assertEquals(4.0, eval("round(3.6)"), EPSILON);
        }

        @Test
        void rint() {
            assertEquals(4.0, eval("rint(3.5)"), EPSILON);
        }
    }

    @Nested
    @DisplayName("Sign and Absolute")
    class SignAbsolute {

        @Test
        void abs() {
            assertEquals(5.0, eval("abs(-5)"), EPSILON);
        }

        @Test
        void signum_pos() {
            assertEquals(1.0, eval("signum(42)"), EPSILON);
        }

        @Test
        void signum_neg() {
            assertEquals(-1.0, eval("signum(-42)"), EPSILON);
        }

        @Test
        void signum_zero() {
            assertEquals(0.0, eval("signum(0)"), EPSILON);
        }
    }

    @Nested
    @DisplayName("Conversion")
    class Conversion {

        @Test
        void toradian() {
            assertEquals(Math.toRadians(180), eval("toradian(180)"), EPSILON);
        }

        @Test
        void todegree() {
            assertEquals(Math.toDegrees(Math.PI), eval("todegree(pi)"), EPSILON);
        }
    }

    @Nested
    @DisplayName("Min and Max")
    class MinMax {

        @Test
        void min() {
            Expression expr = new ExpressionBuilder("min(3, 7)").build();
            assertEquals(3.0, expr.evaluate(), EPSILON);
        }

        @Test
        void max() {
            Expression expr = new ExpressionBuilder("max(3, 7)").build();
            assertEquals(7.0, expr.evaluate(), EPSILON);
        }
    }

    @Nested
    @DisplayName("Clamp")
    class Clamp {

        @Test
        void clampInRange() {
            Expression expr = new ExpressionBuilder("clamp(5, 0, 10)").build();
            assertEquals(5.0, expr.evaluate(), EPSILON);
        }

        @Test
        void clampBelowMin() {
            Expression expr = new ExpressionBuilder("clamp(-5, 0, 10)").build();
            assertEquals(0.0, expr.evaluate(), EPSILON);
        }

        @Test
        void clampAboveMax() {
            Expression expr = new ExpressionBuilder("clamp(15, 0, 10)").build();
            assertEquals(10.0, expr.evaluate(), EPSILON);
        }
    }

    @Nested
    @DisplayName("Combinatorics")
    class Combinatorics {

        @Test
        void factorial() {
            assertEquals(120.0, eval("factorial(5)"), EPSILON);
        }

        @Test
        void factorialZero() {
            assertEquals(1.0, eval("factorial(0)"), EPSILON);
        }

        @Test
        void gcd() {
            Expression expr = new ExpressionBuilder("gcd(12, 8)").build();
            assertEquals(4.0, expr.evaluate(), EPSILON);
        }

        @Test
        void lcm() {
            Expression expr = new ExpressionBuilder("lcm(4, 6)").build();
            assertEquals(12.0, expr.evaluate(), EPSILON);
        }
    }

    @Nested
    @DisplayName("Miscellaneous")
    class Misc {

        @Test
        void hypot() {
            Expression expr = new ExpressionBuilder("hypot(3, 4)").build();
            assertEquals(5.0, expr.evaluate(), EPSILON);
        }

        @Test
        void fma() {
            Expression expr = new ExpressionBuilder("fma(2, 3, 4)").build();
            assertEquals(10.0, expr.evaluate(), EPSILON);
        }
    }
}
