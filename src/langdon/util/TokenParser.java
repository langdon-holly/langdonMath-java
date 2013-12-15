package langdon.util;

import java.text.ParseException;

public abstract class TokenParser {
    
    public abstract Token<?> parseToken(Token<?> token) throws ParseException;
    
    public <E> TokenList<?> parseTokenList(TokenList<E> tokens) throws ParseException {
        for (int i = 0; i < tokens.size(); i++) {
            Token<E> parsedToken = (Token<E>) parseToken(tokens.get(i));
            if (parsedToken != null) tokens.set(i, parsedToken);
        }
        return tokens;
    }
    
}