package langdon.math;

import langdon.math.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Pi extends Constant {
    
    public Pi() {
    }
    
    public Expr deriv(Var inTermsOf) {
        return Number.make(0);
    }
    
    public String pretty() {
        return "pi";
    }
    
    public boolean equalsExpr(Expr expr) {
        return expr instanceof Pi;
    }
    
    public Expr copyPass(HashMap<Expr, Expr> subs) {
        return new Pi();
    }
    
    public int sign() {
        return 1;
    }

}