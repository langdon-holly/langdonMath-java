package langdon.math;

import langdon.math.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Var extends Expr {

    public static void main(String[] args) {
        
        Var var = new Var('y');
        System.out.println("y?: "+var.character());
        
        Var var2 = new Var();
        System.out.println("x?: "+var2.character());
        
        var2.character('z');
        System.out.println("z?: "+var2.character());
        
    }

    private Character character;
    
    public HashMap<Var, Expr> derivInTermsOf = new HashMap<Var, Expr>();
    
    public Var() {
        this('x');
    }
    
    public Var(char character) {
        this.character = character;
    }
    
    public Character character() {
        return character;
    }
    
    public void character(char character) {
        this.character = character;
    }
    
    public Expr deriv(Var inTermsOf) {
        if (this.equals(inTermsOf)) return Number.make(1);
        if (derivInTermsOf.containsKey(inTermsOf)) return derivInTermsOf.get(inTermsOf);
        return Derivative.make(this, inTermsOf, false);
    }
    
    public String pretty() {
        return String.valueOf(character);
    }
    
    public boolean equalsExpr(Expr expr) {
        return equals(expr);
    }
    
    public Expr copyPass(HashMap<Expr, Expr> subs) {
        return this;
    }
    
    public int sign() {
        return 2;
    }

}