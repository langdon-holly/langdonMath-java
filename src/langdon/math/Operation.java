package langdon.math;

import java.util.ArrayList;
import java.util.Collection;

import langdon.util.ArrayLists;

public abstract class Operation extends Expr {
    
    public abstract ArrayList<Expr> getExprs();
    
    // to be overrided
    public static Expr make(ArrayList<? extends Expr> exprs) {
        throw new UnsupportedOperationException();
    }
    
    // to be overrided
    public static Expr makeDefined(ArrayList<? extends Expr> exprs) {
        throw new UnsupportedOperationException();
    }
    
    //public static <E extends Expr> Expr make(Collection<E> exprs) {
    //    return make(new ArrayList<E>(exprs));
    //}
    
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
    
    public Expr conditions() {
        return conditions(getExprs());
    }
    
    public static Expr conditions(ArrayList<? extends Expr> exprs) {
        ArrayList<Expr> condsArr = new ArrayList<Expr>();
        for (Expr expr : exprs) {
            condsArr.add(expr.condition());
        }
        return And.make(condsArr);
    }
    
    public ArrayList<Expr> defineds() {
        return defineds(getExprs());
    }
    
    public static ArrayList<Expr> defineds(ArrayList<? extends Expr> exprsOrig) {
        ArrayList<Expr> exprs = new ArrayList<Expr>(exprsOrig);
        for (int i = 0; i < exprs.size(); i++) {
            Expr exprOn = exprs.get(i);
            if (exprOn instanceof Undef) {
//                 exprs.remove(i);
//                 i--;
//                 continue;
                return null;
            }
            if (exprOn instanceof Conditional) {
                exprs.set(i, exprOn.defined());
            }
        }
        return exprs;
    }
    
//     public static Expr conditionsAndDefineds(ArrayList<? extends Expr> exprs) {
//         Expr conditions = conditions(exprs);
//         exprs = defineds(exprs);
//         return conditions;
//     }
    
    public Expr conditioned() {
        Expr conditions = conditions();
        try {
            if (!conditions.equalsExpr(Expr.yep())) {
                ArrayList<Expr> defineds = defineds();
                if (defineds == null) return new Undef();
                return Conditional.make(conditions,
                        (Expr) this.getClass().getMethod("makeDefined", ArrayList.class).invoke(null, defineds()));
            }
        } catch(Exception e) {
            throw new RuntimeException(e + "\nmake(ArrayList) failed on " + this.getClass().getSimpleName());
        }
        return null;
    }
    
    public boolean equalsExpr(Expr expr) {
        if (expr == null) return false;
        if (expr == this) return true;
        if (!this.getClass().isAssignableFrom(expr.getClass())) return false;
        
        if (ArrayLists.elemExprsEqual(this.getExprs(), ((Operation) expr).getExprs())) return true;
        
        return false;
    }
    
    public boolean notEqualsExpr(Expr expr) {
        if (expr == null) return false;
        if (expr == this) return false;
        if (!this.getClass().isAssignableFrom(expr.getClass())) return false;
        
        ArrayList<Expr> al1 = this.getExprs();
        ArrayList<Expr> al2 = ((Operation) expr).getExprs();
        if (al1.size() != al2.size()) return false;
        
        boolean diffYet = false;
        for (int i = 0; i < al1.size(); i++) {
            if (al1.get(i).notEqualsExpr(al2.get(i))) {
                if (diffYet) return false;
                diffYet = true;
            }
        }
        
        return diffYet;
    }
    
//     public Expr printSimplify() {
//         ArrayList<Expr> simplified = new ArrayList<Expr>();
//         for (Expr expr : this.getExprs()) {
//             simplified.add(expr.printSimplify());
//         }
//         
//         try {
//             return ((Expr) this.getClass().getMethod("make", ArrayList.class).invoke(null, simplified)).printSimplifyPass();
//         } catch(Exception e) {
//             throw new RuntimeException();
//         }
//     }
    
//     public Expr condAndSimplify() {
//         Expr conditioned = this.conditioned();
//         if (conditioned != null) return conditioned;
//         return this.simplify();
//     }
    
}
