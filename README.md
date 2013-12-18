langdonMath
===========

a symbolic math engine

I wrote a computer program in Java, originally to do differentiation, but currently it simplifies a lot of numerical expressions.  
It can differentiate and/or simplify basically anything with real numbers, e, pi, variables, sums, products, exponents, division, logarithms, sine functions, and cosine functions, except (it does not yet know how to simplify) non-(constant or e-based) logarithms.  
Including: automatic simplification of a lot of stuff (when precision would not be lost), grouping by parenthesis, implicit multiplication (without a times symbol, for example: "2x"), negative stuff, implicit differentiation, undefined values, functional importance, and pointing to your errors.


Command-line Arguments for langdon.math.Simple
----------------------------------------------

first, options:  
`-d`       turns on Debugging  
`-D`       turns off Debugging (default)  
`-p`       switches to Plain printing style  
`-P`       switches to interactive printing style (default)  
`-i inputFormat`  switches to Input format inputFormat (like "pretty" or "s-expr")  
`-o outputFormat`  switches to Output format outputFormat (like "pretty" or "s-expr")  
`-e expr`  simplifies the mathematical Expression expr  
then (optional):  
`-- [expr1, [expr2, ...]]` (some mathematical expressions to simplify)  

You can put substitutions at/before the beginning of the expression, separated by a ';', to be substituted in the expression after and before it's simplified, like "a=2;2a".


Examples
--------

```
$ java langdon.math.Simple -e 'd/dx(5x^2+cx^3)'
d/dx(5x^2+cx^3) = 10x+3c*x^2+dc/dx*x^3
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
$ java langdon.math.Simple -o s-expr -- 2 e d/dx2cx
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
$ java langdon.math.Simple -i s-expr -e '(Division (Derivative (Logarithm e (Product 2 x)) x) 5)'
(Division (Derivative (Logarithm e (Product 2 x)) x) 5) = 0.2/x
```

```
$ java langdon.math.Simple -do s-expr -e '7x^3'
tokenizing "7x^3"
tokens:    [([number]"7"), ([var]"x"), ([exponentCaret]"^"), ([number]"3")]
parsed to: [([7]"7"), ([x]"x"), ([exponentCaret]"^"), ([3]"3")]
mapped to: [([7]"7"), ([x]"x"), ([exponent]"^"), ([3]"3")]
--------------
contexts: [[]]
levels:   [([stringBeg])]
token:    ([7]"7")
--------------
contexts: [[([7]"7")]]
levels:   [([stringBeg])]
token:    ([x]"x")
--------------
contexts: [[([7]"7"), ([x]"x")]]
levels:   [([stringBeg])]
token:    ([exponent]"^")
--------------
contexts: [[([7]"7"), ([x]"x"), ([exponent]"^")]]
levels:   [([stringBeg])]
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
$ java langdon.math.Simple -de '3*+/2'
tokenizing "3*+/2"
tokens:    [([number]"3"), ([timesAst]"*"), ([plus]"+"), ([divisionSlash]"/"), ([number]"2")]
parsed to: [([3]"3"), ([timesAst]"*"), ([plus]"+"), ([divisionSlash]"/"), ([2]"2")]
mapped to: [([3]"3"), ([times]"*"), ([plus]"+"), ([division]"/"), ([2]"2")]
--------------
contexts: [[]]
levels:   [([stringBeg])]
token:    ([3]"3")
--------------
contexts: [[([3]"3")]]
levels:   [([stringBeg])]
token:    ([times]"*")
--------------
contexts: [[([3]"3"), ([times]"*")]]
levels:   [([stringBeg])]
token:    ([plus]"+")
--------------
contexts: [[([3]"3"), ([times]"*"), ([plus]"+")]]
levels:   [([stringBeg])]
token:    ([division]"/")
--------------
contexts: [[([3]"3"), ([times]"*"), ([plus]"+"), ([division]"/")]]
levels:   [([stringBeg])]
token:    ([2]"2")
parsing     [([3]"3"), ([times]"*"), ([plus]"+"), ([division]"/"), ([2]"2")]
altered to: [([3]"3"), ([times]"*"), ([plus]"+"), ([division]"/"), ([2]"2")]
splitting on [([plus])]
splitted: [[([3]"3"), ([times]"*")], [([division]"/"), ([2]"2")]]
parsing     [([3]"3"), ([times]"*")]
altered to: [([3]"3"), ([times]"*")]
splitting on [([times]), ([division])]
splitted: [[([3]"3")], []]
parsing     [([3]"3")]
parsing     []
"3*+/2" could not be parsed:
parse error: token(s) expected, but not found (check your syntax)
3*+/2
  ^
```

