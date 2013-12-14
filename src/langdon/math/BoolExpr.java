package langdon.math;

public abstract class BoolExpr extends Expr {
    
    public abstract boolean isTrue(); // known to be true
    public abstract boolean isFalse(); // known to be false
    
}