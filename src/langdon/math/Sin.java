package langdon.math;

import langdon.math.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Sin extends Function {
    
    private static HashMap<Expr, Expr> values = new HashMap<Expr, Expr>();
    static {
        values.put(Number.make(0), Number.make(0)); // 0
        values.put(Number.make(1), Number.make(0)); // pi
        values.put(Number.make(0.5), Number.make(1)); // pi/2
        values.put(Number.make(1.5), Number.make(-1)); // 3pi/2
        values.put(Division.make(Number.make(1), Number.make(6)), Number.make(0.5)); // pi/6
        values.put(Division.make(Number.make(5), Number.make(6)), Number.make(0.5)); // 5pi/6
        values.put(Division.make(Number.make(-1), Number.make(6)), Number.make(-0.5)); // -pi/6
        values.put(Division.make(Number.make(-5), Number.make(6)), Number.make(-0.5)); // -5pi/6
        values.put(Number.make(0.25),
                   Division.make(Number.make(1), Exponent.make(Number.make(2), Number.make(0.5)))); // pi/4
        values.put(Number.make(0.75),
                   Division.make(Number.make(1), Exponent.make(Number.make(2), Number.make(0.5)))); // 3pi/4
        values.put(Number.make(-0.25),
                   Division.make(Number.make(-1), Exponent.make(Number.make(2), Number.make(0.5)))); // -pi/4
        values.put(Number.make(-0.75),
                   Division.make(Number.make(-1), Exponent.make(Number.make(2), Number.make(0.5)))); // -3pi/4
        values.put(Division.make(Number.make(1), Number.make(3)),
                   Division.make(Exponent.make(Number.make(3), Number.make(0.5)), Number.make(2))); // pi/3
        values.put(Division.make(Number.make(2), Number.make(3)),
                   Division.make(Exponent.make(Number.make(3), Number.make(0.5)), Number.make(2))); // 2pi/3
        values.put(Division.make(Number.make(-1), Number.make(3)),
                   Division.make(Exponent.make(Number.make(3), Number.make(0.5)), Number.make(-2))); // -pi/3
        values.put(Division.make(Number.make(-2), Number.make(3)),
                   Division.make(Exponent.make(Number.make(3), Number.make(0.5)), Number.make(-2))); // -2pi/3
    }
    
    private Expr ofExpr;
    
    private Sin(Expr ofExpr) {
        this.ofExpr = ofExpr;
    }
    
    public static Expr make(Expr ofExpr) {
        Sin sin = new Sin(ofExpr);
        return sin.simplify();
    }
    
    public static Expr make(ArrayList<? extends Expr> exprs) {
        return make(exprs.get(0));
    }
    
    public Expr deriv(Var inTermsOf) {
        return Product.make(ofExpr.deriv(inTermsOf), Cos.make(ofExpr));
    }
    
    public ArrayList<Expr> getExprs() {
        ArrayList<Expr> arrayList = new ArrayList<Expr>();
        arrayList.add(ofExpr);
        return arrayList;
    }
    
    private Expr simplify() {
        if (hasUndef()) return new Undef();
        if (ofExpr.isConstant()) {
            Expr dividedBy2Pi = Division.make(ofExpr, Product.make(Number.make(2), new Pi()));
            
            for (Expr key : values.keySet()) {
                Expr subtracted = Sum.make(dividedBy2Pi, Division.make(key, Number.make(-2)));
                if (subtracted instanceof Number && ((Number) subtracted).isInt()) return values.get(key);
            }
        }
        
        return this;
    }
    
    public String pretty() {
        String string;
        boolean parens = ofExpr.functionalParens();
        
        string = "sin";
        if (!parens) string = string.concat(" ");
                
        string = string.concat((parens?"(":"") + ofExpr.pretty() + (parens?")":""));
        
        return string;
    }
    
    public boolean equalsExpr(Expr expr) {
        if (expr == null) return false;
        if (expr == this) return true;
        if (!(expr instanceof Sin)) return false;
        
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