```
$ java langdon.math.Simple -di s-expr -e '(Division (Derivative (Logarithm e (Product 2 x)) x) 5)'
tokenizing "(Division (Derivative (Logarithm e (Product 2 x)) x) 5)"
tokens:    [([leftParen]"("), ([func]"Division"), ([space]" "), ([leftParen]"("), ([func]"Derivative"), ([space]" "), ([leftParen]"("), ([func]"Logarithm"), ([space]" "), ([e]"e"), ([space]" "), ([leftParen]"("), ([func]"Product"), ([space]" "), ([number]"2"), ([space]" "), ([var]"x"), ([rightParen]")"), ([rightParen]")"), ([space]" "), ([var]"x"), ([rightParen]")"), ([space]" "), ([number]"5"), ([rightParen]")")]
parsed to: [([leftParen]"("), ([func]"Division"), ([space]" "), ([leftParen]"("), ([func]"Derivative"), ([space]" "), ([leftParen]"("), ([func]"Logarithm"), ([space]" "), ([e]"e"), ([space]" "), ([leftParen]"("), ([func]"Product"), ([space]" "), ([2]"2"), ([space]" "), ([x]"x"), ([rightParen]")"), ([rightParen]")"), ([space]" "), ([x]"x"), ([rightParen]")"), ([space]" "), ([5]"5"), ([rightParen]")")]
--------------
contexts: [[]]
levels:   [([stringBeg])]
token:    ([leftParen]"(")
pushing level on leftParen…
--------------
contexts: [[], []]
levels:   [([stringBeg]), ([leftParen]"(")]
token:    ([func]"Division")
--------------
contexts: [[], [([func]"Division")]]
levels:   [([stringBeg]), ([leftParen]"(")]
token:    ([space]" ")
--------------
contexts: [[], [([func]"Division"), ([space]" ")]]
levels:   [([stringBeg]), ([leftParen]"(")]
token:    ([leftParen]"(")
pushing level on leftParen…
--------------
contexts: [[], [([func]"Division"), ([space]" ")], []]
levels:   [([stringBeg]), ([leftParen]"("), ([leftParen]"(")]
token:    ([func]"Derivative")
--------------
contexts: [[], [([func]"Division"), ([space]" ")], [([func]"Derivative")]]
levels:   [([stringBeg]), ([leftParen]"("), ([leftParen]"(")]
token:    ([space]" ")
--------------
contexts: [[], [([func]"Division"), ([space]" ")], [([func]"Derivative"), ([space]" ")]]
levels:   [([stringBeg]), ([leftParen]"("), ([leftParen]"(")]
token:    ([leftParen]"(")
pushing level on leftParen…
--------------
contexts: [[], [([func]"Division"), ([space]" ")], [([func]"Derivative"), ([space]" ")], []]
levels:   [([stringBeg]), ([leftParen]"("), ([leftParen]"("), ([leftParen]"(")]
token:    ([func]"Logarithm")
--------------
contexts: [[], [([func]"Division"), ([space]" ")], [([func]"Derivative"), ([space]" ")], [([func]"Logarithm")]]
levels:   [([stringBeg]), ([leftParen]"("), ([leftParen]"("), ([leftParen]"(")]
token:    ([space]" ")
--------------
contexts: [[], [([func]"Division"), ([space]" ")], [([func]"Derivative"), ([space]" ")], [([func]"Logarithm"), ([space]" ")]]
levels:   [([stringBeg]), ([leftParen]"("), ([leftParen]"("), ([leftParen]"(")]
token:    ([e]"e")
--------------
contexts: [[], [([func]"Division"), ([space]" ")], [([func]"Derivative"), ([space]" ")], [([func]"Logarithm"), ([space]" "), ([e]"e")]]
levels:   [([stringBeg]), ([leftParen]"("), ([leftParen]"("), ([leftParen]"(")]
token:    ([space]" ")
--------------
contexts: [[], [([func]"Division"), ([space]" ")], [([func]"Derivative"), ([space]" ")], [([func]"Logarithm"), ([space]" "), ([e]"e"), ([space]" ")]]
levels:   [([stringBeg]), ([leftParen]"("), ([leftParen]"("), ([leftParen]"(")]
token:    ([leftParen]"(")
pushing level on leftParen…
--------------
contexts: [[], [([func]"Division"), ([space]" ")], [([func]"Derivative"), ([space]" ")], [([func]"Logarithm"), ([space]" "), ([e]"e"), ([space]" ")], []]
levels:   [([stringBeg]), ([leftParen]"("), ([leftParen]"("), ([leftParen]"("), ([leftParen]"(")]
token:    ([func]"Product")
--------------
contexts: [[], [([func]"Division"), ([space]" ")], [([func]"Derivative"), ([space]" ")], [([func]"Logarithm"), ([space]" "), ([e]"e"), ([space]" ")], [([func]"Product")]]
levels:   [([stringBeg]), ([leftParen]"("), ([leftParen]"("), ([leftParen]"("), ([leftParen]"(")]
token:    ([space]" ")
--------------
contexts: [[], [([func]"Division"), ([space]" ")], [([func]"Derivative"), ([space]" ")], [([func]"Logarithm"), ([space]" "), ([e]"e"), ([space]" ")], [([func]"Product"), ([space]" ")]]
levels:   [([stringBeg]), ([leftParen]"("), ([leftParen]"("), ([leftParen]"("), ([leftParen]"(")]
token:    ([2]"2")
--------------
contexts: [[], [([func]"Division"), ([space]" ")], [([func]"Derivative"), ([space]" ")], [([func]"Logarithm"), ([space]" "), ([e]"e"), ([space]" ")], [([func]"Product"), ([space]" "), ([2]"2")]]
levels:   [([stringBeg]), ([leftParen]"("), ([leftParen]"("), ([leftParen]"("), ([leftParen]"(")]
token:    ([space]" ")
--------------
contexts: [[], [([func]"Division"), ([space]" ")], [([func]"Derivative"), ([space]" ")], [([func]"Logarithm"), ([space]" "), ([e]"e"), ([space]" ")], [([func]"Product"), ([space]" "), ([2]"2"), ([space]" ")]]
levels:   [([stringBeg]), ([leftParen]"("), ([leftParen]"("), ([leftParen]"("), ([leftParen]"(")]
token:    ([x]"x")
--------------
contexts: [[], [([func]"Division"), ([space]" ")], [([func]"Derivative"), ([space]" ")], [([func]"Logarithm"), ([space]" "), ([e]"e"), ([space]" ")], [([func]"Product"), ([space]" "), ([2]"2"), ([space]" "), ([x]"x")]]
levels:   [([stringBeg]), ([leftParen]"("), ([leftParen]"("), ([leftParen]"("), ([leftParen]"(")]
token:    ([rightParen]")")
popping level on rightParen…
parsing     [([func]"Product"), ([space]" "), ([2]"2"), ([space]" "), ([x]"x")]
splitting on {space}
splitted: [[([func]"Product")], [([2]"2")], [([x]"x")]]
parsing     [([2]"2")]
parsing     [([x]"x")]
…popped level
--------------
contexts: [[], [([func]"Division"), ([space]" ")], [([func]"Derivative"), ([space]" ")], [([func]"Logarithm"), ([space]" "), ([e]"e"), ([space]" "), ([(Product 2 x)]"(Product 2 x)")]]
levels:   [([stringBeg]), ([leftParen]"("), ([leftParen]"("), ([leftParen]"(")]
token:    ([rightParen]")")
popping level on rightParen…
parsing     [([func]"Logarithm"), ([space]" "), ([e]"e"), ([space]" "), ([(Product 2 x)]"(Product 2 x)")]
splitting on {space}
splitted: [[([func]"Logarithm")], [([e]"e")], [([(Product 2 x)]"(Product 2 x)")]]
parsing     [([e]"e")]
parsing     [([(Product 2 x)]"(Product 2 x)")]
…popped level
--------------
contexts: [[], [([func]"Division"), ([space]" ")], [([func]"Derivative"), ([space]" "), ([(Logarithm e (Product 2 x))]"(Logarithm e (Product 2 x))")]]
levels:   [([stringBeg]), ([leftParen]"("), ([leftParen]"(")]
token:    ([space]" ")
--------------
contexts: [[], [([func]"Division"), ([space]" ")], [([func]"Derivative"), ([space]" "), ([(Logarithm e (Product 2 x))]"(Logarithm e (Product 2 x))"), ([space]" ")]]
levels:   [([stringBeg]), ([leftParen]"("), ([leftParen]"(")]
token:    ([x]"x")
--------------
contexts: [[], [([func]"Division"), ([space]" ")], [([func]"Derivative"), ([space]" "), ([(Logarithm e (Product 2 x))]"(Logarithm e (Product 2 x))"), ([space]" "), ([x]"x")]]
levels:   [([stringBeg]), ([leftParen]"("), ([leftParen]"(")]
token:    ([rightParen]")")
popping level on rightParen…
parsing     [([func]"Derivative"), ([space]" "), ([(Logarithm e (Product 2 x))]"(Logarithm e (Product 2 x))"), ([space]" "), ([x]"x")]
splitting on {space}
splitted: [[([func]"Derivative")], [([(Logarithm e (Product 2 x))]"(Logarithm e (Product 2 x))")], [([x]"x")]]
parsing     [([(Logarithm e (Product 2 x))]"(Logarithm e (Product 2 x))")]
parsing     [([x]"x")]
Division.simplify: (Division 2 (Product 2 x)) divided to (Division 1 x)
…popped level
--------------
contexts: [[], [([func]"Division"), ([space]" "), ([(Division 1 x)]"(Derivative (Logarithm e (Product 2 x)) x)")]]
levels:   [([stringBeg]), ([leftParen]"(")]
token:    ([space]" ")
--------------
contexts: [[], [([func]"Division"), ([space]" "), ([(Division 1 x)]"(Derivative (Logarithm e (Product 2 x)) x)"), ([space]" ")]]
levels:   [([stringBeg]), ([leftParen]"(")]
token:    ([5]"5")
--------------
contexts: [[], [([func]"Division"), ([space]" "), ([(Division 1 x)]"(Derivative (Logarithm e (Product 2 x)) x)"), ([space]" "), ([5]"5")]]
levels:   [([stringBeg]), ([leftParen]"(")]
token:    ([rightParen]")")
popping level on rightParen…
parsing     [([func]"Division"), ([space]" "), ([(Division 1 x)]"(Derivative (Logarithm e (Product 2 x)) x)"), ([space]" "), ([5]"5")]
splitting on {space}
splitted: [[([func]"Division")], [([(Division 1 x)]"(Derivative (Logarithm e (Product 2 x)) x)")], [([5]"5")]]
parsing     [([(Division 1 x)]"(Derivative (Logarithm e (Product 2 x)) x)")]
parsing     [([5]"5")]
…popped level
parsing     [([(Division 1 (Product 5 x))]"(Division (Derivative (Logarithm e (Product 2 x)) x) 5)")]
Simple.simplify: before substitution: (Division 1 (Product 5 x))
Simple.simplify: after substitution:  (Division 1 (Product 5 x))
(Division (Derivative (Logarithm e (Product 2 x)) x) 5) = 0.2/x
```


