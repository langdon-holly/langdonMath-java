package langdon.math;

import java.util.ArrayList;
import java.util.HashMap;

public class Derivative extends Function {
    
    private Expr ofExpr;
    private Var respected;
    
    private Derivative(Expr ofExpr, Var respected/*, int order*/) {
        this.ofExpr = ofExpr;
        this.respected = respected;
    }
    
    public static Expr make(Expr ofExpr, Var respected) {
        return make(ofExpr, respected, true);
    }
    
    public static Expr make(Expr ofExpr, Var respected, boolean simplify) {
        return make(ofExpr, respected, simplify, 1);
    }
    
    public static Expr make(Expr ofExpr, Var respected, int order) {
        return make(ofExpr, respected, true, order);
    }
    
    public static Expr make(Expr ofExpr, Var respected, boolean simplify, int order) {
        for (int i = 0; i < order; i++) {
            ofExpr = new Derivative(ofExpr, respected);
            if (simplify) ofExpr = ((Derivative) ofExpr).simplify();
        }
        return ofExpr;
    }
    
    public static Expr make(ArrayList<? extends Expr> exprs) {
        return make(exprs.get(0), (Var) exprs.get(1));
    }
    
    private Expr simplify() {
        if (hasUndef()) return new Undef();
        if (!(ofExpr instanceof Derivative)) {
            return ofExpr.deriv(respected);
        }
        return this;
    }
    
    public Var respected() {
        return respected;
    }
    
    public ArrayList<Expr> getExprs() {
        ArrayList<Expr> arrayList = new ArrayList<Expr>();
        arrayList.add(ofExpr);
        arrayList.add(respected);
        return arrayList;
    }
    
    public boolean equalsExpr(Expr expr) {
        if (expr == null) return false;
        if (expr == this) return true;
        if (!(expr instanceof Derivative)) return false;
        
        if (ofExpr.equalsExpr(((Operation) expr).getExprs().get(0)) && respected.equals(((Derivative) expr).respected())) return true;
        
        return false;
    }
    
    public Expr deriv(Var respected) {
         return make(this, respected);
    }
    
    public String pretty() {
        int order = 1;
        Expr botOfExpr = ofExpr;
        while (botOfExpr instanceof Derivative && respected.equals(((Derivative) botOfExpr).respected())) {
            order++;
            botOfExpr = ((Operation) botOfExpr).getExprs().get(0);
        }
        boolean parens = botOfExpr.functionalParens();
        return "d" + (order > 1 ? "^" + order : "") + ((botOfExpr instanceof Var)?botOfExpr:"") + "/d" + respected + (order > 1 ? "^" + order : "") + ((botOfExpr instanceof Var)?"":(parens?"(":"") + botOfExpr + (parens?")":""));
    }
    
    public Expr copyPass(HashMap<Expr, Expr> subs) {
        return make(ofExpr.copy(subs), (Var) respected.copy(subs));
    }
    
    public Integer personalLevelLeft() {
        int order = 1;
        Expr botOfExpr = ofExpr;
        while (botOfExpr instanceof Derivative && respected.equals(((Derivative) botOfExpr).respected())) {
            order++;
            botOfExpr = ((Operation) botOfExpr).getExprs().get(0);
        }
        
        if (botOfExpr instanceof Var) return classOrderNum - 1;
        
        return super.personalLevelLeft();
    }
    
    public Integer personalLevelRight() {
        int order = 1;
        Expr botOfExpr = ofExpr;
        while (botOfExpr instanceof Derivative && respected.equals(((Derivative) botOfExpr).respected())) {
            order++;
            botOfExpr = ((Operation) botOfExpr).getExprs().get(0);
        }
        
        if (botOfExpr instanceof Var) return classOrderNum - 1;
        
        return super.personalLevelRight();
    }
    
    public int sign() {
        return 2;
    }
    
