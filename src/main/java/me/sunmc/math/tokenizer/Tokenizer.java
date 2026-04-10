package me.sunmc.math.tokenizer;

import me.sunmc.math.ParseException;
import me.sunmc.math.function.BuiltInFunctions;
import me.sunmc.math.function.MathFunction;
import me.sunmc.math.operator.BuiltInOperators;
import me.sunmc.math.operator.MathOperator;
import me.sunmc.math.token.*;

import java.util.*;

/**
 * Tokenizes a mathematical expression string into a stream of {@link Token} records.
 *
 * <p>Implements both {@link Iterator} and {@link Iterable} for convenient
 * use in for-each loops and stream-based processing.</p>
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Full Unicode support for variable and function names</li>
 *   <li>Scientific notation ({@code 1.5E-3})</li>
 *   <li>Implicit multiplication ({@code 2cos(x)} → {@code 2 * cos(x)})</li>
 *   <li>Multiple bracket types: {@code ()}, {@code []}, {@code {}}</li>
 *   <li>Precise error reporting with character position</li>
 * </ul>
 *
 * @author sun-dev
 * @since 1.0.0
 */
public final class Tokenizer implements Iterator<Token>, Iterable<Token> {

    private final char[] expression;
    private final String expressionString;
    private final int length;
    private final Map<String, MathFunction> userFunctions;
    private final Map<String, MathOperator> userOperators;
    private final Set<String> variableNames;
    private final boolean implicitMultiplication;

    private int pos = 0;
    private Token lastToken;

    /**
     * Creates a new Tokenizer for the given expression.
     *
     * @param expression             the mathematical expression string
     * @param userFunctions          user-defined functions (may be empty)
     * @param userOperators          user-defined operators (may be empty)
     * @param variableNames          declared variable names
     * @param implicitMultiplication whether to support implicit multiplication
     */
    public Tokenizer(String expression,
                     Map<String, MathFunction> userFunctions,
                     Map<String, MathOperator> userOperators,
                     Set<String> variableNames,
                     boolean implicitMultiplication) {
        Objects.requireNonNull(expression, "Expression must not be null");
        this.expressionString = expression.trim();
        this.expression = this.expressionString.toCharArray();
        this.length = this.expression.length;
        this.userFunctions = Objects.requireNonNullElse(userFunctions, Map.of());
        this.userOperators = Objects.requireNonNullElse(userOperators, Map.of());
        this.variableNames = Objects.requireNonNullElse(variableNames, Set.of());
        this.implicitMultiplication = implicitMultiplication;
    }

    private static boolean isNumericChar(char ch, boolean lastWasExponent) {
        return Character.isDigit(ch) || ch == '.'
                || ch == 'e' || ch == 'E'
                || (lastWasExponent && (ch == '+' || ch == '-'));
    }

    private static boolean isExponentIndicator(char ch) {
        return ch == 'e' || ch == 'E';
    }

    private static boolean isIdentifierChar(char ch, boolean isFirst) {
        if (isFirst) {
            return Character.isLetter(ch) || ch == '_';
        }
        return Character.isLetter(ch) || Character.isDigit(ch) || ch == '_';
    }

    private static boolean isOpenParen(char ch) {
        return ch == '(' || ch == '[' || ch == '{';
    }

    private static boolean isCloseParen(char ch) {
        return ch == ')' || ch == ']' || ch == '}';
    }

    @Override
    public boolean hasNext() {
        return pos < length;
    }

    @Override
    public Token next() {
        return nextToken();
    }

    @Override
    public Iterator<Token> iterator() {
        return this;
    }

    /**
     * Reads and returns the next token from the expression.
     *
     * @return the next token
     * @throws ParseException if the expression contains invalid characters or syntax
     */
    public Token nextToken() {
        // Skip whitespace
        while (pos < length && Character.isWhitespace(expression[pos])) {
            pos++;
        }
        if (pos >= length) {
            throw new ParseException("Unexpected end of expression", expressionString, pos);
        }

        char ch = expression[pos];

        if (Character.isDigit(ch) || ch == '.') {
            if (shouldInsertImplicitMultiplication()) {
                return emitImplicitMultiplication();
            }
            return parseNumber();
        }

        if (ch == ',') {
            pos++;
            lastToken = new SeparatorToken();
            return lastToken;
        }

        if (isOpenParen(ch)) {
            if (shouldInsertImplicitMultiplication()) {
                return emitImplicitMultiplication();
            }
            pos++;
            lastToken = new OpenParenToken();
            return lastToken;
        }

        if (isCloseParen(ch)) {
            pos++;
            lastToken = new CloseParenToken();
            return lastToken;
        }

        if (MathOperator.isAllowedOperatorChar(ch)) {
            return parseOperator(ch);
        }

        if (Character.isLetter(ch) || ch == '_') {
            if (shouldInsertImplicitMultiplication()) {
                return emitImplicitMultiplication();
            }
            return parseFunctionOrVariable();
        }

        throw new ParseException(
                "Unexpected character '%c' (U+%04X)".formatted(ch, (int) ch),
                expressionString, pos);
    }

