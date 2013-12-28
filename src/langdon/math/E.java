package langdon.math;

import langdon.math.*;

import java.util.ArrayList;
import java.util.HashMap;

public class E extends Constant {
    
    public E() {
    }
    
    public Expr deriv(Var respected) {
        return Number.make(0);
    }
    
    public String pretty() {
        return "e";
    }
    
    public boolean equalsExpr(Expr expr) {
        return expr instanceof E;
    }
    
    public boolean notEqualsExpr(Expr expr) {
        return false;
    }
    
    public Expr copyPass(HashMap<Expr, Expr> subs) {
        return new E();
    }
    
    public int sign() {
        return 1;
    }

}
