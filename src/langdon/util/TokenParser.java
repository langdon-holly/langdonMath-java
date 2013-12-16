package langdon.util;

import java.text.ParseException;

public abstract class TokenParser {
    
    public abstract Token<Object> parseToken(Token<?> token) throws ParseException;
    
    public TokenList<Object> parseTokenList(TokenList<?> tokens) throws ParseException {
        TokenList<Object> newTokens = new TokenList(tokens.fromStrBegin);
        for (Token<?> token : tokens) {
            Token<Object> parsedToken = parseToken(token);
            newTokens.add(parsedToken);
        }
        return newTokens;
    }
    
}