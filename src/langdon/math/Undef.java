package langdon.math;

import java.util.HashMap;

import langdon.math.*;

public class Undef extends Expr {
    
    public Expr deriv(Var inTermsOf) {
        return new Undef();
    }
    
    public String pretty() {
        return "undef";
    }
    
    public boolean equalsExpr(Expr expr) {
        return false;
    }
    
    public Expr copyPass(HashMap<Expr, Expr> substitutions) {
        return new Undef();
    }
    
    public int sign() {
        return 2;
    }
    
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        return o instanceof Undef;
    }
    
}
