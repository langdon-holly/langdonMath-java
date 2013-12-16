package langdon.util;

import java.util.ArrayList;
import java.text.ParseException;

public class LevelsParser {
    
    public Token[][] levelDelims;
    
    public LevelParser levelParser;
    
    public AfterLevelPopHandler afterPopHandler = null;
    
    public boolean debug = false;
    
    public LevelsParser(Token[][] levelDelims, LevelParser levelParser) {
        this.levelDelims = levelDelims;
        this.levelParser = levelParser;
    }
    
    public LevelsParser withAfterPopHandler(AfterLevelPopHandler afterPopHandler) {
        this.afterPopHandler = afterPopHandler;
        return this;
    }
    
    public int direction(Token<?> token) {
        int index = ArraySearch.index(levelDelims, token);
        if (index == -1) return 0;
        if (levelDelims[index][0].equals(token)) return 1;
        return -1;
    }
    
    public boolean validDelims(Token[] tokenPair) {
        for (int i = 0; i < levelDelims.length; i++) {
            Token[] delims = levelDelims[i];
            if (delims[0].equals(tokenPair[0]) && delims[1].equals(tokenPair[1])) return true;
        }
        return false;
    }
    
    public Token<Object> parseLevels(TokenList<?> tokened) throws ParseException {
        int indexOn;
        
        ArrayList<TokenList<Object>> parenContextStack = new ArrayList<TokenList<Object>>();
        parenContextStack.add(new TokenList<Object>(tokened.fromStrBegin));
        ArrayList<Token<Object>> parenStack = new ArrayList<Token<Object>>();
        parenStack.add(new Token<Object>("stringBeg"));
        
        indexOn = 0;
        while (indexOn < tokened.size()) {
            if (debug) System.err.println("--------------");
            if (debug) System.err.println("contexts: " + parenContextStack);
            if (debug) System.err.println("parens:   " + parenStack);
            Token<Object> tokenOn = tokened.get(indexOn).castValueTo(Object.class);
            Object tokenValueOn = tokenOn.tokenValue;
            if (debug) System.err.println("token:    " + tokenOn);
            
            int direction = direction(tokenOn);
            
            if (direction == 1) {
                if (debug) System.err.println("pushing level on " + tokenValueOn + "…");
                parenContextStack.add(new TokenList<Object>(tokenOn.fromStrBegin));
                parenStack.add(tokenOn);
            }
            else if (direction == -1) {
                if (debug) System.err.println("popping level on " + tokenValueOn + "…");
                
                Token[] tokenPair = new Token[2];
                tokenPair[0] = parenStack.get(parenStack.size() - 1);
                tokenPair[1] = tokenOn;
                if (!validDelims(tokenPair)) throw new ParseException("unopened level", indexOn);
                
                parenContextStack.get(parenContextStack.size() - 2).add(levelParser.parseLevel(tokenPair, parenContextStack.remove(parenContextStack.size() - 1)));
                parenStack.remove(parenStack.size() - 1);
                
                if (afterPopHandler != null) parenContextStack.set(parenContextStack.size() - 1,
                        afterPopHandler.handleAfterPop(tokenPair, parenContextStack.get(
                            parenContextStack.size() - 1), levelParser));
                
                if (debug) System.err.println("…popped paren");
            }
//             else if (tokenValueOn.equals("leftBracket")) {
//                 if (debug) System.err.println("pushing bracket…");
//                 parenContextStack.add(new TokenList<Object>(tokenOn.fromStrBegin));
//                 parenStack.add("bracket");
//             }
//             else if (tokenValueOn.equals("rightBracket")) {
//                 if (debug) System.err.println("popping bracket…");
//                 if (!parenStack.get(parenStack.size() - 1).equals("bracket")) {
//                     throw new ParseException("unopened bracket", indexOn);
//                 }
//                 Token<Expr> subscript = parseLevel(parenContextStack.remove(parenContextStack.size() - 1), context);
//                 parenContextStack.get(parenContextStack.size() - 1).add(new Token<Subscript>(new Subscript(subscript.tokenValue), subscript, subscript).castValueTo(Object.class));
//                 parenStack.remove(parenStack.size() - 1);
//                 if (debug) System.err.println("…popped bracket");
//             }
            else {
                parenContextStack.get(parenContextStack.size() - 1).add(tokenOn);
            }
            
            indexOn++;
        }
        
        Token[] stringDelims = {new Token<Object>("stringBeg"), new Token<Object>("stringEnd")};
        Token<Object> bigToken = levelParser.parseLevel(stringDelims, parenContextStack.remove(parenContextStack.size() - 1));
        
        if (!parenContextStack.isEmpty()) {
            throw new ParseException("unclosed level", indexOn - 1);
        }
        
        return bigToken;
    }
}
