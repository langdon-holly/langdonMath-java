package langdon.math;

import langdon.util.*;
import langdon.math.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Exponent extends Operation {
    
    private Expr base;    
    private Expr exponent;
    
    private Exponent(Expr base, Expr exponent) {
        this.base = base;
        this.exponent = exponent;
    }
    
    public static Expr make(Expr base, Expr exponent) {
        return make(base, exponent, true);
    }
    
    public static Expr make(Expr base, Expr exponent, boolean simplify) {
        Exponent exp = new Exponent(base, exponent);
        return simplify ? exp.simplify() : exp;
    }
    
    public static Expr make(ArrayList<? extends Expr> exprs) {
        return make(exprs.get(0), exprs.get(1));
    }
    
    public Expr deriv(Var inTermsOf) {
        if (exponent.isConstant()) {
            ArrayList<Expr> tmp = new ArrayList<Expr>();
            tmp.add(exponent);
            tmp.add(Exponent.make(base, Sum.make(exponent, Number.make(-1))));
            tmp.add(base.deriv(inTermsOf));
            return Product.make(tmp);
        }
        if (base.isConstant()) {
            ArrayList<Expr> tmp = new ArrayList<Expr>();
            tmp.add(Logarithm.make(new E(), base));
            tmp.add(this.simplify());
            tmp.add(exponent.deriv(inTermsOf));
            return Product.make(tmp);
        }
        return new Exponent(new E(), Product.make(exponent, Logarithm.make(new E(), base))).deriv(inTermsOf);
        // throw new UnsupportedOperationException("derivative of " + this);
    }
    
    public Integer classOrderPass() {
        if (isFunctionPass()) return classOrder.get(Sin.class);
        
        return super.classOrderPass();
    }
    
    public boolean isSqrt() {
        return exponent.equalsExpr(Number.make(0.5));
    }
    
    public boolean isFunctionPass() {
        if (isSqrt()) return true;
        
        return super.isFunctionPass();
    }
    
    public Integer printLevelLeftPass() {
        if (isFunctionPass()) return classOrderNum - 1;
        
        return super.printLevelLeftPass();
    }
    
    public Integer printLevelRightPass() {
        if (isFunctionPass()) return Expr.classPersonalLevelRight(Sin.class);
        
        return super.printLevelRightPass();
    }
    
    public boolean firstParenPrint() {
        if (isFunctionPass()) return false;
        
        return super.firstParenPrint();
    }
    
    public String pretty() {
        if (exponent.equalsExpr(Number.make(0.5))) return "sqrt" + (base.functionalParens()?"(":" ") + base.pretty() + (base.functionalParens()?")":"");
        
        String string = new String();
        
        Integer thisClassOrder = this.classOrder();
        
        boolean baseParens = false;
        if (thisClassOrder > base.printLevelRight() || base.isNegated()) baseParens = true;
        boolean exponentParens = false;
        if (thisClassOrder > exponent.printLevelLeft()) exponentParens = true;
        
        string = string.concat((baseParens?"(":"") + base.pretty() + (baseParens?")":""));
        string = string.concat("^");
        string = string.concat((exponentParens?"(":"") + exponent.pretty() + (exponentParens?")":""));
        
        return string;
    }
    
    private Expr simplify() {
        if (hasUndef()) return new Undef();
        if (base instanceof Number && ((Number) base).val() == 0 && exponent instanceof Number && ((Number) exponent).val() == 0) return new Undef(); // 0^0
        if (base instanceof Number && ((Number) base).val() == 0) return Number.make();
        if (exponent instanceof Number && ((Number) exponent).val() == 0) return Number.make(1);
        if (base instanceof Number && ((Number) base).val() == 1) return Number.make(1);
        if (exponent instanceof Number && ((Number) exponent).val() == 1) return base;
        if (base instanceof Number && exponent instanceof Number) {
            if ((Math.pow(Math.pow(((Number) base).val(), ((Number) exponent).val()), 1/((Number) exponent).val()) == ((Number) base).val()) && (Math.pow(Math.pow(((Number) base).val(), ((Number) exponent).val()), 1/((Number) exponent).val()) == Math.abs(((Number) base).val()))) {
                return Number.make(Math.pow(((Number) base).val(), ((Number) exponent).val()));
            }
        }
        if (exponent instanceof Logarithm && base.equalsExpr(((Operation) exponent).getExprs().get(0))) return ((Operation) exponent).getExprs().get(1);
        if (exponent instanceof Product) {
            for (int i = 0; i < ((Operation) exponent).getExprs().size(); i++) {
                if (!(Exponent.make(base, ((Operation) exponent).getExprs().get(i)) instanceof Exponent)) {
                    ArrayList<Expr> newProduct = ((Operation) exponent).getExprs();
                    return make(Exponent.make(base, newProduct.remove(i)), Product.make(newProduct));
                }
            }
        }
        if (base instanceof Exponent) return make(((Operation) base).getExpr(0), Product.make(((Operation) base).getExpr(1), exponent));
        if (base instanceof Division) return Division.make(Exponent.make(((Operation) base).getExpr(0), exponent), Exponent.make(((Operation) base).getExpr(1), exponent.copy()));
        
        return this;
    }
    
    public ArrayList<Expr> getExprs() {
        ArrayList<Expr> arrayList = new ArrayList<Expr>();
        arrayList.add(base);
        arrayList.add(exponent);
        return arrayList;
    }
    
    public boolean equalsExpr(Expr expr) {
        if (expr == null) return false;
        if (expr == this) return true;
        if (!(expr instanceof Exponent)) return false;
        
        if (base.equalsExpr(((Operation) expr).getExprs().get(0)) && exponent.equalsExpr(((Operation) expr).getExprs().get(1))) return true;
        
        return false;
    }
    
    public Expr copyPass(HashMap<Expr, Expr> subs) {
        return make(base.copy(subs), exponent.copy(subs));
    }
    
    public int sign() {
        return base.sign();
    }
    
}