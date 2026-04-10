package me.sunmc.math;

import me.sunmc.math.function.MathFunction;
import me.sunmc.math.operator.Associativity;
import me.sunmc.math.operator.MathOperator;
import me.sunmc.math.operator.Precedence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for the ExpressionBuilder API.
 *
 * @author sun-dev
 */
@DisplayName("ExpressionBuilder")
class ExpressionBuilderTest {

    @Test
    @DisplayName("null expression throws IllegalArgumentException")
    void nullExpression() {
        assertThrows(IllegalArgumentException.class, () -> new ExpressionBuilder(null));
    }

    @Test
    @DisplayName("empty expression throws IllegalArgumentException")
    void emptyExpression() {
        assertThrows(IllegalArgumentException.class, () -> new ExpressionBuilder(""));
    }

    @Test
    @DisplayName("blank expression throws IllegalArgumentException")
    void blankExpression() {
        assertThrows(IllegalArgumentException.class, () -> new ExpressionBuilder("   "));
    }

    @Test
    @DisplayName("variable name conflicting with function throws")
    void variableConflictsWithFunction() {
        assertThrows(IllegalArgumentException.class, () ->
                new ExpressionBuilder("sin + 1")
                        .variable("sin")
                        .build());
    }

    @Test
    @DisplayName("fluent builder chain")
    void fluentChain() {
        Expression expr = new ExpressionBuilder("x + y")
                .variable("x")
                .variable("y")
                .implicitMultiplication(true)
                .build()
                .setVariable("x", 1)
                .setVariable("y", 2);
        assertEquals(3.0, expr.evaluate(), 1e-10);
    }

    @Test
    @DisplayName("operator with invalid symbol throws")
    void invalidOperatorSymbol() {
        MathOperator bad = new MathOperator("abc", 2, Associativity.LEFT, 500,
                args -> args[0] + args[1]);
        assertThrows(IllegalArgumentException.class, () ->
                new ExpressionBuilder("1 abc 2").operator(bad));
    }

    @Nested
    @DisplayName("Custom Functions")
    class CustomFuncTests {

        @Test
        @DisplayName("custom single-arg function")
        void customSingleArg() {
            MathFunction twice = MathFunction.of("twice", x -> x * 2);
            double result = new ExpressionBuilder("twice(5)")
                    .function(twice)
                    .build()
                    .evaluate();
            assertEquals(10.0, result, 1e-10);
        }

        @Test
        @DisplayName("custom multi-arg function")
        void customMultiArg() {
            MathFunction avg = MathFunction.of("avg", 4, args -> {
                double sum = 0;
                for (double a : args) sum += a;
                return sum / args.length;
            });
            double result = new ExpressionBuilder("avg(1, 2, 3, 4)")
                    .function(avg)
                    .build()
                    .evaluate();
            assertEquals(2.5, result, 1e-10);
        }
    }

    @Nested
    @DisplayName("Custom Operators")
    class CustomOpTests {

        @Test
        @DisplayName("custom factorial operator")
        void factorialOperator() {
            MathOperator factorial = new MathOperator("!", 1, Associativity.LEFT,
                    Precedence.POWER + 1, args -> {
                int n = (int) args[0];
                double r = 1;
                for (int i = 2; i <= n; i++) r *= i;
                return r;
            });
            double result = new ExpressionBuilder("5!")
                    .operator(factorial)
                    .build()
                    .evaluate();
            assertEquals(120.0, result, 1e-10);
        }

        @Test
        @DisplayName("custom >= operator")
        void gteqOperator() {
            MathOperator gteq = MathOperator.binary(">=", Associativity.LEFT,
                    Precedence.ADDITION - 1,
                    (a, b) -> a >= b ? 1.0 : 0.0);
            Expression expr = new ExpressionBuilder("2 >= 1")
                    .operator(gteq)
                    .build();
            assertEquals(1.0, expr.evaluate(), 1e-10);

            Expression expr2 = new ExpressionBuilder("1 >= 2")
                    .operator(gteq)
                    .build();
            assertEquals(0.0, expr2.evaluate(), 1e-10);
        }
    }
}
