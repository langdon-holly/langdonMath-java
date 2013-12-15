package langdon.util;

import java.text.ParseException;

public interface TokenParser {
    
    public Token parseToken(String tokenString, String matched, ParseContext context,
            String origString, int begin, int end) throws ParseException;
    
}