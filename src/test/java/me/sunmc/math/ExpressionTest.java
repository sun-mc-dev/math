package me.sunmc.math;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Core expression evaluation tests.
 *
 * @author sun-dev
 */
@DisplayName("Expression Evaluation")
class ExpressionTest {

    private static final double EPSILON = 1e-10;

    @Nested
    @DisplayName("Basic Arithmetic")
    class BasicArithmetic {

        @Test
        @DisplayName("addition: 2 + 3 = 5")
        void addition() {
            double result = new ExpressionBuilder("2 + 3").build().evaluate();
            assertEquals(5.0, result, EPSILON);
        }

        @Test
        @DisplayName("subtraction: 10 - 4 = 6")
        void subtraction() {
            double result = new ExpressionBuilder("10 - 4").build().evaluate();
            assertEquals(6.0, result, EPSILON);
        }

        @Test
        @DisplayName("multiplication: 3 * 7 = 21")
        void multiplication() {
            double result = new ExpressionBuilder("3 * 7").build().evaluate();
            assertEquals(21.0, result, EPSILON);
        }

        @Test
        @DisplayName("division: 15 / 3 = 5")
        void division() {
            double result = new ExpressionBuilder("15 / 3").build().evaluate();
            assertEquals(5.0, result, EPSILON);
        }

        @Test
        @DisplayName("modulo: 17 % 5 = 2")
        void modulo() {
            double result = new ExpressionBuilder("17 % 5").build().evaluate();
            assertEquals(2.0, result, EPSILON);
        }

        @Test
        @DisplayName("power: 2 ^ 10 = 1024")
        void power() {
            double result = new ExpressionBuilder("2 ^ 10").build().evaluate();
            assertEquals(1024.0, result, EPSILON);
        }

        @Test
        @DisplayName("complex expression: 3 * sin(y) - 2 / (x - 2)")
        void complexExpression() {
            Expression expr = new ExpressionBuilder("3 * sin(y) - 2 / (x - 2)")
                    .variables("x", "y")
                    .build()
                    .setVariable("x", 2.3)
                    .setVariable("y", 3.14);
            double expected = 3 * Math.sin(3.14) - 2 / (2.3 - 2);
            assertEquals(expected, expr.evaluate(), EPSILON);
        }
    }

    @Nested
    @DisplayName("Operator Precedence")
    class OperatorPrecedence {

        @Test
        @DisplayName("multiplication before addition: 2 + 3 * 4 = 14")
        void multBeforeAdd() {
            double result = new ExpressionBuilder("2 + 3 * 4").build().evaluate();
            assertEquals(14.0, result, EPSILON);
        }

        @Test
        @DisplayName("parentheses override precedence: (2 + 3) * 4 = 20")
        void parenthesesOverride() {
            double result = new ExpressionBuilder("(2 + 3) * 4").build().evaluate();
            assertEquals(20.0, result, EPSILON);
        }

        @Test
        @DisplayName("power right-associative: 2 ^ 3 ^ 2 = 512")
        void powerRightAssociative() {
            double result = new ExpressionBuilder("2 ^ 3 ^ 2").build().evaluate();
            assertEquals(512.0, result, EPSILON);
        }

        @Test
        @DisplayName("unary minus precedence: -1 ^ 2 = -1 (not 1)")
        void unaryMinusPrecedence() {
            double result = new ExpressionBuilder("-1 ^ 2").build().evaluate();
            assertEquals(-1.0, result, EPSILON);
        }
    }

    @Nested
    @DisplayName("Unary Operators")
    class UnaryOperators {

        @Test
        @DisplayName("unary minus: -5")
        void unaryMinus() {
            double result = new ExpressionBuilder("-5").build().evaluate();
            assertEquals(-5.0, result, EPSILON);
        }

        @Test
        @DisplayName("unary plus: +5")
        void unaryPlus() {
            double result = new ExpressionBuilder("+5").build().evaluate();
            assertEquals(5.0, result, EPSILON);
        }

