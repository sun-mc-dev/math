package me.sunmc.math;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Edge case tests for robustness.
 *
 * @author sun-dev
 */
@DisplayName("Edge Cases")
class EdgeCaseTest {

    private static final double EPSILON = 1e-10;

    @Test
    @DisplayName("division by zero throws")
    void divisionByZero() {
        Expression expr = new ExpressionBuilder("1 / 0").build();
        assertThrows(EvaluationException.class, expr::evaluate);
    }

    @Test
    @DisplayName("modulo by zero throws")
    void moduloByZero() {
        Expression expr = new ExpressionBuilder("5 % 0").build();
        assertThrows(EvaluationException.class, expr::evaluate);
    }

    @Test
    @DisplayName("mismatched parentheses throws ParseException")
    void mismatchedParens() {
        assertThrows(ParseException.class, () ->
                new ExpressionBuilder("(2 + 3").build());
    }

    @Test
    @DisplayName("empty parentheses in expression")
    void extraCloseParen() {
        assertThrows(Exception.class, () ->
                new ExpressionBuilder("2 + 3)").build());
    }

    @Test
    @DisplayName("single number")
    void singleNumber() {
        assertEquals(42.0, new ExpressionBuilder("42").build().evaluate(), EPSILON);
    }

    @Test
    @DisplayName("single negative number")
    void singleNegativeNumber() {
        assertEquals(-42.0, new ExpressionBuilder("-42").build().evaluate(), EPSILON);
    }

    @Test
    @DisplayName("decimal without leading zero: .5")
    void decimalNoLeadingZero() {
        assertEquals(0.5, new ExpressionBuilder(".5").build().evaluate(), EPSILON);
    }

    @Test
    @DisplayName("very large number")
    void veryLargeNumber() {
        double result = new ExpressionBuilder("1E308").build().evaluate();
        assertEquals(1E308, result, 0);
    }

    @Test
    @DisplayName("nested function calls: sin(cos(1))")
    void nestedFunctions() {
        double expected = Math.sin(Math.cos(1.0));
        double result = new ExpressionBuilder("sin(cos(1))").build().evaluate();
        assertEquals(expected, result, EPSILON);
    }

    @Test
    @DisplayName("function with expression argument: sin(2 + 3)")
    void functionWithExprArg() {
        double expected = Math.sin(5.0);
        double result = new ExpressionBuilder("sin(2 + 3)").build().evaluate();
        assertEquals(expected, result, EPSILON);
    }

    @Test
    @DisplayName("spaces everywhere")
    void spacesEverywhere() {
        double result = new ExpressionBuilder("  2  +  3  ").build().evaluate();
        assertEquals(5.0, result, EPSILON);
    }

    @Test
    @DisplayName("long chain of operations")
    void longChain() {
        double result = new ExpressionBuilder("1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9 + 10")
                .build().evaluate();
        assertEquals(55.0, result, EPSILON);
    }

    @Test
    @DisplayName("cotangent at zero throws ArithmeticException")
    void cotZero() {
        Expression expr = new ExpressionBuilder("cot(0)").build();
        assertThrows(EvaluationException.class, expr::evaluate);
    }

    @Test
    @DisplayName("factorial of negative throws")
    void factorialNegative() {
        Expression expr = new ExpressionBuilder("factorial(-1)").build();
        assertThrows(EvaluationException.class, expr::evaluate);
    }

    @Test
    @DisplayName("unknown function throws ParseException")
    void unknownFunction() {
        assertThrows(ParseException.class, () ->
                new ExpressionBuilder("foobar(1)")
                        .build());
    }

    @Test
    @DisplayName("clearVariables and re-set")
    void clearAndReset() {
        Expression expr = new ExpressionBuilder("x + 1")
                .variable("x")
                .build()
                .setVariable("x", 5);
        assertEquals(6.0, expr.evaluate(), EPSILON);

        expr.clearVariables();
        expr.setVariable("x", 10);
        assertEquals(11.0, expr.evaluate(), EPSILON);
    }
}
