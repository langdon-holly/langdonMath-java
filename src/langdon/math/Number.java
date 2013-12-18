package langdon.math;

import langdon.math.*;

import java.util.ArrayList;
import java.util.regex.*;
import java.util.HashMap;
import java.math.BigDecimal;

public class Number extends Constant implements Comparable<Number> {

    private Double val;
    
    private Number() {
        this(0d);
    }
    
    private Number(double val) {
        this.val = val;
    }
    
    public static Expr make() {
        return make(0);
    }
    
    public static Expr make(double val) {
        if (allowedVal(val)) return new Number(val);
        
        BigDecimal valBD = new BigDecimal(new Double(val).toString());
        // if (debug) System.err.println("Number.make: big: " + valBD);
        
        double numerator= valBD.scaleByPowerOfTen(valBD.scale()).doubleValue();
        double denom = Math.pow(10, valBD.scale());
        // if (debug) System.err.println("Number.make: numerator: " + numerator);
            
        return Division.make(new Number(numerator), new Number(denom));
    }
    
    public static boolean allowedVal(double val) {
        //return true;
        return val == new Double(val).longValue();
    }
    
    public Double val() {
        return val;
    }
    
    public boolean isInt() {
        return val == val.longValue();
    }
    
    public int compareTo(Number number) {
        return new Double(this.val() - number.val()).intValue();
    }
    
    public Number gcd(Number number) {
        if (!this.isInt() || !number.isInt()) return new Number();
        
        long val1 = Math.abs(this.val().longValue());
        long val2 = Math.abs(number.val().longValue());
        
        long lowestVal = val1 <= val2 ? val1 : val2;
        
        if (val1 % lowestVal == 0 && val2 % lowestVal == 0) return new Number(lowestVal);
        
        for (long factor = lowestVal / 2; factor >= 2; factor--) {
            if (val1 % factor == 0 && val2 % factor == 0) return new Number(factor);
        }
        
        return new Number(1);
    }
    
    public Expr deriv(Var inTermsOf) {
        return make();
    }
    
    public String pretty() {
        Matcher matcher = Pattern.compile("\\.0(?=[eE]|$)").matcher(val.toString());
        return matcher.replaceFirst("");
    }
    
    public boolean equals(Object o) {
        // if (debug) System.err.println("Number.equals: " + dump() + ", " + (o instanceof Expr ? ((Expr) o) : o));
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof Number)) return false;
        
        // if (debug) System.err.println("Number.equals: " + val + " " + (val.equals(((Number) o).val())?"==":"!=") + " " + ((Number) o).val());
        
        return val.equals(((Number) o).val());
    }
    
    public boolean equalsExpr(Expr expr) {
        return equals(expr);
    }
    
    public Expr copyPass(HashMap<Expr, Expr> subs) {
        return make(val);
    }
    
    public boolean isNumberPrinted() {
        return true;
    }
    
    public int sign() {
        if (val > 0) return 1;
        if (val < 0) return -1;
        return 0;
    }

}