Source Files
------------

+ classes.txt      (a list of the files to compile)
+ ArrayLists.java  (general ArrayList methods I needed, like for splitting on operations)
+ ArraySearch.java (general Array search method I needed)
+ Simple.java      (a simple simplification user interface)
+ Derivative.java  (a user interface for differentiation (also a derivative class))
+ Expr.java        (superclass for expressions (including sums, variables, e))
+ // BoolExpr.java    (superclass for boolean Exprs)
+ Constant.java    (superclass for constants (e, pi, numbers))
+ Operation.java   (superclass for operations (like sums, products))
+ Function.java    (abstract functions (like sin, log, sqrt))
+ // Conditional.java (conditional stuff)
+ ExprParser.java  (the longest (and most complex), parses an expression from a string)
+ Subscript.java   (a mathematical subscript, like for a logarithm)
+ Context.java     (keeps track of variable names for parsing)
+ PartialParseExpr.java (class for partially-parsed expressions)
+ Token.java       (general container class for partially- or fully-parsed expressions)
+ TokenList.java   (ArrayList of Tokens)
+ Tokenizer.java   (a Tokenizer)
+ TokenParser.java (abstractly parses individual Tokens)
+ ExprTokenParser.java (parses individual pretty Tokens)
+ SExprTokenParser.java (parses individual s-expression Tokens)
+ LevelsParser.java (parses inner levels (e.g., parens) first)
+ LevelParser.java (interface for parsing levels)
+ ExprLevelParser.java (parses pretty-levels)
+ SExprLevelParser.java (parses s-expr-levels)
+ AfterLevelPopHandler.java (to handle after-(level pop)s (like for functional parens))
+ ParenFunctioning.java (handles after-(level pop)s for paren functioning)
+ TokenListMapper.java (to map sequences and subsequences of Tokens)
+ Number.java      (a number, with its value stored in a Double)
+ E.java           (an e constant)
+ Pi.java          (a π (pi) constant)
+ Var.java         (a variable (like x, y, z, a))
+ Sum.java         (a sum of expressions)
+ Product.java     (a product of expressions)
+ Exponent.java    (an exponent with a base and exponent)
+ Division.java    (a quotient with a numerator and denominator)
+ Logarithm.java   (a logarithm)
+ Sin.java         (the trigonometric sine function)
+ Cos.java         (the trigonometric cosine function)
+ Undef.java       (undefined (undef))
+ // Bool.java        (true (yes) or false (no))