    private Token parseNumber() {
        int start = pos;
        pos++;

        while (pos < length && isNumericChar(expression[pos], pos > 0 && isExponentIndicator(expression[pos - 1]))) {
            pos++;
        }

        // Handle trailing 'e' or 'E' without a valid exponent
        if (pos > start && isExponentIndicator(expression[pos - 1])) {
            pos--;
        }

        String numStr = new String(expression, start, pos - start);
        try {
            double value = Double.parseDouble(numStr);
            lastToken = new NumberToken(value);
            return lastToken;
        } catch (NumberFormatException e) {
            throw new ParseException(
                    "Invalid number literal: '%s'".formatted(numStr),
                    expressionString, start);
        }
    }

    private Token parseOperator(char firstChar) {
        int start = pos;

        // Try to match the longest operator symbol
        StringBuilder sb = new StringBuilder();
        sb.append(firstChar);
        int tempPos = pos + 1;
        while (tempPos < length && MathOperator.isAllowedOperatorChar(expression[tempPos])) {
            sb.append(expression[tempPos]);
            tempPos++;
        }

        // Try longest match first, then shrink
        MathOperator matched = null;
        int matchLen = 0;
        while (sb.length() > 0) {
            String sym = sb.toString();
            MathOperator op = userOperators.get(sym);
            if (op != null) {
                matched = op;
                matchLen = sym.length();
                break;
            }
            // For single-char built-in operators
            if (sym.length() == 1) {
                int argc = determineOperandCount();
                Optional<MathOperator> builtin = BuiltInOperators.get(sym.charAt(0), argc);
                if (builtin.isPresent()) {
                    matched = builtin.get();
                    matchLen = 1;
                    break;
                }
            }
            sb.setLength(sb.length() - 1);
        }

        if (matched == null) {
            throw new ParseException(
                    "Unknown operator '%c'".formatted(firstChar),
                    expressionString, start);
        }

        pos += matchLen;
        lastToken = new OperatorToken(matched);
        return lastToken;
    }

    private int determineOperandCount() {
        if (lastToken == null) {
            return 1; // Unary at start of expression
        }
        return switch (lastToken) {
            case OpenParenToken() -> 1;
            case SeparatorToken() -> 1;
            case OperatorToken(var op) -> {
                if (op.operands() == 2
                        || (op.operands() == 1 && !op.isLeftAssociative())) {
                    yield 1;
                }
                yield 2;
            }
            default -> 2;
        };
    }

    private Token parseFunctionOrVariable() {
        int start = pos;
        int bestLen = 0;
        Token bestToken = null;

        // Advance through valid identifier characters
        int end = pos;
        while (end < length && isIdentifierChar(expression[end], end == start)) {
            end++;
        }

        // Try to find the longest matching function or variable
        for (int len = end - start; len >= 1; len--) {
            String name = new String(expression, start, len);

            if (variableNames.contains(name)) {
                if (len > bestLen) {
                    bestLen = len;
                    bestToken = new VariableToken(name);
                }
                // Keep searching for a longer function match
            }

            MathFunction fn = userFunctions.get(name);
            if (fn == null) {
                fn = BuiltInFunctions.get(name).orElse(null);
            }
            if (fn != null && len > bestLen) {
                bestLen = len;
                bestToken = new FunctionToken(fn);
            }
        }

        if (bestToken == null) {
            String name = new String(expression, start, end - start);
            throw new ParseException(
                    "Unknown function or variable: '%s'".formatted(name),
                    expressionString, start);
        }

        pos = start + bestLen;
        lastToken = bestToken;
        return lastToken;
    }

    private boolean shouldInsertImplicitMultiplication() {
        if (!implicitMultiplication || lastToken == null) {
            return false;
        }
        return switch (lastToken) {
            case NumberToken nt -> true;
            case VariableToken vt -> true;
            case CloseParenToken cp -> true;
            default -> false;
        };
    }

    private Token emitImplicitMultiplication() {
        MathOperator mul = BuiltInOperators.MULTIPLICATION;
        lastToken = new OperatorToken(mul);
        return lastToken;
    }
}
