package langdon.math;

import langdon.util.*;

import java.util.ArrayList;
import java.util.regex.*;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.lang.reflect.Array;
import java.text.ParseException;

public abstract class ExprParser {
    
    public static void main(String[] args) {
        try {
            Expr expr = parseExpr(args[0]);
            System.out.println(expr);
        } catch (ParseException e) {
            System.err.println(generateParseMesg(args[0], e));
        }
    }
    
    public static final String[][] tokens
            = {{"\\s+","space"},
               {"d(\\^\\d+)?[a-zA-Z]/d[a-zA-Z](\\^\\d+)?","varDerivative"},
               {"d(\\^\\d+)?/d[a-zA-Z](\\^\\d+)?","derivativeFunc"},
               {"undef", "undef"},
               {"sqrt", "sqrt"},
               {"sin", "sin"}, // (?![a-zA-Z])
               {"cos", "cos"},
               {"tan", "tan"},
               {"cot", "cot"},
               {"sec", "sec"},
               {"csc", "csc"},
               {"log", "log"},
               {"ln", "ln"},
               {"pi", "piWord"},
               {"e", "e"},
               {"E", "lonely-E"},
               {"d", "lonely-d"},
               {"i", "i"},
               {"π", "pi"},
               {"[a-zA-Z]", "var"},
               {"(\\d+(\\.\\d+)?|\\.\\d+)(E(\\+|-)?\\d+)?", "number"},
               {"\\+", "plus"},
               {"-", "minusHyph"},
               {"\\*", "timesAst"},
               {"/", "divisionSlash"},
               {"\\(", "leftParen"},
               {"\\)", "rightParen"},
               {"\\[", "leftBracket"},
               {"\\]", "rightBracket"},
               {"\\^", "exponentCaret"}};
    
    public static final HashMap<String,String[]> tokenMap = new HashMap<String,String[]>();
    static {
        tokenMap.put("space", new String[]{});
        tokenMap.put("minusHyph", new String[]{"minus"});
        tokenMap.put("timesAst", new String[]{"times"});
        tokenMap.put("divisionSlash", new String[]{"division"});
        tokenMap.put("exponentCaret", new String[]{"exponent"});
        tokenMap.put("piWord", new String[]{"pi"});
    }
    
    private static final String noTokenMsg = "invalid token";
    
    private static boolean debug = true;
    
    public static Expr parseExpr(String string) throws ParseException {
        return parseExpr(string, new Context());
    }
    
    public static Expr parseExpr(String string, Context context) throws ParseException {
        return parseExpr(mapTokens(new ExprTokenParser(context).parseTokenList(
                new Tokenizer(tokens).tokenize(string))), context);
    }
    
//     public static Expr parseExpr(ArrayList<Object> tokened) throws ParseException {
//         return parseExpr(tokened, new Context());
//     }
    
    public static Expr parseExpr(TokenList<Object> tokened, Context context) throws ParseException {
        return parse(tokened, context).tokenValue;
    }
    
    public static Token<Expr> parse(TokenList<Object> tokened, Context context) throws ParseException {
        Token[][] levelDelims = {{new Token<Object>("leftParen"), new Token<Object>("rightParen")},
                                 {new Token<Object>("leftBracket"), new Token<Object>("rightBracket")}};
        return new LevelsParser(levelDelims, new ExprLevelParser(context))
                .withAfterPopHandler(new ParenFunctioning()).parseLevels(tokened)
                .castValueTo(Expr.class);
    }
    
 //    public static Expr parseLevel(ArrayList<Object> tokened) throws ParseException {
//         return parseLevel(tokened, new Context());
//     }
    
    public static TokenList<Object> mapTokens(TokenList<Object> tokened) {
        // if (debug) System.err.println("mapping tokens…");
        tokened = (TokenList<Object>) tokened.clone();
        int indexOn;
        
        indexOn = 0;
        while (indexOn < tokened.size()) {
            Token<Object> tokenOn = tokened.get(indexOn);
            Object tokenValueOn = tokenOn.tokenValue;
            // if (debug) System.err.println("on token: "+tokenOn);
            
            if (tokenValueOn instanceof String && tokenMap.containsKey(tokenValueOn)) {
                List<Token<Object>> subList = tokened.subList(indexOn, indexOn + 1);
                String[] replacement = tokenMap.get(tokenValueOn);
                Token<Object>[] replacementTokens = new Token[replacement.length];
                for (int i = 0; i < replacement.length; i++) {
                    replacementTokens[i] = new Token<Object>(replacement[i], tokenOn, tokenOn);
                }
                
                subList.clear();
                subList.addAll(Arrays.asList(replacementTokens));
                
                indexOn--;
            }
            
            indexOn++;
        }
        
        if (debug) System.err.println("mapped to: " + tokened);
        // if (debug) System.err.println("…done mapping tokens");
        return tokened;
    }
    
    public static String generateParseMesg(String input, ParseException e) {
        // if (!e.getMessage().equals(noTokenMsg)) return null;
        
        String string = input+"\n";
        for (int i = e.getErrorOffset(); i>0; i--) {
            string = string.concat(" ");
        }
        string = string.concat("^");
        
        return string;
    }
    
    public static void noDebug() {
        debug = false;
    }
    
    public Expr parseSExpr(String input) {
        
        
        return null;
    }
    
}
