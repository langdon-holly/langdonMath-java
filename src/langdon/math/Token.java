package langdon.math;

import java.util.ArrayList;

public class Token<T> {
    
    public T tokenValue;
    public String fromStr = null;
    public Integer fromStrOffset = null;
    public Integer fromStrEnd = null;
    
    public Token(T tokenValue, String fromStr, int fromStrOffset, int fromStrEnd) {
        this.tokenValue = tokenValue;
        this.fromStr = fromStr;
        this.fromStrOffset = fromStrOffset;
        this.fromStrEnd = fromStrEnd;
    }
    
    public Token(T tokenValue, Token fromStrOffsetToken, Token fromStrEndToken) {
        this.tokenValue = tokenValue;
        this.fromStr = fromStrOffsetToken.fromStr;
        this.fromStrOffset = fromStrOffsetToken.fromStrOffset;
        this.fromStrEnd = fromStrEndToken.fromStrEnd;
    }
    
    public Token(T tokenValue) {
        this.tokenValue = tokenValue;
    }
    
    public boolean valueEquals(Object o) {
        return tokenValue.equals(o);
    }
    
    public String toString() {
        return "([" + (tokenValue instanceof Expr ? ((Expr) tokenValue) : tokenValue) + "]"
                + (fromStr != null && fromStrOffset != null && fromStrEnd != null
                 ? "\"" + fromStr.substring(fromStrOffset, fromStrEnd) + "\"" : "") + ")";
    }
    
    public static <T> ArrayList<T> getValues(ArrayList<Token<T>> tokens) {
        ArrayList<T> values = new ArrayList<T>();
        for (Token<T> token : tokens) {
            values.add(token.tokenValue);
        }
        return values;
    }
    
    public <T> Token<T> castValueTo(Class<T> toClass) {
        return new Token<T>((T) tokenValue, fromStr, fromStrOffset, fromStrEnd);
    }
    
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof Token)) return false;
        
        return ((Token) o).tokenValue.equals(this.tokenValue);
    }
    
}