package langdon.math;

import java.util.HashMap;

public class Conditional extends Expr {
    
    public Expr ifIs;
    public Expr then;
    
    private Conditional(Expr ifIs, Expr then) {
        this.ifIs = ifIs;
        this.then = then;
    }
    
    public static Expr make(Expr ifIs, Expr then) {
        Conditional con = new Conditional(ifIs, then);
        return con.simplify();
    }
    
    private Expr simplify() {
        if (ifIs.isTrue()) return then;
        if (ifIs.isFalse()) return new Undef();
        return this;
    }
    
    public String pretty() {
        return then + " if " + ifIs;
    }
    
    public int sign() {
        return 2;
    }
    
    public Expr copyPass(HashMap<Expr, Expr> subs) {
        return make(ifIs.copy(subs), then.copy(subs));
    }
    
    public boolean equalsExpr(Expr expr) {
        return false;
    }
    
    public Expr deriv(Var respected) {
        return new Conditional(ifIs, then.deriv(respected));
    }
    
}
