package langdon.math;

import java.util.ArrayList;

public abstract class Operation extends Expr {
    
    public abstract ArrayList<Expr> getExprs();
    
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