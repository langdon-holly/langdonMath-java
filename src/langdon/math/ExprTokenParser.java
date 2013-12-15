package langdon.math;

import java.util.regex.*;
import java.text.ParseException;

import langdon.util.*;

public class ExprTokenParser implements TokenParser {

    public Context context;
    
    public ExprTokenParser() {
        this.context = null;
    }
    
    public ExprTokenParser(Context context) {
        this.context = context;
    }
    
    public Token parseToken(String tokenString, String matched, ParseContext parseContext,
            String origString, int begin, int end) throws ParseException {
        if (!(parseContext instanceof Context)) throw new IllegalArgumentException();
        context = (Context) parseContext;
        return parseToken(tokenString, matched, origString, begin, end);
    }
    
    
    public Token parseToken(String tokenString, String matched,
            String origString, int begin, int end) throws ParseException {
        if (tokenString.equals("piWord")) {
            return new Token<Object>(new Pi(), origString, begin, end);
        }
        else if (tokenString.equals("e")) {
            return new Token<Object>(new E(), origString, begin, end);
        }
        else if (tokenString.equals("lonely-E")) {
            throw new ParseException("E is lonely", begin);
        }
        else if (tokenString.equals("lonely-d")) {
            throw new ParseException("d is lonely", begin);
        }
        else if (tokenString.equals("i")) {
            // if (debug) System.err.println("i am not yet supported");
            throw new ParseException("i am not yet supported", begin);
        }
        else if (tokenString.equals("var")) {
            return new Token<Object>(context.getVar(matched.charAt(0)), origString, begin, end);
        }
        else if (tokenString.equals("number")) {
            return new Token<Object>(langdon.math.Number.make(Double.parseDouble(matched)), origString, begin, end);
        }
        else if (tokenString.equals("derivativeFunc")) {
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
            if (firstNum == null || lastNum == null || Integer.parseInt(firstNum) != Integer.parseInt(lastNum)) throw new ParseException("derivative degrees don't match", begin);
            PartialParseExpr partial = new PartialParseExpr("derivativeFunc");
            partial.put("character", character.charAt(0));
            partial.put("degree", Integer.parseInt(firstNum));
            return new Token<Object>(partial, origString, begin, end);
        }
        else if (tokenString.equals("varDerivative")) {
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
            if (firstNum == null || lastNum == null || Integer.parseInt(firstNum) != Integer.parseInt(lastNum)) throw new ParseException("derivative degrees don't match", begin);
            return new Token<Object>(Derivative.make(context.getVar(var.charAt(0)), context.getVar(character.charAt(0)), Integer.parseInt(firstNum)), origString, begin, end);
        }
        else if (tokenString.equals("undef")) {
            return new Token<Object>(new Undef(), origString, begin, end);
        }
        
        return null;
    }
    
}
