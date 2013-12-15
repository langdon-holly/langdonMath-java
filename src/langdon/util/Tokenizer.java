package langdon.util;

import java.util.regex.*;
import java.util.HashMap;
import java.lang.reflect.Array;
import java.text.ParseException;

public class Tokenizer {
    
    public String[][] tokens;
    public TokenParser tokenParser;
    public String noTokenMsg = "invalid token";
    
    public boolean debug = false;
    
    public Tokenizer(String[][] tokens, TokenParser tokenParser) {
        this.tokens = tokens;
        this.tokenParser = tokenParser;
    }
    
    public TokenList tokenize(String string, ParseContext context) throws ParseException {
        if (debug) System.err.println("tokenizing \"" + string + "\"");
        String string2 = string;
        TokenList<Object> tokened = new TokenList<Object>(0);
        int indexAt = 0;
        
        while (indexAt < string.length()) {
        
            int tokenOnIndex = 0;
            boolean keepGoing = true;
            
            while (keepGoing && tokenOnIndex < Array.getLength(tokens)) {
                Pattern pattern = Pattern.compile("^"+tokens[tokenOnIndex][0]);
                Matcher matcher = pattern.matcher(string2);
                
                if (matcher.find()) {
                    String matched = matcher.group();
                    String token = tokens[tokenOnIndex][1];
                    
                    Token parsedToken = tokenParser.parseToken(token, matched, context, string, indexAt, indexAt + matcher.end());
                    tokened.add(parsedToken != null ? parsedToken
                            : new Token<Object>(token, string, indexAt, indexAt + matcher.end()));
                    
                    keepGoing = false;
                    
                    indexAt+= matcher.end() - matcher.start();
                    string2 = string2.substring(matcher.end(), string2.length());
                    
                    // if (debug) System.err.println("last token: " +  tokened.get(tokened.size() - 1));
                    // if (debug) System.err.println("indexAt next: " +  indexAt);
                }
                tokenOnIndex++;
            }
            
            if (keepGoing) {
                throw new ParseException(noTokenMsg, indexAt);
            }
        }
        
        if (debug) System.err.println("tokens:    " + tokened);
        return tokened;
    }
    
}