        @Test
        @DisplayName("double negation: --5 = 5")
        void doubleNegation() {
            double result = new ExpressionBuilder("--5").build().evaluate();
            assertEquals(5.0, result, EPSILON);
        }

        @Test
        @DisplayName("unary minus in expression: 2 * -3 = -6")
        void unaryMinusInExpression() {
            double result = new ExpressionBuilder("2 * -3").build().evaluate();
            assertEquals(-6.0, result, EPSILON);
        }
    }

    @Nested
    @DisplayName("Variables")
    class Variables {

        @Test
        @DisplayName("single variable")
        void singleVariable() {
            Expression expr = new ExpressionBuilder("x * 2")
                    .variable("x")
                    .build()
                    .setVariable("x", 5);
            assertEquals(10.0, expr.evaluate(), EPSILON);
        }

        @Test
        @DisplayName("multiple variables")
        void multipleVariables() {
            Expression expr = new ExpressionBuilder("x + y + z")
                    .variables("x", "y", "z")
                    .build()
                    .setVariables(Map.of("x", 1.0, "y", 2.0, "z", 3.0));
            assertEquals(6.0, expr.evaluate(), EPSILON);
        }

        @Test
        @DisplayName("variable names: getVariableNames()")
        void getVariableNames() {
            Expression expr = new ExpressionBuilder("x + y * z")
                    .variables("x", "y", "z")
                    .build();
            assertTrue(expr.getVariableNames().containsAll(java.util.Set.of("x", "y", "z")));
        }

        @Test
        @DisplayName("missing variable throws EvaluationException")
        void missingVariable() {
            Expression expr = new ExpressionBuilder("x + 1")
                    .variable("x")
                    .build();
            assertThrows(EvaluationException.class, expr::evaluate);
        }

        @Test
        @DisplayName("variable provider")
        void variableProvider() {
            Expression expr = new ExpressionBuilder("myvar + 1")
                    .variable("myvar")
                    .build()
                    .setVariableProvider(name -> "myvar".equals(name) ? 42.0 : null);
            assertEquals(43.0, expr.evaluate(), EPSILON);
        }
    }

    @Nested
    @DisplayName("Built-in Constants")
    class Constants {

        @Test
        @DisplayName("pi constant")
        void piConstant() {
            double result = new ExpressionBuilder("pi").build().evaluate();
            assertEquals(Math.PI, result, EPSILON);
        }

        @Test
        @DisplayName("pi unicode: π")
        void piUnicode() {
            double result = new ExpressionBuilder("π").build().evaluate();
            assertEquals(Math.PI, result, EPSILON);
        }

        @Test
        @DisplayName("e constant")
        void eulerConstant() {
            double result = new ExpressionBuilder("e").build().evaluate();
            assertEquals(Math.E, result, EPSILON);
        }

        @Test
        @DisplayName("phi: φ = golden ratio")
        void goldenRatio() {
            double result = new ExpressionBuilder("φ").build().evaluate();
            assertEquals(1.6180339887498948482, result, EPSILON);
        }

        @Test
        @DisplayName("combined constants: pi + e")
        void combinedConstants() {
            double result = new ExpressionBuilder("pi + e").build().evaluate();
            assertEquals(Math.PI + Math.E, result, EPSILON);
        }

        @Test
        @DisplayName("tau = 2 * pi")
        void tauConstant() {
            double result = new ExpressionBuilder("tau").build().evaluate();
            assertEquals(2 * Math.PI, result, EPSILON);
        }
    }

    @Nested
    @DisplayName("Scientific Notation")
    class ScientificNotation {

        @Test
        @DisplayName("1.5E3 = 1500")
        void scientificUpper() {
            double result = new ExpressionBuilder("1.5E3").build().evaluate();
            assertEquals(1500.0, result, EPSILON);
        }

