package me.sunmc.math;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for expression validation.
 *
 * @author sun-dev
 */
@DisplayName("Validation")
class ValidationTest {

    @Test
    @DisplayName("valid expression with variables set")
    void validWithVars() {
        Expression expr = new ExpressionBuilder("x + 1")
                .variable("x")
                .build()
                .setVariable("x", 5);
        ValidationResult result = expr.validate();
        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    @DisplayName("invalid: variable not set")
    void variableNotSet() {
        Expression expr = new ExpressionBuilder("x + 1")
                .variable("x")
                .build();
        ValidationResult result = expr.validate();
        assertFalse(result.isValid());
        assertFalse(result.getErrors().isEmpty());
    }

    @Test
    @DisplayName("valid when skipping variable check")
    void skipVariableCheck() {
        Expression expr = new ExpressionBuilder("x + 1")
                .variable("x")
                .build();
        ValidationResult result = expr.validate(false);
        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("ValidationResult.SUCCESS singleton")
    void successSingleton() {
        assertSame(ValidationResult.SUCCESS, ValidationResult.SUCCESS);
        assertTrue(ValidationResult.SUCCESS.isValid());
    }

    @Test
    @DisplayName("ValidationResult.failure()")
    void failureFactory() {
        ValidationResult result = ValidationResult.failure("something wrong");
        assertFalse(result.isValid());
        assertEquals(List.of("something wrong"), result.getErrors());
    }

    @Test
    @DisplayName("simple literal always valid")
    void simpleLiteral() {
        Expression expr = new ExpressionBuilder("42").build();
        assertTrue(expr.validate().isValid());
    }
}
