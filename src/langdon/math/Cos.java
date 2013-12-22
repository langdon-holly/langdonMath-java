package langdon.math;

import langdon.math.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Cos extends Function {
    
    private Expr ofExpr;
    
    private Cos(Expr ofExpr) {
        this.ofExpr = ofExpr;
    }
    
    public static Expr make(Expr ofExpr) {
        Cos cos = new Cos(ofExpr);
        return cos.simplify();
    }
    
    public static Expr make(ArrayList<? extends Expr> exprs) {
        return make(exprs.get(0));
    }
    
    public Expr deriv(Var respected) {
        return Product.make(ofExpr.deriv(respected), Product.negative(Sin.make(ofExpr)));
    }
    
    public ArrayList<Expr> getExprs() {
        ArrayList<Expr> arrayList = new ArrayList<Expr>();
        arrayList.add(ofExpr);
        return arrayList;
    }
    
    private Expr simplify() {
        if (hasUndef()) return new Undef();
        Expr sinSimplified = Sin.make(Sum.make(ofExpr, Division.make(new Pi(), Number.make(2))));
        if (sinSimplified.isConstant()) return sinSimplified;
        
        return this;
    }
    
    public String pretty() {
        String string;
        boolean parens = ofExpr.functionalParens();
        
        string = "cos";
        if (!parens) string = string.concat(" ");
                
        string = string.concat((parens?"(":"") + ofExpr.pretty() + (parens?")":""));
        
        return string;
    }
    
    public boolean equalsExpr(Expr expr) {
        if (expr == null) return false;
        if (expr == this) return true;
        if (!(expr instanceof Cos)) return false;
        
        if (ofExpr.equalsExpr(((Operation) expr).getExprs().get(0))) return true;
        
        return false;
    }
    
    public Expr copyPass(HashMap<Expr, Expr> subs) {
        return make(ofExpr.copy(subs));
    }
    
    public int sign() {
        return 2;
    }
    
}
