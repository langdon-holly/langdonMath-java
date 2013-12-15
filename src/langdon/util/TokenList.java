package langdon.util;

import java.util.ArrayList;
import java.util.Collection;

public class TokenList<T> extends ArrayList<Token<T>> {
    
    public Integer fromStrOffset = null;
    
    public TokenList(int fromStrOffset) {
        super();
        this.fromStrOffset = fromStrOffset;
    }
    
    public TokenList(Collection<Token<T>> tokens, int fromStrOffset) {
        super(tokens);
        this.fromStrOffset = fromStrOffset;
    }
    
    public TokenList(Collection<Token<T>> tokens) {
        super(tokens);
        if (size() > 0) fromStrOffset = get(0).fromStrOffset;
    }
    
    public static <T> ArrayList<TokenList<T>> toArrTokenList(Collection<ArrayList<Token<T>>> tokenArrArr) {
        ArrayList<TokenList<T>> tokenListList = new ArrayList<TokenList<T>>();
        for (Collection<Token<T>> tokenArr : tokenArrArr) {
            tokenListList.add(new TokenList<T>(tokenArr));
        }
        return tokenListList;
    }
    
}