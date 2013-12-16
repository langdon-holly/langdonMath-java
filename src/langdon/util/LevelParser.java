package langdon.util;

import java.text.ParseException;

public interface LevelParser {
    
    public Token<Object> parseLevel(Token[] delims, TokenList<?> tokens) throws ParseException;
    
}
