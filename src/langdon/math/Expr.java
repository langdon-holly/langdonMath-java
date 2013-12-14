package langdon.math;

import java.util.ArrayList;
import java.util.HashMap;
import java.lang.reflect.Array;
import java.util.Arrays;

public abstract class Expr {
    
    private static final Class[][] classOrderA = {{Sum.class},
                                                  {Logarithm.class, Sin.class, Cos.class, Derivative.class},
                                                  {Product.class, Division.class},
                                                  {Exponent.class}};
    
    public static final HashMap<Class,Integer> classOrder = new HashMap<Class,Integer>();
    static {
        for (int i = 0; i < Array.getLength(classOrderA); i++) {
            for (int j = 0; j < Array.getLength(classOrderA[i]); j++) {
                classOrder.put(classOrderA[i][j], i);
            }
        }
    }
    
    public static final int classOrderNum = classOrderA.length;
    
    public static final Object[][] printLevelSidesAddA = {{Sin.class, null, -1},
                                                          {Product.class,   -1, 0},
                                                          {Exponent.class,   0, -1}};
    
    public static final HashMap<Class, Integer[]> printLevelSidesHM = new HashMap<Class, Integer[]>();
    static {
        for (Object[] objects : printLevelSidesAddA) {
            Integer thisClassOrder = classOrder.get((Class) objects[0]);
            // System.err.println(((Class) objects[0]).toString() + ".classOrder()=" + thisClassOrder);
            for (Class class1 : classOrderA[thisClassOrder]) {
                Integer[] printLevelSides = new Integer[2];
                printLevelSides[0] = objects[1] != null ? thisClassOrder + (Integer) objects[1] : null;
                printLevelSides[1] = objects[1] != null ? thisClassOrder + (Integer) objects[2] : null;
                printLevelSidesHM.put(class1, printLevelSides);
            }
        }
    }
    
    // private static final Class[] functions = {Logarithm.class, Sin.class, Cos.class, Derivative.class};
    
    private static final Class[] trigs = {Sin.class, Cos.class};
    
    public static boolean debug = true;
    
    public boolean printSimplified = false;
    
//     public static Var getVar(Expr expr, char character) {
//         if (expr instanceof Var) {
//             if (((Var) expr).character().equals(character)) return (Var) expr;
//             return null;
//         }
//         if (!(expr instanceof Operation)) return null;
//         for (Expr expr2 : ((Operation) expr).getExprs()) {
//             Var var = getVar(expr2, character);
//             if (var != null) return var;
//         }
//         return null;
//     }
    
    public static void noDebug() {
        debug = false;
    }
    

    public abstract Expr deriv(Var inTermsOf);
    
    public abstract boolean equalsExpr(Expr expr);
    
    public abstract Expr copyPass(HashMap<Expr, Expr> substitutions);
    
    public abstract int sign();
    
    public abstract String pretty();
    
    public Expr firstAtom() {
        if (!(this instanceof Operation)) return this;
        return ((Operation) this).getExpr(0).firstAtom();
    }
    
    public Expr lastAtom() {
        if (!(this instanceof Operation)) return this;
        ArrayList<Expr> exprs = ((Operation) this).getExprs();
        return exprs.get(exprs.size() - 1).lastAtom();
    }
    
