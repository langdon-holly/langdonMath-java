package langdon.math;

import java.util.ArrayList;
import java.util.List;
import java.text.ParseException;

import langdon.util.*;

public class ParenFunctioning implements AfterLevelPopHandler {
    
    public ArrayList<Object> functions = new ArrayList<Object>();
    
    public Token[] doItTokenPair = {new Token<Object>("leftParen"),
                                 new Token<Object>("rightParen")};
    
    public Token[] functioningTokenPair = {new Token<Object>("parenFuncBeg"),
                                           new Token<Object>("parenFuncEnd")};
    
    public boolean debug = false;
    
    public ParenFunctioning() {
        functions.add("sin");
        functions.add("cos");
        functions.add("tan");
        functions.add("cot");
        functions.add("sec");
        functions.add("csc");
        functions.add("log");
        functions.add("ln");
        functions.add("derivativeFunc");
        functions.add("sqrt");
    }
    
    public TokenList<Object> handleAfterPop(Token[] delims, TokenList<?> currentLevelW, LevelParser levelParser) throws ParseException {
        TokenList<Object> currentLevel = currentLevelW.castValuesTo(Object.class);
        if (!delims[0].equals(doItTokenPair[0])) return currentLevel;
        if (!delims[1].equals(doItTokenPair[1])) return currentLevel;
        
        for (int itr = currentLevel.size() - 2; itr >= 0; itr--) {
            if (!(currentLevel.get(itr).tokenValue instanceof Subscript)) {
                if (functions.contains(currentLevel.get(itr).tokenValue)) {
                    if (debug) System.err.println("currentLevel before: " + currentLevel);
                    List<Token<Object>> mkFuncList = currentLevel.subList(itr, currentLevel.size());
                    // System.err.println(mkFuncList);
                    Token<Object> parsedFunc = levelParser.parseLevel(functioningTokenPair, new TokenList<Object>(mkFuncList));
                    mkFuncList.clear();
                    mkFuncList.add(parsedFunc);
                    // System.err.println(mkFuncList);
                    if (debug) System.err.println("currentLevel after:  " + currentLevel);
                }
                break;
            }
        }
        return currentLevel;
    }
    
}
