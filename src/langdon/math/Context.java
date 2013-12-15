package langdon.math;

import java.util.HashMap;
import langdon.util.ParseContext;

public class Context implements ParseContext {
    
    public HashMap<Character, Var> vars = new HashMap<Character, Var>();
    
    public HashMap<Expr, Expr> subs = new HashMap<Expr, Expr>();
    
    public Context() {
    }
    
    public Var getVar(char varChar) {
        if (vars.containsKey(varChar)) {
            return vars.get(varChar);
        } else {
            Var var = new Var(varChar);
            vars.put(varChar, var);
            return var;
        }
    }
    
}