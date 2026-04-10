package me.sunmc.math.token;

/**
 * Sealed interface representing all possible tokens in a mathematical expression.
 *
 * <p>This sealed hierarchy enables exhaustive pattern matching via {@code switch}
 * expressions, ensuring the compiler verifies that all token types are handled.
 * Each token type is implemented as an immutable {@link Record}.</p>
 *
 * <p>Example usage with pattern matching:</p>
 * <pre>{@code
 * switch (token) {
 *     case NumberToken(var value)   -> handle(value);
 *     case VariableToken(var name)  -> resolve(name);
 *     case OperatorToken(var op)    -> apply(op);
 *     case FunctionToken(var fn)    -> invoke(fn);
 *     case OpenParenToken()         -> pushToStack();
 *     case CloseParenToken()        -> popUntilOpen();
 *     case SeparatorToken()         -> handleSeparator();
 * }
 * }</pre>
 *
 * @author sun-dev
 * @since 1.0.0
 */
public sealed interface Token
        permits NumberToken, VariableToken, OperatorToken,
        FunctionToken, OpenParenToken, CloseParenToken,
        SeparatorToken {
}
