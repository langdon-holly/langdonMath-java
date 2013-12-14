langdonMath
===========

a symbolic math engine

I wrote a computer program in Java, originally to do differentiation, but currently it simplifies a lot of numerical expressions.  
It can differentiate and/or simplify basically anything with real numbers, e, pi, variables, sums, products, exponents, division, logarithms, sine functions, and cosine functions, except (it does not yet know how to simplify) non-(constant or e-based) logarithms.  
Including: automatic simplification of a lot of stuff (when precision would not be lost), grouping by parenthesis, implicit multiplication (without a times symbol, for example: "2x"), negative stuff, implicit differentiation, undefined values, functional importance, and pointing to your errors.


Source Files
------------

+ mathClasses.txt  // a list of the files to compile
+ ArrayLists.java  // general ArrayList methods I needed, like for splitting on operations
+ ArraySearch.java // general Array search method I needed
+ Simple.java      // a simple simplification user interface
+ Derivative.java  // a user interface for differentiation (also a derivative class)
+ Expr.java        // superclass for expressions (including sums, variables, e)
+ // BoolExpr.java    // superclass for boolean Exprs
+ Constant.java    // superclass for constants (e, pi, numbers)
+ Operation.java   // superclass for operations (like sums, products)
+ // Conditional.java // conditional stuff
+ ExprParser.java  // the longest (and most complex), parses an expression from a string
+ Subscript.java   // a mathematical subscript, like for a logarithm
+ PartialParseExpr.java // class for partially-parsed expressions
+ Token.java       // general container class for partially- or fully-parsed expressions
+ TokenList.java   // ArrayList of Tokens
+ Number.java      // a number, with its value stored in a Double
+ E.java           // an e constant
+ Pi.java          // a π (pi) constant
+ Var.java         // a variable (like x, y, z, a)
+ Sum.java         // a sum of expressions
+ Product.java     // a product of expressions
+ Exponent.java    // an exponent with a base and exponent
+ Division.java    // a quotient with a numerator and denominator
+ Logarithm.java   // a logarithm
+ Sin.java         // the trigonometric sine function
+ Cos.java         // the trigonometric cosine function
+ Undef.java       // undefined (undef)
+ // Bool.java        // true (yes) or false (no)


Command-line Arguments for langdon.math.Simple
----------------------------------------------

first, options:
`-d`       turns on Debugging  
`-D`       turns off Debugging (default)  
`-p`       switches to Plain printing style  
`-P`       switches to interactive printing style (default)  
`-s printStyle`  switches to printing Style printStyle (like "pretty" or "dump")  
`-e expr`  simplifies the mathematical Expression expr  
then (optional):  
`-- \[expr1, [expr2, ...]]` (some mathematical expressions to simplify)  

You can put substitutions at/before the beginning of the expression, separated by a ';', to be substituted in the expression after and before it's simplified, like "a=2;2a".


Examples
--------

```
$ java langdon.math.Simple -e 'd/dx(5x^2+cx^3)'  
d/dx(5x^2+cx^3) = 10x+3c\*x^2+dc/dx*x^3
```

```
$ java langdon.math.Simple -pe 'd/dx x/(x/(x^2))'  
2x
```

```
$ java langdon.math.Simple -e 'log[-2] 2'  
log[-2] 2 = undef
```

```
$ java langdon.math.Simple -s dump -- 2 e d/dx2cx  
2 = 2  
e = e  
d/dx2cx = (Sum (Product 2 c) (Product 2 (Derivative c x) x))
```

```
$ java langdon.math.Simple  
: sin 3pi/2  
= -1  
: sin 100pi/3  
= -0.5sqrt(3)  
:
```

```
$ java langdon.math.Simple -e 'a=b; d/db dy/da'  
a=b; d/db dy/da = d^2y/db^2  
```

```
$ java langdon.math.Simple -ds dump -e '7x^3'  
tokenizing "7x^3"  
tokens:    [([7]"7"), ([x]"x"), ([exponentCaret]"^"), ([3]"3")]  
mapped to: [([7]"7"), ([x]"x"), ([exponent]"^"), ([3]"3")]  
\--------------  
contexts: [[]]  
parens:   [string]  
token:    ([7]"7")  
\--------------  
contexts: [[([7]"7")]]  
parens:   [string]  
token:    ([x]"x")  
\--------------  
contexts: [[([7]"7"), ([x]"x")]]  
parens:   [string]  
token:    ([exponent]"^")  
\--------------  
contexts: [[([7]"7"), ([x]"x"), ([exponent]"^")]]  
parens:   [string]  
token:    ([3]"3")  
parsing     [([7]"7"), ([x]"x"), ([exponent]"^"), ([3]"3")]  
altered to: [([7]"7"), ([times]""), ([x]"x"), ([exponent]"^"), ([3]"3")]  
splitting on [([times]), ([division])]  
splitted: [[([7]"7")], [([x]"x"), ([exponent]"^"), ([3]"3")]]  
parsing     [([7]"7")]  
parsing     [([x]"x"), ([exponent]"^"), ([3]"3")]  
altered to: [([x]"x"), ([exponent]"^"), ([3]"3")]  
splitting on [([exponent])]  
splitted: [[([x]"x")], [([3]"3")]]  
parsing     [([x]"x")]  
parsing     [([3]"3")]  
Simple.simplify: before substitution: (Product 7 (Exponent x 3))  
Simple.simplify: after substitution:  (Product 7 (Exponent x 3))  
7x^3 = (Product 7 (Exponent x 3))
```

```
$ java langdon.math.Simple -de '3\*+/2'  
tokenizing "3\*+/2"  
tokens:    [([3]"3"), ([timesAst]"\*"), ([plus]"+"), ([divisionSlash]"/"), ([2]"2")]  
mapped to: [([3]"3"), ([times]"\*"), ([plus]"+"), ([division]"/"), ([2]"2")]  
\--------------  
contexts: [[]]  
parens:   [string]  
token:    ([3]"3")  
\--------------  
contexts: [[([3]"3")]]  
parens:   [string]  
token:    ([times]"\*")  
\--------------  
contexts: [[([3]"3"), ([times]"\*")]]  
parens:   [string]  
token:    ([plus]"+")  
\--------------  
contexts: [[([3]"3"), ([times]"\*"), ([plus]"+")]]  
parens:   [string]  
token:    ([division]"/")  
\--------------  
contexts: [[([3]"3"), ([times]"\*"), ([plus]"+"), ([division]"/")]]  
parens:   [string]  
token:    ([2]"2")  
parsing     [([3]"3"), ([times]"\*"), ([plus]"+"), ([division]"/"), ([2]"2")]  
altered to: [([3]"3"), ([times]"\*"), ([plus]"+"), ([division]"/"), ([2]"2")]  
splitting on [([plus])]  
splitted: [[([3]"3"), ([times]"\*")], [([division]"/"), ([2]"2")]]  
parsing     [([3]"3"), ([times]"\*")]  
altered to: [([3]"3"), ([times]"\*")]  
splitting on [([times]), ([division])]  
splitted: [[([3]"3")], []]  
parsing     [([3]"3")]  
parsing     []  
"3\*+/2" could not be parsed:  
parse error: token(s) expected, but not found (check your syntax)  
3*+/2  
  ^
```