        @Test
        @DisplayName("7.2973525698e-3")
        void fineStructure() {
            double result = new ExpressionBuilder("7.2973525698e-3").build().evaluate();
            assertEquals(7.2973525698e-3, result, EPSILON);
        }

        @Test
        @DisplayName("1E+2 = 100")
        void scientificPositiveExponent() {
            double result = new ExpressionBuilder("1E+2").build().evaluate();
            assertEquals(100.0, result, EPSILON);
        }
    }

    @Nested
    @DisplayName("Implicit Multiplication")
    class ImplicitMultiplication {

        @Test
        @DisplayName("2x = 2 * x")
        void numberTimesVariable() {
            Expression expr = new ExpressionBuilder("2x")
                    .variable("x")
                    .build()
                    .setVariable("x", 3);
            assertEquals(6.0, expr.evaluate(), EPSILON);
        }

        @Test
        @DisplayName("2cos(0) = 2 * cos(0) = 2")
        void numberTimesFunction() {
            double result = new ExpressionBuilder("2cos(0)").build().evaluate();
            assertEquals(2.0, result, EPSILON);
        }

        @Test
        @DisplayName("(2)(3) = 6")
        void parenTimesParen() {
            double result = new ExpressionBuilder("(2)(3)").build().evaluate();
            assertEquals(6.0, result, EPSILON);
        }

        @Test
        @DisplayName("disabled implicit multiplication")
        void disabledImplicitMul() {
            assertThrows(Exception.class, () ->
                    new ExpressionBuilder("2x")
                            .variable("x")
                            .implicitMultiplication(false)
                            .build()
                            .setVariable("x", 3)
                            .evaluate());
        }
    }

    @Nested
    @DisplayName("Async Evaluation")
    class AsyncEvaluation {

        @Test
        @DisplayName("evaluateAsync() with virtual thread")
        void asyncVirtualThread() throws Exception {
            Expression expr = new ExpressionBuilder("2 + 3").build();
            double result = expr.evaluateAsync().get();
            assertEquals(5.0, result, EPSILON);
        }

        @Test
        @DisplayName("evaluateAsync(executor)")
        void asyncWithExecutor() throws Exception {
            var executor = java.util.concurrent.Executors.newSingleThreadExecutor();
            try {
                Expression expr = new ExpressionBuilder("7 * 6").build();
                double result = expr.evaluateAsync(executor).get();
                assertEquals(42.0, result, EPSILON);
            } finally {
                executor.shutdown();
            }
        }
    }

    @Nested
    @DisplayName("Expression Copy")
    class ExpressionCopy {

        @Test
        @DisplayName("copy() creates independent instance")
        void copyIsIndependent() {
            Expression original = new ExpressionBuilder("x + 1")
                    .variable("x")
                    .build()
                    .setVariable("x", 5);

            Expression copy = original.copy();
            copy.setVariable("x", 10);

            assertEquals(6.0, original.evaluate(), EPSILON);
            assertEquals(11.0, copy.evaluate(), EPSILON);
        }
    }

    @Nested
    @DisplayName("RPN String")
    class RPNString {

        @Test
        @DisplayName("toRPNString() returns postfix notation")
        void rpnString() {
            Expression expr = new ExpressionBuilder("2 + 3").build();
            String rpn = expr.toRPNString();
            assertNotNull(rpn);
            assertFalse(rpn.isBlank());
        }
    }

    @Nested
    @DisplayName("Nested Parentheses")
    class NestedParentheses {

        @Test
        @DisplayName("deeply nested: ((((2 + 3)))) = 5")
        void deeplyNested() {
            double result = new ExpressionBuilder("((((2 + 3))))").build().evaluate();
            assertEquals(5.0, result, EPSILON);
        }

        @Test
        @DisplayName("bracket types: [2 + 3] * {4 - 1}")
        void mixedBrackets() {
            double result = new ExpressionBuilder("[2 + 3] * {4 - 1}").build().evaluate();
            assertEquals(15.0, result, EPSILON);
        }
    }
}
