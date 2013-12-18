package langdon.math;

import java.util.ArrayList;
import java.util.Collection;

public abstract class Operation extends Expr {
    
    public abstract ArrayList<Expr> getExprs();
    
    public static Expr make(ArrayList<? extends Expr> exprs) {
        throw new UnsupportedOperationException();
    }
    
    public static <E extends Expr> Expr make(Collection<E> exprs) {
        return make(new ArrayList<E>(exprs));
    }
    
    public Expr getExpr(int index) {
        return getExprs().get(index);
    }
    
    public Expr lastExpr() {
        ArrayList<Expr> exprs = getExprs();
        return exprs.get(exprs.size() - 1);
    }
    
    public boolean firstParenPrint() {
        Integer exprLevelRight = this.getExpr(0).printLevelRight();
        return (exprLevelRight != null && this.classOrder() > exprLevelRight) || this.getExpr(0).firstParenPrint();
    }
    
    public boolean hasUndef() {
        return getExprs().contains(new langdon.math.Undef());
    }
    
}