    //=======================================================
    //==  STUFF FOR AN OLD DERIVATIVE USER INTERFACE       ==
    //=======================================================
    
//     private static boolean debug = true;
//     
//     private static boolean debugAll = false;
//     private static String printStyle = "interactive";
//     private static Integer times = 1;
//     
//     public static void main(String[] args) throws IOException {
// //         CommandLineParser parser = new BasicParser();
// //         Options options = new Options();
// //         CommandLine commandLine;
// //         try {
// //             commandLine = parser.parse(options, args);
// //         } catch (org.apache.commons.cli.ParseException e) {
// //             throw new IllegalArgumentException();
// //         }
// //         String[] args2 = commandLine.getArgs();
//         
//         int argOn = 0;
//         for (; argOn < Array.getLength(args); argOn++) {
//             String arg = args[argOn];
//             if (arg.length() == 0) break;
//             if (arg.charAt(0) != '=') break;
//             String option = arg.substring(1, arg.length());
//             if (option.equals("d")) {
//                 debugAll = true;
//                 printStyle = "interactive";
//             }
//             else if (option.equals("D")) {
//                 debugAll = false;
//             }
//             else if (option.equals("p")) {
//                 printStyle = "pipe";
//             }
//             else if (option.equals("P")) {
//                 printStyle = "interactive";
//             }
//             else if (option.equals("t")) {
//                 times = 1;
//             }
//             else if (Pattern.matches("^t=.*", option)) {
//                 if (debug) System.err.println("=t=something");
//                 times = Integer.parseInt(option.substring(2, option.length()));
//             }
//         }
//         
//         if (!debugAll) {
//             noDebug();
//             ExprParser.noDebug();
//             Expr.noDebug();
//         }
//         
//         if (argOn < Array.getLength(args)) {
//             for (; argOn < Array.getLength(args); argOn++) {
//                 derivative(args[argOn]);
//             }
//         }
//         else {
//             // System.err.println("please argue about math");
//             BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//             String input;
//             
//             if (printStyle.equals("interactive")) System.out.print("> ");
//             
//             while ((input = in.readLine()) != null) {
//                 derivative(input);
//                 if (printStyle.equals("interactive")) System.out.print("> ");
//             }
//         }
//     }
//     
//     private static void derivative(String input) {
//         Context context = new Context();
//         Expr expression;
//         try {
//             String[] splitInputA = input.split(";");
//             ArrayList<String> splitInput = new ArrayList<String>();
//             for (String string : splitInputA) {
//                 if (!string.equals("")) splitInput.add(string);
//             }
//             
//             String derivOf = splitInput.remove(splitInput.size() - 1);
//             
//             for (String string : splitInput) {
//                 if (string.matches("^d[a-zA-Z]/d[a-zA-Z]=.*")) {
//                     if (debug) System.err.println("yep");
//                     char dyC = string.charAt(1);
//                     char dxC = string.charAt(4);
//                     Var dy = context.getVar(dyC);
//                     Var dx = context.getVar(dxC);
//                     dy.derivInTermsOf.put(dx, ExprParser.parse(string.substring(6, string.length()), context));
//                     if (debug) System.err.println("d" + dyC + "/d" + dxC + "=" + Derivative.make(dy, dx));
//                 }
//                 else if (string.indexOf('=') != -1) {
//                     String exprS = string.substring(0, string.indexOf('='));
//                     String subS = string.substring(string.indexOf('=') + 1, string.length());
//                     try {
//                         Expr expr = ExprParser.parse(exprS, context);
//                         try {
//                             Expr sub = ExprParser.parse(subS, context);
//                             context.subs.put(expr, sub);
//                         } catch (Exception e) {
//                             System.err.println(subS + " could not be parsed:");
//                             if (e instanceof java.text.ParseException) {
//                                 String parseMsg = ExprParser.generateParseMesg(subS, (java.text.ParseException) e);
//                                 System.err.println("parse error: " + e.getMessage());
//                                 if (parseMsg != null) System.err.println(parseMsg);
//                             }
//                         }
//                     } catch (Exception e) {
//                         System.err.println(exprS + " could not be parsed:");
//                         if (e instanceof java.text.ParseException) {
//                             String parseMsg = ExprParser.generateParseMesg(exprS, (java.text.ParseException) e);
//                             System.err.println("parse error: " + e.getMessage());
//                             if (parseMsg != null) System.err.println(parseMsg);
//                         }
//                     }
//                 }
//             }
//             
//             expression = ExprParser.parse(derivOf, context);
//             if (debug) System.err.println("Derivative.main: y = " + expression);
//             expression = expression.copy(context.subs);
//             
//             Var dy = context.getVar('y');
//             Var dx = context.getVar('x');
//             
//             int preSpaceLength = 0;
//             if (times > 0) {
//                 preSpaceLength = 5;
//                 if (times > 1) {
//                     preSpaceLength+= 2 + times.toString().length() * 2;
//                 }
//             }
//             
//             if (printStyle.equals("interactive")) {
//                 String preSpace = "";
//                 for (int i = 0; i < preSpaceLength - 1; i++) preSpace = preSpace.concat(" ");
//                 System.out.println(preSpace + "y = " + expression);
//             }
//             
//             Expr derivative = expression;
//             String derivativeString = "y";
//             for (int time = 1; time <= times; time++) {
//                 // derivative = derivative.deriv(dx);
//                 derivative = Derivative.make(derivative, dx);
//                 derivativeString = "d" + (time > 1 ? "^" + time : "") + dy + "/d" + dx + (time > 1 ? "^" + time : "");
//                 if (printStyle.equals("interactive")) {
//                     String preSpace = "";
//                     for (int i = 0; i < preSpaceLength - derivativeString.length(); i++) preSpace = preSpace.concat(" ");
//                     System.out.println(preSpace + derivativeString + " = " + derivative);
//                 }
//             }
//             
//             if (!printStyle.equals("interactive")) {
//                 System.out.println(derivative);
//             }
//         } catch (Exception e) {
//             if (e instanceof java.text.ParseException) {
//                 String parseMsg = ExprParser.generateParseMesg(input, (java.text.ParseException) e);
//                 System.err.println("parse error: " + e.getMessage());
//                 if (parseMsg != null) System.err.println(parseMsg);
//             }
//             // else if (e instanceof UnsupportedOperationException) {
//             //     System.err.println(e);
//             //     if (debug) e.printStackTrace();
//             // }
//             else {
//                 System.err.println(e);
//                 e.printStackTrace();
//             }
//         }
//     }
//     
//     public static void noDebug() {
//         debug = false;
//     }
    
}