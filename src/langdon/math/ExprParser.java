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
            Expr expr = parse(args[0]);
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
    
    public static final Object[][] breakOrder = {{"plus"},
                                                 {"sin", "cos", "tan", "cot", "sec", "csc", "log", "ln", new PartialParseExpr("derivativeFunc"), "sqrt"},
                                                 {"times", "division"},
                                                 {"minus"},
                                                 {"exponent"}};
    
    public static final Token<Object>[][] tokenBreakOrder = new Token[breakOrder.length][];
    static {
        for (int i = 0; i < breakOrder.length; i++) {
            Token<Object>[] thisLevel = new Token[breakOrder[i].length];
            for (int j = 0; j < breakOrder[i].length; j++) {
                thisLevel[j] = new Token<Object>(breakOrder[i][j]);
            }
            tokenBreakOrder[i] = thisLevel;
        }
    }
    
    public static final HashMap<String,Integer> parseDir = new HashMap<String,Integer>();
    static {
        parseDir.put("plus", 0);
        parseDir.put("sin", -1); // right-associative
        parseDir.put("cos", -1);
        parseDir.put("tan", -1);
        parseDir.put("cot", -1);
        parseDir.put("sec", -1);
        parseDir.put("csc", -1);
        parseDir.put("log", -1);
        parseDir.put("ln", -1);
        parseDir.put("derivativeFunc", -1);
        parseDir.put("sqrt", -1);
        parseDir.put("times", 1);
        parseDir.put("division", 1); // left-associative
        parseDir.put("minus", -1);
        parseDir.put("exponent", -1);
    }
    
    public static final ArrayList<Object> functions = new ArrayList<Object>();
    static {
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
    
    private static final String noTokenMsg = "invalid token";
    
    private static boolean debug = true;
    
    public static Expr parse(String string) throws ParseException {
        return parse(string, new Context());
    }
    
    public static Expr parse(String string, Context context) throws ParseException {
        TokenList<Object> tokened = tokenize(string, context);
        return parse(tokened, context);
    }
    
//     public static Expr parse(ArrayList<Object> tokened) throws ParseException {
//         return parse(tokened, new Context());
//     }
    
    public static Expr parse(TokenList<Object> tokened, Context context) throws ParseException {
        tokened = mapTokens(tokened);
        int indexOn;
        
        ArrayList<TokenList<Object>> parenContextStack = new ArrayList<TokenList<Object>>();
        parenContextStack.add(new TokenList<Object>(tokened.fromStrOffset));
        ArrayList<String> parenStack = new ArrayList<String>();
        parenStack.add("string");
        
        indexOn = 0;
        while (indexOn < tokened.size()) {
            if (debug) System.err.println("--------------");
            if (debug) System.err.println("contexts: " + parenContextStack);
            if (debug) System.err.println("parens:   " + parenStack);
            Token<Object> tokenOn = tokened.get(indexOn);
            Object tokenValueOn = tokenOn.tokenValue;
            if (debug) System.err.println("token:    " + tokenOn);
            
            if (tokenValueOn.equals("leftParen")) {
                if (debug) System.err.println("pushing paren…");
                parenContextStack.add(new TokenList<Object>(tokenOn.fromStrOffset));
                parenStack.add("paren");
            }
            else if (tokenValueOn.equals("rightParen")) {
                if (debug) System.err.println("popping paren…");
                if (!parenStack.get(parenStack.size() - 1).equals("paren")) {
                    throw new ParseException("unopened paren", indexOn);
                }
                parenContextStack.get(parenContextStack.size() - 2).add(parseLevel(parenContextStack.remove(parenContextStack.size() - 1), context).castValueTo(Object.class));
                parenStack.remove(parenStack.size() - 1);
                
                TokenList<Object> currentContext = parenContextStack.get(parenContextStack.size() - 1);
                for (int itr = currentContext.size() - 2; itr >= 0; itr--) {
                    if (!(currentContext.get(itr).tokenValue instanceof Subscript)) {
                        if (functions.contains(currentContext.get(itr).tokenValue)) {
                            if (debug) System.err.println("currentContext before: " + currentContext);
                            List<Token<Object>> mkFuncList = currentContext.subList(itr, currentContext.size());
                            // System.err.println(mkFuncList);
                            Token<Expr> parsedFunc = parseLevel(new TokenList<Object>(mkFuncList), context);
                            mkFuncList.clear();
                            mkFuncList.add(parsedFunc.castValueTo(Object.class));
                            // System.err.println(mkFuncList);
                            if (debug) System.err.println("currentContext after:  " + currentContext);
                        }
                        break;
                    }
                }
                
                if (debug) System.err.println("…popped paren");
            }
            else if (tokenValueOn.equals("leftBracket")) {
                if (debug) System.err.println("pushing bracket…");
                parenContextStack.add(new TokenList<Object>(tokenOn.fromStrOffset));
                parenStack.add("bracket");
            }
            else if (tokenValueOn.equals("rightBracket")) {
                if (debug) System.err.println("popping bracket…");
                if (!parenStack.get(parenStack.size() - 1).equals("bracket")) {
                    throw new ParseException("unopened bracket", indexOn);
                }
                Token<Expr> subscript = parseLevel(parenContextStack.remove(parenContextStack.size() - 1), context);
                parenContextStack.get(parenContextStack.size() - 1).add(new Token<Subscript>(new Subscript(subscript.tokenValue), subscript, subscript).castValueTo(Object.class));
                parenStack.remove(parenStack.size() - 1);
                if (debug) System.err.println("…popped bracket");
            }
            else {
                parenContextStack.get(parenContextStack.size() - 1).add(tokenOn);
            }
            
            indexOn++;
        }
        
        Token<Expr> bigToken = parseLevel(parenContextStack.remove(parenContextStack.size() - 1), context);
        Expr expr = bigToken.tokenValue;
        
        if (!parenContextStack.isEmpty()) {
            throw new ParseException("unclosed paren", indexOn - 1);
        }
        
        return expr;
    }
    
 //    public static Expr parseLevel(ArrayList<Object> tokened) throws ParseException {
//         return parseLevel(tokened, new Context());
//     }
    
    public static Token<Expr> parseLevel(TokenList<Object> tokened, Context context) throws ParseException {
        if (debug) System.err.println("parsing     " + tokened);
        
        if (tokened.size() == 0) throw new ParseException("token(s) expected, but not found (check your syntax)", tokened.fromStrOffset);
        Token<Object> firstToken = tokened.get(0);
        if (tokened.size() == 1 && firstToken.tokenValue instanceof Expr) {
            return firstToken.castValueTo(Expr.class);
        }
        
        tokened = alterOperations(tokened);
        
        for (int i = 0; i < Array.getLength(breakOrder); i++) {
            if (ArrayLists.containsIn(Token.getValues(tokened), breakOrder[i])) {
                if (debug) System.err.println("splitting on " + Arrays.toString(tokenBreakOrder[i]));
                
                int tokenParseDir = parseDir.get(breakOrder[i][0]);
                
                Token<Object>[] splitOnA = new Token[]{new Token<Object>("none")};
                
                ArrayList<TokenList<Object>> splitted = TokenList.toArrTokenList(ArrayLists.split(tokened, tokenBreakOrder[i], tokenParseDir, splitOnA));
                if (splitted.get(0).fromStrOffset == null) splitted.get(0).fromStrOffset = tokened.fromStrOffset;
                if (splitted.get(splitted.size() - 1).fromStrOffset == null) {
                    splitted.get(splitted.size() - 1).fromStrOffset = tokened.get(tokened.size() - 1).fromStrEnd;
                }
                
                Token<Object> splitOnToken = splitOnA[0];
                Object splitOn = splitOnToken.tokenValue;
                if (debug) System.err.println("splitted: " + splitted);
                
                if (splitOn.equals("plus")) {
                    ArrayList<Token<Expr>> parsed = new ArrayList<Token<Expr>>();
                    for (TokenList<Object> toParse : splitted) {
                        parsed.add(parseLevel(toParse, context));
                    }
                    // if (debug) System.err.println("sum: " + Sum.make(parsed));
                    return new Token<Expr>(Sum.make(Token.getValues(parsed)), parsed.get(0), parsed.get(parsed.size() - 1));
                }
                else if (splitOn.equals("minus")) {
                    Token<Expr> minused = parseLevel(splitted.remove(1), context);
                    splitted.get(0).add(new Token<Object>(Product.make(langdon.math.Number.make(-1d), minused.tokenValue), splitOnToken, minused));
                    return parseLevel(splitted.get(0), context);
                }
                else if (splitOn.equals("times")) {
                    Token<Expr> firstFactor = parseLevel(splitted.get(0), context);
                    Token<Expr> secondFactor = parseLevel(splitted.get(1), context);
                    return new Token<Expr>(Product.make(firstFactor.tokenValue, secondFactor.tokenValue), firstFactor, secondFactor);
                }
                else if (splitOn.equals("exponent")) {
                    Token<Expr> base = parseLevel(splitted.get(0), context);
                    Token<Expr> exponent = parseLevel(splitted.get(1), context);
                    return new Token<Expr>(Exponent.make(base.tokenValue, exponent.tokenValue), base, exponent);
                }
                else if (splitOn.equals("division")) {
                    Token<Expr> dividend = parseLevel(splitted.get(0), context);
                    Token<Expr> divisor = parseLevel(splitted.get(1), context);
                    return new Token<Expr>(Division.make(dividend.tokenValue, divisor.tokenValue), dividend, divisor);
                }
                else if (splitOn.equals("log")) {
                    Expr base = (splitted.get(1).get(0).tokenValue instanceof Subscript) ? ((Subscript) splitted.get(1).remove(0).tokenValue).getExpr() : langdon.math.Number.make(10d);
                    Token<Expr> logOf = parseLevel(splitted.get(1), context);
                    splitted.get(0).add(new Token<Object>(Logarithm.make(base, logOf.tokenValue), splitOnToken, logOf));
                    return parseLevel(splitted.get(0), context);
                }
                else if (splitOn.equals("ln")) {
                    Token<Expr> logOf = parseLevel(splitted.get(1), context);
                    splitted.get(0).add(new Token<Object>(Logarithm.make(new E(), logOf.tokenValue), splitOnToken, logOf));
                    return parseLevel(splitted.get(0), context);
                }
                else if (splitOn.equals("sin")) {
                    Token<Expr> sinOf = parseLevel(splitted.get(1), context);
                    splitted.get(0).add(new Token<Object>(Sin.make(sinOf.tokenValue), splitOnToken, sinOf));
                    return parseLevel(splitted.get(0), context);
                }
                else if (splitOn.equals("cos")) {
                    Token<Expr> cosOf = parseLevel(splitted.get(1), context);
                    splitted.get(0).add(new Token<Object>(Cos.make(cosOf.tokenValue), splitOnToken, cosOf));
                    return parseLevel(splitted.get(0), context);
                }
                else if (splitOn.equals("tan")) {
                    Token<Expr> tanOf = parseLevel(splitted.get(1), context);
                    splitted.get(0).add(new Token<Object>(Division.make(Sin.make(tanOf.tokenValue), Cos.make(tanOf.tokenValue)), splitOnToken, tanOf));
                    return parseLevel(splitted.get(0), context);
                }
                else if (splitOn.equals("cot")) {
                    Token<Expr> cotOf = parseLevel(splitted.get(1), context);
                    splitted.get(0).add(new Token<Object>(Division.make(Cos.make(cotOf.tokenValue), Sin.make(cotOf.tokenValue)), splitOnToken, cotOf));
                    return parseLevel(splitted.get(0), context);
                }
                else if (splitOn.equals("sec")) {
                    Token<Expr> secOf = parseLevel(splitted.get(1), context);
                    splitted.get(0).add(new Token<Object>(Division.make(langdon.math.Number.make(1), Cos.make(secOf.tokenValue)), splitOnToken, secOf));
                    return parseLevel(splitted.get(0), context);
                }
                else if (splitOn.equals("csc")) {
                    Token<Expr> cscOf = parseLevel(splitted.get(1), context);
                    splitted.get(0).add(new Token<Object>(Division.make(langdon.math.Number.make(1), Sin.make(cscOf.tokenValue)), splitOnToken, cscOf));
                    return parseLevel(splitted.get(0), context);
                }
                else if (splitOn.equals(new PartialParseExpr("derivativeFunc"))) {
                    // if (debug) System.err.println(((PartialParseExpr) splitOn).hash);
                    Token<Expr> derivOf = parseLevel(splitted.get(1), context);
                    splitted.get(0).add(new Token<Object>(Derivative.make(derivOf.tokenValue, context.getVar((Character) ((PartialParseExpr) splitOn).get("character")),
                            (Integer) ((PartialParseExpr) splitOn).get("degree")), splitOnToken, derivOf));
                    return parseLevel(splitted.get(0), context);
                }
                else if (splitOn.equals("sqrt")) {
                    Token<Expr> sqrtOf = parseLevel(splitted.get(1), context);
                    splitted.get(0).add(new Token<Object>(Exponent.make(sqrtOf.tokenValue, langdon.math.Number.make(0.5)), splitOnToken, sqrtOf));
                    return parseLevel(splitted.get(0), context);
                }
                else {
                    if (debug) System.err.println(splitOn + " operation not yet supported");
                    throw new ParseException(splitOn + " operation not yet supported", splitOnToken.fromStrOffset);
                }
            }
        }
        
        throw new ParseException("no operation found", tokened.fromStrOffset);
    }
    
//     public static Subscript parseSubscript (ArrayList<Object> tokened) throws ParseException {
//         return new Subscript(parseLevel(tokened));
//     }
    
//     public static ArrayList<Object> tokenize(String string) throws ParseException {
//         return tokenize(string, new Context());
//     }
    
    public static TokenList<Object> tokenize(String string, Context context) throws ParseException {
        if (debug) System.err.println("tokenizing \"" + string + "\"");
        String string2 = string;
        TokenList<Object> tokened = new TokenList<Object>(0);
        HashMap<Character,Var> vars = new HashMap<Character,Var>();
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
                    
                    if (token.equals("piWord")) {
                        tokened.add(new Token<Object>(new Pi(), string, indexAt, indexAt + matcher.end()));
                    }
                    else if (token.equals("e")) {
                        tokened.add(new Token<Object>(new E(), string, indexAt, indexAt + matcher.end()));
                    }
                    else if (token.equals("lonely-E")) {
                        throw new ParseException("E is lonely", indexAt);
                    }
                    else if (token.equals("lonely-d")) {
                        throw new ParseException("d is lonely", indexAt);
                    }
                    else if (token.equals("i")) {
                        // if (debug) System.err.println("i am not yet supported");
                        throw new ParseException("i am not yet supported", indexAt);
                    }
                    else if (token.equals("var")) {
                        tokened.add(new Token<Object>(context.getVar(matched.charAt(0)), string, indexAt, indexAt + matcher.end()));
                    }
                    else if (token.equals("number")) {
                        tokened.add(new Token<Object>(langdon.math.Number.make(Double.parseDouble(matched)), string, indexAt, indexAt + matcher.end()));
                    }
                    else if (token.equals("derivativeFunc")) {
                        Pattern pattern1 = Pattern.compile("^d(?:\\^(\\d+))?/d([a-zA-Z])(?:\\^(\\d+))?$");
                        Matcher matcher1 = pattern1.matcher(matched);
                        matcher1.find();
                        String firstNum  = matcher1.group(1);
                        String character = matcher1.group(2);
                        String lastNum   = matcher1.group(3);
                        if (firstNum == null && lastNum == null) {
                            firstNum = "1";
                            lastNum = "1";
                        }
                        if (firstNum == null || lastNum == null || Integer.parseInt(firstNum) != Integer.parseInt(lastNum)) throw new ParseException("derivative degrees don't match", indexAt);
                        //tokened.add("derivativeFunc(" + character + "," + firstNum + ")");
                        PartialParseExpr partial = new PartialParseExpr("derivativeFunc");
                        //System.err.println("chrat"+character.charAt(0));
                        partial.put("character", character.charAt(0));
                        partial.put("degree", Integer.parseInt(firstNum));
                        tokened.add(new Token<Object>(partial, string, indexAt, indexAt + matcher.end()));
                    }
                    else if (token.equals("varDerivative")) {
                        Pattern pattern1 = Pattern.compile("^d(?:\\^(\\d+))?([a-zA-Z])/d([a-zA-Z])(?:\\^(\\d+))?$");
                        Matcher matcher1 = pattern1.matcher(matched);
                        matcher1.find();
                        String firstNum  = matcher1.group(1);
                        String var       = matcher1.group(2);
                        String character = matcher1.group(3);
                        String lastNum   = matcher1.group(4);
                        if (firstNum == null && lastNum == null) {
                            firstNum = "1";
                            lastNum = "1";
                        }
                        if (firstNum == null || lastNum == null || Integer.parseInt(firstNum) != Integer.parseInt(lastNum)) throw new ParseException("derivative degrees don't match", indexAt);
                        tokened.add(new Token<Object>(Derivative.make(context.getVar(var.charAt(0)), context.getVar(character.charAt(0)), Integer.parseInt(firstNum)), string, indexAt, indexAt + matcher.end()));
                    }
                    else if (token.equals("undef")) {
                        tokened.add(new Token<Object>(new Undef(), string, indexAt, indexAt + matcher.end()));
                    }
                    else {
                        tokened.add(new Token<Object>(token, string, indexAt, indexAt + matcher.end()));
                    }
                    
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
    
    public static TokenList<Object> alterOperations(TokenList<Object> tokened) {
        tokened = (TokenList<Object>) tokened.clone();
        
        for (int i = 1; i < tokened.size(); i++) {
            if (tokened.get(i - 1).tokenValue instanceof Expr && tokened.get(i).tokenValue instanceof Expr) {
                tokened.add(i, new Token<Object>("times", tokened.get(i).fromStr, tokened.get(i).fromStrOffset, tokened.get(i).fromStrOffset));
            }
            else if (tokened.get(i - 1).tokenValue instanceof Expr && tokened.get(i).valueEquals("minus")) {
                tokened.add(i, new Token<Object>("plus", tokened.get(i).fromStr, tokened.get(i).fromStrOffset, tokened.get(i).fromStrOffset));
            }
        }
        
        if (debug) System.err.println("altered to: " + tokened);
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
    
}