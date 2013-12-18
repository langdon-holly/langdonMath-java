package langdon.math;

public class Bool extends BoolExpr {
    
    public boolean is;
    
    public Bool(boolean is) {
        this.is = is;
    }
    
    public boolean isTrue() {
        return is;
    }
    
    public boolean isFalse() {
        return !is;
    }
    
    public static Bool yes() {
        return new Bool(true);
    }
    
    public static Bool no() {
        return new Bool(false);
    }
    
}
