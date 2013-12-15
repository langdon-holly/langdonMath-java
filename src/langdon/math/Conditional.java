package langdon.math;

public class Conditional<T extends Expr> extends Expr {
    
    public BoolExpr ifIs;
    public T then;
    
    private Conditional(BoolExpr ifIs, T then) {
        this.ifIs = ifIs;
        this.then = then;
    }
    
    public Expr make(BoolExpr ifIs, T then) {
        Conditional con = new Conditional(ifIs, then);
        return con.simplify();
    }
    
    private Expr simplify() {
        if (ifIs.isTrue()) return then;
        if (ifIs.isFalse()) return new Undef();
        return this;
    }
    
}