    public boolean isConstant() {
        if (this instanceof Constant) return true;
        if (this instanceof Var) return false;
        if (this instanceof Undef) return false;
        for (Expr expr : ((Operation) this).getExprs()) {
            if (!expr.isConstant()) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isNumber() {
        if (this instanceof langdon.math.Number) return true;
        if (this.isConstant()) return false;
        if (this instanceof Var) return false;
        for (Expr expr : ((Operation) this).getExprs()) {
            if (!expr.isNumber()) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isFunction() {
        return this.printSimplify().isFunctionPass();
    }
    
    public boolean isFunctionPass() {
        return this instanceof Function;
    }
    
    public boolean isNumberPrinted() {
        return false;
    }
    
    public static Integer classPersonalLevelLeft(Class aClass) {
        Integer[] printLevelSides = printLevelSidesHM.get(aClass);
        Integer levelLeft = printLevelSides != null ? printLevelSides[0] : null;
        if (levelLeft != null) return levelLeft;
        Integer thisClassOrder = classOrder.get(aClass);
        return (thisClassOrder != null ? thisClassOrder : classOrderNum) - 1;
    }
    
    public static Integer classPersonalLevelRight(Class aClass) {
        Integer[] printLevelSides = printLevelSidesHM.get(aClass);
        Integer levelRight = printLevelSides != null ? printLevelSides[1] : null;
        if (levelRight != null) return levelRight;
        Integer thisClassOrder = classOrder.get(aClass);
        return (thisClassOrder != null ? thisClassOrder : classOrderNum) - 1;
    }
    
    public Integer personalLevelLeft() {
        return classPersonalLevelLeft(this.getClass());
    }
    
    public Integer personalLevelRight() {
        return classPersonalLevelRight(this.getClass());
    }
    
    public Integer printLevelLeft() {
        return this.printSimplify().printLevelLeftPass();
    }
    
    public Integer printLevelLeftPass() {
        Integer thisLevel = personalLevelLeft();
        
        if (this instanceof Operation) {
            Expr firstExpr = ((Operation) this).getExpr(0);
            if (!this.parensRight(firstExpr)) {
                Integer firstExprLevel = firstExpr.printLevelLeft();
                if (firstExprLevel < thisLevel) return firstExprLevel;
            }
        }
        
        return thisLevel;
    }
    
    public Integer printLevelRight() {
        return this.printSimplify().printLevelRightPass();
    }
    
    public Integer printLevelRightPass() {
        Integer thisLevel = personalLevelRight();
        
        if (this instanceof Operation) {
            Expr lastExpr = ((Operation) this).lastExpr();
            if (!this.parensLeft(lastExpr)) {
                Integer lastExprLevel = lastExpr.printLevelRight();
                if (lastExprLevel < thisLevel) return lastExprLevel;
            }
        }
        
        return thisLevel;
    }
    
    public boolean firstParenPrint() {
        return false;
    }
    
    public boolean parensRight(Expr expr) {
//         if (debug) System.err.println("parensRight: thisClassOrder: " + this.classOrder());
//         if (debug) System.err.println("parensRight: expr: " + expr);
//         if (debug) System.err.println("parensRight: expr.printLevelRight(): " + expr.printLevelRight());
        return this.classOrder() > expr.printLevelRight();
    }
    
    public boolean parensLeft(Expr expr) {
//         if (debug) System.err.println("parensLeft: thisClassOrder: " + this.classOrder());
//         if (debug) System.err.println("parensLeft: expr: " + expr);
//         if (debug) System.err.println("parensRight: expr.printLevelLeft(): " + expr.printLevelLeft());
        return this.classOrder() > expr.printLevelLeft();
    }
    
    public boolean functionalParens() {
        return true; // always parens for functions like that
        // Integer thisClassOrder = Expr.classOrder.get(Sin.class);
        // Integer exprPrintLevel = this.printLevelLeft();
        // 
        // return thisClassOrder > exprPrintLevel || firstParenPrint();
    }
    
//     public Integer[] printLevelSides() {
//         Integer[] sides = new Integer[2];
//         
//         sides[0] = printLevelLeft();
//         sides[1] = printLevelRight();
//         
//         return sides;
//     }
    
    public boolean isTrig() {
        return Arrays.asList(trigs).contains(this.getClass())
                || (this instanceof Exponent && Arrays.asList(trigs).contains(((Exponent) this).getExpr(0).getClass()));
    }
    
    public ArrayList<Expr>[] toTopsBottoms() {
        ArrayList<Expr>[] topsBottoms = new ArrayList[2];
        ArrayList<Expr> tops = new ArrayList<Expr>();
        ArrayList<Expr> bottoms = new ArrayList<Expr>();
        topsBottoms[0] = tops;
        topsBottoms[1] = bottoms;
        
        Expr top;
        if (this instanceof Division) {
            top = ((Operation) this).getExpr(0);
            Expr bottom = ((Operation) this).getExpr(1);
            if (bottom instanceof Product) {
                bottoms.addAll(((Operation) bottom).getExprs());
            } else {
                bottoms.add(bottom);
            }
        } else {
            top = this;
        }
        if (top instanceof Product) {
            tops.addAll(((Operation) top).getExprs());
        } else {
            tops.add(top);
        }
        
        return topsBottoms;
    }
    
    public Expr[] toBasePower() {
        Expr[] basePower = new Expr[2];
        Expr base;
        Expr power;
        
        if (this instanceof Exponent) {
            base = ((Operation) this).getExpr(0);
            power = ((Operation) this).getExpr(1);
        } else {
            base = this;
            power = langdon.math.Number.make(1);
        }
        
        basePower[0] = base;
        basePower[1] = power;
        return basePower;
    }
    
    public boolean isNegated() {
        if (this instanceof langdon.math.Number) return ((langdon.math.Number) this).val() < 0;
        if (this instanceof Product) return ((Operation) this).getExpr(0).isNegated();
        if (this instanceof Division) return this.toTopsBottoms()[0].get(0).isNegated();
        
        return false;
    }
    
    public Integer classOrder() {
        return this.printSimplify().classOrderPass();
    }
    
    public Integer classOrderPass() {
        return classOrder.get(this.getClass());
    }
    
    public Expr copy() {
        return copy(new HashMap<Expr, Expr>());
    }
    
    public Expr copy(HashMap<Expr, Expr> subs) {
        for (Expr expr : subs.keySet()) {
            if (equalsExpr(expr)) return subs.get(expr).copy();
        }
        return copyPass(subs);
    }
    
    public Expr printSimplify() {
        printSimplified = true;
        return this;
    }
    
    public String dump() {
        if (this instanceof Operation) {
            String dumpStr = "(" + this.getClass().getSimpleName();
            for (Expr expr : ((Operation) this).getExprs()) dumpStr+= " " + expr;
            return  dumpStr + ")";
        }
        return pretty();
    }
    
    public String toString() {
        return dump();
    }
    
    public String toString(String format) {
        if (format.equals("dump")) return dump();
        if (format.equals("pretty")) return pretty();
        return toString();
    }
    
}