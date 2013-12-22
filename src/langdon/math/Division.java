package langdon.math;

import langdon.util.ArrayLists;
import langdon.math.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Division extends Operation {
    
    private Expr numerator;
    private Expr denom;
    
    private Division(Expr numerator, Expr denom) {
        this.numerator = numerator;
        this.denom = denom;
    }
    
    public static Expr make(Expr numerator, Expr denom) {
        return make(numerator, denom, true);
    }
    
    public static Expr make(Expr numerator, Expr denom, boolean simplify) {
        Division division = new Division(numerator, denom);
        if (simplify) return division.simplify();
        return division;
    }
    
    public static Expr make(ArrayList<? extends Expr> exprs) {
        return make(exprs.get(0), exprs.get(1));
    }
    
    public Expr deriv(Var respected) {
        return make(Sum.make(Product.make(denom, numerator.deriv(respected)), Product.make(Number.make(-1d), Product.make(numerator, denom.deriv(respected)))), Exponent.make(denom, Number.make(2d)));
    }
    
    public ArrayList<Expr> getExprs() {
        ArrayList<Expr> arrayList = new ArrayList<Expr>();
        arrayList.add(numerator);
        arrayList.add(denom);
        return arrayList;
    }
    
    private Expr simplify() {
        if (hasUndef()) return new Undef();
        if (denom instanceof Number && ((Number) denom).val() == 1) return numerator; // divide by 1
        if (denom instanceof Number && ((Number) denom).val() == 0) return new Undef();
        if (numerator instanceof Number && ((Number) numerator).val() == 0 && denom.isConstant()) return Number.make(); // zero over a non-zero constant
        if (numerator instanceof Number && denom instanceof Number
         && ((Number) numerator).val() / (((Number) numerator).val() / ((Number) denom).val()) == ((Number) denom).val()
         && (Number.allowedVal(((Number) numerator).val() / ((Number) denom).val()) || !((Number) numerator).isInt() || !((Number) denom).isInt())) { // dividing integers doesn't result in a non-integer
            return Number.make(((Number) numerator).val() / ((Number) denom).val()); // divide the Numbers
        }
        if (numerator.equalsExpr(denom)) return Number.make(1); // x/x
        
        if (denom instanceof Division) return make(Product.make(numerator, ((Operation) denom).getExpr(1)), ((Operation) denom).getExpr(0)); // flip up denominators in the denominator
        if (numerator instanceof Division) return make(((Operation) numerator).getExpr(0), Product.make(((Operation) numerator).getExpr(1), denom)); // bring down denominators in the numerator
        // subtract exponents when bases are equal
        if (numerator instanceof Exponent && denom instanceof Exponent && ((Operation) numerator).getExpr(0).equalsExpr(((Operation) denom).getExpr(0))) return Exponent.make(((Operation) numerator).getExpr(0), Sum.make(((Operation) numerator).getExpr(1), Product.negative(((Operation) denom).getExpr(1))));
        if (numerator instanceof Exponent && ((Operation) numerator).getExpr(0).equalsExpr(denom)) return Exponent.make(denom, Sum.make(((Operation) numerator).getExpr(1), Number.make(-1))); // subtract 1 from exponent when denominator exponent is 1
        if (denom instanceof Exponent && ((Operation) denom).getExpr(0).equalsExpr(numerator)) return Exponent.make(numerator, Sum.make(Number.make(1), Product.negative(((Operation) denom).getExpr(1)))); // exponent = 1 - (denominator exponent)
        //if (numerator instanceof Number && denom instanceof Exponent) return Product.make(numerator, Exponent.make(denom, Number.make(-1))); // flip up with negative exponent when numerator is Number
        
        if (numerator instanceof Number && denom instanceof Number) {
            Number gcd = ((Number) numerator).gcd((Number) denom);
            // if (debug && !gcd.equalsExpr(Number.make(1))) System.err.println("gcd of (" + numerator + ", " + denom + ") = " + gcd);
            numerator = Division.make(numerator, gcd);
            denom = Division.make(denom, gcd);
        }
        
        //if (debug) System.err.println("Division simplify: " + dump());
    
        ArrayList<Expr> numeratorExprs = new ArrayList<Expr>();
        if (numerator instanceof Product) numeratorExprs = ((Operation) numerator).getExprs();
        else numeratorExprs.add(numerator);
        ArrayList<Expr> denomExprs = new ArrayList<Expr>();
        if (denom instanceof Product) denomExprs = ((Operation) denom).getExprs();
        else denomExprs.add(denom);
        
        for (int i = 0; i < numeratorExprs.size(); i++) {
            if (numeratorExprs.get(i) instanceof Exponent) {
                Exponent exponent = (Exponent) numeratorExprs.get(i);
                // if (debug) System.err.println("yep: " + exponent);
                Expr exponentExp = exponent.getExpr(1);
                if ((exponentExp instanceof Number && ((Number) exponentExp).val() < 0) || (exponentExp instanceof Product && ((Operation) exponentExp).getExpr(0) instanceof Number && ((Number) ((Operation) exponentExp).getExpr(0)).val() < 0)) {
                    // if (debug) System.err.println("yep");
                    denomExprs.add(Exponent.make(numeratorExprs.remove(i), Number.make(-1)));
                    i--;
                }
            }
        }
        for (int i = 0; i < denomExprs.size(); i++) {
            if (denomExprs.get(i) instanceof Exponent) {
                Exponent exponent = (Exponent) denomExprs.get(i);
                // if (debug) System.err.println("yep: " + exponent);
                Expr exponentExp = exponent.getExpr(1);
                if ((exponentExp instanceof Number && ((Number) exponentExp).val() < 0) || (exponentExp instanceof Product && ((Operation) exponentExp).getExpr(0) instanceof Number && ((Number) ((Operation) exponentExp).getExpr(0)).val() < 0)) {
                    // if (debug) System.err.println("yep");
                    numeratorExprs.add(Exponent.make(denomExprs.remove(i), Number.make(-1)));
                    i--;
                }
            }
        }
        
        if (numerator instanceof Product || denom instanceof Product) {
        
            for (int i = 0; i < numeratorExprs.size(); i++) {
                Expr numeratorExpr = numeratorExprs.get(i);
                for (int j = 0; j < denomExprs.size(); j++) {
                    Expr denomExpr = denomExprs.get(j);
                    // if (debug) System.err.println("Division simplify: dividing (" + numeratorExpr + ")/(" + denomExpr + ")");
                    Expr divided = Division.make(numeratorExpr, denomExpr);
                    // if (debug) System.err.println("Division simplify: divided: " + divided);
                    if (!(divided instanceof Division)) {
                        numeratorExprs.set(i, divided);
                        denomExprs.remove(j);
                        Division newProduct = new Division(ArrayLists.productArrToExpr(numeratorExprs), ArrayLists.productArrToExpr(denomExprs));
                        if (debug) System.err.println("Division.simplify: " + dump() + " divided to " + newProduct);
                        return newProduct.simplify();
                    }
                    else if (!((Operation) divided).getExprs().get(0).equals(numeratorExpr)) {
                        numeratorExprs.set(i, ((Operation) divided).getExprs().get(0));
                        numerator = (numeratorExprs.size() == 1) ? numeratorExprs.get(0) : Product.make(numeratorExprs);
                        denomExprs.set(j, ((Operation) divided).getExprs().get(1));
                        denom = (denomExprs.size() == 1) ? denomExprs.get(0) : Product.make(denomExprs);
                        if (debug) System.err.println("Division.simplify: divided to: " + dump());
                        return simplify();
                    }
                }
            }
        }
        
        numerator = (numeratorExprs.size() == 1) ? numeratorExprs.get(0) : Product.make(numeratorExprs);
        denom = (denomExprs.size() == 1) ? denomExprs.get(0) : Product.make(denomExprs);
        
        if (denom.isNegated()) {
            numerator = Product.negative(numerator);
            denom = Product.negative(denom);
        }
        
        return this;
    }
    
    public Integer classOrderPass() {
        if (isFunctionPass()) return classOrder.get(Sin.class);
        // if (numerator instanceof Number && denom instanceof Number && decimalPrinted(((Number) numerator).val(), ((Number) denom).val()) != null) return null;
        // System.err.println(numerator.toString() + " / " + denom.toString());
        
        return super.classOrderPass();
    }
    
    public boolean isFunctionPass() {
        if (isTan() || isCot() || isSec() || isCsc()) return true;
        
        return super.isFunctionPass();
    }
    
    public Integer printLevelLeftPass() {
        if (isFunctionPass()) return classOrderNum - 1;
        if (isNumberPrinted()) return classOrderNum - 1;
        
        return super.printLevelLeftPass();
    }
    
    public Integer printLevelRightPass() {
        if (isFunctionPass()) return Expr.classPersonalLevelRight(Sin.class);
        if (isNumberPrinted()) return classOrderNum - 1;
        
        return super.printLevelRightPass();
    }
    
    public boolean firstParenPrint() {
        if (isFunctionPass()) return false;
        if (isNumberPrinted()) return false;
        
        return super.firstParenPrint();
    }
    
    public Double decimalPrinted(double numerator, double denom) {
            double denomVal = denom;
            while (denomVal % 5 == 0) { denomVal/= 5; }
            while (denomVal % 2 == 0) { denomVal/= 2; }
            if (denomVal == 1) {
                return numerator / denom;
            }
            return null;
    }
    
    public boolean isNumberPrinted() {
        return numerator instanceof Number && denom instanceof Number && decimalPrinted(((Number) numerator).val(), ((Number) denom).val()) != null;
    }
    
    public boolean isTan() {
        return numerator instanceof Sin && denom instanceof Cos && ((Operation) numerator).getExpr(0).equalsExpr(((Operation) denom).getExpr(0));
    }
    
    public boolean isCot() {
        return numerator instanceof Cos && denom instanceof Sin && ((Operation) numerator).getExpr(0).equalsExpr(((Operation) denom).getExpr(0));
    }
    
    public boolean isSec() {
        return numerator.equalsExpr(Number.make(1)) && denom instanceof Cos;
    }
    
    public boolean isCsc() {
        return numerator.equalsExpr(Number.make(1)) && denom instanceof Sin;
    }
    
    public Expr printSimplify() {
        // System.err.println("Division.printSimplify(): this: " + this);
        ArrayList<Expr>[] topsbottoms = toTopsBottoms();
        ArrayList<Expr> numeratorExprs = toTopsBottoms()[0];
        ArrayList<Expr> denomExprs = toTopsBottoms()[1];
        
        for (int i = 0; i < denomExprs.size(); i++) {
            Expr botExpr = denomExprs.get(i);
            Expr[] botBasePower = botExpr.toBasePower();
            Expr botBase = botBasePower[0];
            Expr botPower = botBasePower[1];
            // System.err.println("Division.printSimplify(): botExpr: " + botExpr);
            
            boolean combined = false;
            
            for (int j = 0; j < numeratorExprs.size(); j++) {
                Expr topExpr = numeratorExprs.get(j);
                Expr[] topBasePower = topExpr.toBasePower();
                Expr topBase = topBasePower[0];
                Expr topPower = topBasePower[1];
                
                // System.err.println("Division.printSimplify(): topBase: " + topBase);
                if (topBase.isTrig() && botBase.isTrig() && topPower.equalsExpr(botPower) && ((Operation) topBase).getExpr(0).equalsExpr(((Operation) botBase).getExpr(0))) {
                    Expr trigDivision = Division.make(topBase, botBase);
                    trigDivision.printSimplified = true;
                    numeratorExprs.set(j, topPower.equalsExpr(Number.make(1)) ? trigDivision : Exponent.make(trigDivision, topPower, false));
                    combined = true;
                } else if (topExpr instanceof Number && botExpr instanceof Number && decimalPrinted(((Number) topExpr).val(), ((Number) botExpr).val()) != null) {
                    Expr numDivision = Division.make(topExpr, botExpr);
                    numDivision.printSimplified = true;
                    numeratorExprs.set(j, numDivision);
                    combined = true;
                }
                
                if (combined) {
                    denomExprs.remove(i);
                    i--;
                    break;
                }
            }
            
            if (!combined && botBase.isTrig()) {
                // System.err.println("Division.printSimplify(): !combined && " + botBase + " is trig");
                // System.err.println("  before: (Division " + ArrayLists.productArrToExpr(numeratorExprs) + " " + ArrayLists.productArrToExpr(denomExprs) + ")");
                Expr trigFlip = Division.make(Number.make(1), botBase);
                trigFlip.printSimplified = true;
                numeratorExprs.add(botPower.equalsExpr(Number.make(1)) ? trigFlip : Exponent.make(trigFlip, botPower, false));
                if (numeratorExprs.get(0).equalsExpr(Number.make(1))) numeratorExprs.remove(0);
                denomExprs.remove(i);
                i--;
                // System.err.println("  after:  (Division " + ArrayLists.productArrToExpr(numeratorExprs) + " " + ArrayLists.productArrToExpr(denomExprs) + ")");
            }
            
        }
        
        Expr newNumerator = ArrayLists.productArrToExpr(numeratorExprs, false);
        Expr newDenom = ArrayLists.productArrToExpr(denomExprs, false);
        
        Expr printSimplified = newDenom.equalsExpr(Number.make(1)) ? newNumerator
                                                                   : Division.make(newNumerator, newDenom, false);
        if (printSimplified instanceof Division) ((Division) printSimplified).printSimplified = true;
        
        return printSimplified;
    }
    
    public String pretty() {
        return pretty(true);
    }
    
    public String pretty(boolean printSimplify) {
        if (printSimplify && !printSimplified) return printSimplify().pretty();
        
        if (numerator instanceof Number && denom instanceof Number) {
            Double decimal = decimalPrinted(((Number) numerator).val(), ((Number) denom).val());
            if (decimal != null) {
                return decimal.toString();
            }
        }
        if (isTan()) return "tan" + (((Operation) numerator).getExpr(0).functionalParens()?"(":" ") + ((Operation) numerator).getExpr(0).pretty() + (((Operation) numerator).getExpr(0).functionalParens()?")":"");
        if (isCot()) return "cot" + (((Operation) numerator).getExpr(0).functionalParens()?"(":" ") + ((Operation) numerator).getExpr(0).pretty() + (((Operation) numerator).getExpr(0).functionalParens()?")":"");
        if (isSec()) return "sec" + (((Operation) denom).getExpr(0).functionalParens()?"(":" ") + ((Operation) denom).getExpr(0).pretty() + (((Operation) denom).getExpr(0).functionalParens()?")":"");
        if (isCsc()) return "csc" + (((Operation) denom).getExpr(0).functionalParens()?"(":" ") + ((Operation) denom).getExpr(0).pretty() + (((Operation) denom).getExpr(0).functionalParens()?")":"");
        
        String string = new String();
        
        Integer thisClassOrder = this.classOrder();
        
        boolean numeratorParens = false;
        if (thisClassOrder > numerator.printLevelRight()) numeratorParens = true;
        // if (debug) System.err.println("Division toString(): for expr=" + numerator + ", printLevelRight=" + numerator.printLevelRight());
        boolean denomParens = false;
        if (thisClassOrder > denom.printLevelLeft()) denomParens = true;
        // if (debug) System.err.println("Division toString(): for expr=" + denom + ", printLevelLeft=" + denom.printLevelLeft());
        
        string = string.concat((numeratorParens?"(":"") + numerator.pretty() + (numeratorParens?")":""));
        string = string.concat("/");
        string = string.concat((denomParens?"(":"") + denom.pretty() + (denomParens?")":""));
        
        return string;
    }
    
    public boolean equalsExpr(Expr expr) {
        if (expr == null) return false;
        if (expr == this) return true;
        if (!(expr instanceof Division)) return false;
        
        if (numerator.equalsExpr(((Operation) expr).getExprs().get(0)) && denom.equalsExpr(((Operation) expr).getExprs().get(1))) return true;
        
        return false;
    }
    
    public Expr copyPass(HashMap<Expr, Expr> subs) {
        return make(numerator.copy(subs), denom.copy(subs));
    }
    
    public int sign() {
        if (numerator.sign() == 2 || denom.sign() == 2) return 2;
        return numerator.sign() * denom.sign();
    }
    
}
