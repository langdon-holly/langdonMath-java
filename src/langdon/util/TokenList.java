package langdon.util;

import java.util.ArrayList;
import java.util.Collection;

public class TokenList<T> extends ArrayList<Token<T>> {
    
    public Integer fromStrBegin = null;
    
    public TokenList(int fromStrBegin) {
        super();
        this.fromStrBegin = fromStrBegin;
    }
    
    public TokenList(Collection<Token<T>> tokens, int fromStrBegin) {
        super(tokens);
        this.fromStrBegin = fromStrBegin;
    }
    
    public TokenList(Collection<Token<T>> tokens) {
        super(tokens);
        if (size() > 0) fromStrBegin = get(0).fromStrBegin;
    }
    
    public static <T> ArrayList<TokenList<T>> toArrTokenList(Collection<ArrayList<Token<T>>> tokenArrArr) {
        ArrayList<TokenList<T>> tokenListList = new ArrayList<TokenList<T>>();
        for (Collection<Token<T>> tokenArr : tokenArrArr) {
            tokenListList.add(new TokenList<T>(tokenArr));
        }
        return tokenListList;
    }
    
    public <U> TokenList<U> castValuesTo(Class<U> toClass) {
        TokenList<U> newTokenList = new TokenList(fromStrBegin);
        for (Token<T> token : this) {
            newTokenList.add(token.castValueTo(toClass));
        }
        return newTokenList;
    }
    
}