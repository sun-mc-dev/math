# SunMC-Math

A math expression parser and evaluator for Java 21+. Parses strings like `"2x + sin(y)"` into evaluable expressions using the Shunting Yard algorithm.

Built with sealed interfaces, records, and pattern matching — no reflection, no dependencies.

[![](https://jitpack.io/v/sun-mc-dev/math.svg)](https://jitpack.io/#sun-mc-dev/math)

## Installation

**Maven**

Add the JitPack repository and the dependency:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.sun-mc-dev</groupId>
    <artifactId>math</artifactId>
    <version>1.0.0</version>
</dependency>
```

**Gradle**

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.sun-mc-dev:math:1.0.0'
}
```

## Quick Start

```java
// simple evaluation
double result = new ExpressionBuilder("3 + 4 * 2")
    .build()
    .evaluate(); // 11.0

// with variables
Expression expr = new ExpressionBuilder("2x + 3y")
    .variables("x", "y")
    .build()
    .setVariable("x", 5)
    .setVariable("y", 10);

expr.evaluate(); // 40.0

// built-in functions and constants work out of the box
new ExpressionBuilder("sin(pi / 2) + log(e)")
    .build()
    .evaluate(); // 2.0
```

## Features

- **Implicit multiplication** — `2x`, `3sin(x)`, `(2)(3)` all just work. Can be toggled off if you don't want it.
- **35+ built-in functions** — trig, hyperbolic, logarithmic, rounding, combinatorics, and more.
- **8 built-in constants** — `pi`, `e`, `tau`, `phi`, `gamma`, `sqrt2`, `inf`, `nan` (plus Unicode aliases like `π`, `τ`, `φ`, `γ`, `∞`).
- **Custom functions and operators** — register your own with a couple lines of code.
- **Validation** — check expressions before evaluating to get human-readable error messages.
- **Async evaluation** — `evaluateAsync()` runs on virtual threads, or pass your own executor.
- **JPMS module** — `me.sunmc.math`, exports only the public API packages.
- **Zero dependencies** — just the JDK.

## Usage

### Variables

Declare variables when building, set values before evaluating:

```java
Expression expr = new ExpressionBuilder("a^2 + b^2")
    .variables("a", "b")
    .build()
    .setVariable("a", 3)
    .setVariable("b", 4);

expr.evaluate(); // 25.0
```

You can also use a `VariableProvider` for lazy/dynamic resolution:

```java
Map<String, Double> lookup = Map.of("x", 42.0);

Expression expr = new ExpressionBuilder("x + 1")
    .variables("x")
    .build()
    .setVariableProvider(lookup::get);

expr.evaluate(); // 43.0
```

### Custom Functions

Use the `MathFunction` factory methods:

```java
// single argument
MathFunction doubleIt = MathFunction.of("double", x -> x * 2);

// two arguments
MathFunction avg = MathFunction.of("avg", (a, b) -> (a + b) / 2);

// n arguments
MathFunction sum3 = MathFunction.of("sum3", 3, args -> args[0] + args[1] + args[2]);

Expression expr = new ExpressionBuilder("double(avg(10, 20))")
    .functions(doubleIt, avg)
    .build();

expr.evaluate(); // 30.0
```

### Custom Operators

```java
MathOperator factorial = MathOperator.unary("!", x -> {
    int n = (int) x;
    double result = 1;
    for (int i = 2; i <= n; i++) result *= i;
    return result;
});

MathOperator bitwiseAnd = MathOperator.binary(
    "&", Associativity.LEFT, Precedence.ADDITION,
    (a, b) -> (double) ((long) a & (long) b)
);

Expression expr = new ExpressionBuilder("5! & 127")
    .operator(factorial)
    .operator(bitwiseAnd)
    .build();

expr.evaluate(); // 120.0 & 127 = 120.0
```

### Validation

```java
Expression expr = new ExpressionBuilder("x + y")
    .variables("x", "y")
    .build()
    .setVariable("x", 1);
    // forgot to set y

ValidationResult result = expr.validate();
if (!result.isValid()) {
    result.getErrors().forEach(System.err::println);
    // "Variable 'y' has not been set"
}
```

### Async Evaluation

```java
// uses virtual threads (Java 21+)
CompletableFuture<Double> future = expr.evaluateAsync();

// or bring your own executor
CompletableFuture<Double> future = expr.evaluateAsync(myExecutor);
```

### Reusing Expressions

Expressions can be copied for thread-safe reuse — the compiled token list is shared, only variable bindings are cloned:

```java
Expression template = new ExpressionBuilder("x^2 + 1")
    .variables("x")
    .build();

// each thread gets its own copy
Expression copy = template.copy();
copy.setVariable("x", 7);
copy.evaluate(); // 50.0
```

## Built-in Functions

| Category | Functions |
|---|---|
| Trigonometric | `sin`, `cos`, `tan`, `cot`, `sec`, `csc` |
| Inverse trig | `asin`, `acos`, `atan`, `atan2` |
| Hyperbolic | `sinh`, `cosh`, `tanh`, `coth`, `sech`, `csch` |
| Inverse hyperbolic | `asinh`, `acosh`, `atanh` |
| Exponential / Log | `exp`, `expm1`, `log`, `log2`, `log10`, `log1p`, `logb` |
| Power / Root | `pow`, `sqrt`, `cbrt` |
| Rounding | `ceil`, `floor`, `round`, `rint` |
| Sign / Absolute | `abs`, `signum` |
| Conversion | `toradian`, `todegree` |
| Min / Max | `min`, `max` |
| Clamping | `clamp` |
| Combinatorics | `factorial`, `gcd`, `lcm` |
| Misc | `hypot`, `fma` |

## Built-in Constants

| Name | Aliases | Value |
|---|---|---|
| `pi` | `π` | 3.14159265… |
| `e` | — | 2.71828182… |
| `tau` | `τ` | 6.28318530… |
| `phi` | `φ` | 1.61803398… |
| `gamma` | `γ` | 0.57721566… |
| `sqrt2` | — | 1.41421356… |
| `inf` | `∞` | +∞ |
| `nan` | — | NaN |

## Built-in Operators

| Operator | Description | Associativity |
|---|---|---|
| `+`, `-` | Addition, Subtraction | Left |
| `*`, `/`, `%` | Multiplication, Division, Modulo | Left |
| `^` | Exponentiation | Right |
| `-x`, `+x` | Unary minus/plus | Right |

## Requirements

- Java 21 or later

## License

[MIT](LICENSE)