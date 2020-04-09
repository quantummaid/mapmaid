# Domain-Driven Security

Domain-Driven Security addresses technical security risks like injection attacks (SQL injection, 
cross-site scripting, etc.) and **insecure deserialization** using the core principles of Domain-Driven Design.
This chapter will explain the concept in a way that does not require knowledge of Domain-Driven Design. 

## Brief introduction

Let's analyse an imaginary `orderBook` method signature taken from some interface:

<!---[CodeSnippet](orderBook1)-->
```java
void orderBook(String isbn, int amount);
```

Given only the method signature, `amount` can be any valid number - including `0` or even `-100`.
The impact of `-100` can be hazardous.
A negative number of books could result in a negative subtotal - that is free cash for a malicious customer.

Of course the method might start with some `if` statements to ensure the value of the amount is within a valid range.
But the moment you add another method dealing with `amount` (a repository, a database access object, another slightly different `orderBook`
method), you'd have to do the same thing again.

Alternatively, you could create small classes for `isbn` and
`amount` (we'll focus on `amount` to keep things short):

<!---[CodeSnippet](orderAmount)-->
```java
public final class OrderAmount {
    private final int value;

    private OrderAmount(final int value) {
        this.value = value;
    }

    public static OrderAmount fromStringValue(final String value) {
        final int valid = IntegerValidator.ensureIntBetween(1, 100, value, "Invalid order amount");
        return new OrderAmount(valid);
    }

    public int getValue() {
        return this.value;
    }
}
```


The `orderBook` method signature would change into something like this:

<!---[CodeSnippet](orderBook2)-->
```java
void orderBook(Isbn isbn, OrderAmount amount);
```

Now, the compiler will ensure that `isbn` and `amount` are either null or a valid isbn and amount - no matter where you
are using it.

## JSR 303 (Bean Validation)
MapMaid and Domain-Driven Security encourage developers to validate input values with conventional Java code.
For example, a `String` can be validated by matching a regular expression and then throwing an exception.
Another common way to validate input values are [JSR 303 annotations](https://en.wikipedia.org/wiki/Bean_Validation).
A class validated in this way would look like this:
<!---[CodeSnippet](jsr303)-->
```java
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class Player {
    private String name;

    @Min(10)
    @Max(110)
    private int age;

    public void setName(String name) {
        this.name = name;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getAge() {
        return this.age;
    }
}
```
MapMaid does not encourage the use of JSR 303. Validations like this are generally not enforceable.
They always rely on a framework to execute the actual validation.
Even if the framework is called correctly, any line of code might change any value at any time - most often by accident.
**You can never be sure whether an object of a class that relies on JSR 303 validation is actually in a valid state. This defeats the purpose of validation.**

A related problem is the fact that JSR 303 prevents you from declaring any fields as `final`.
Because immutability is a desirable trait, this is a serious problem.
Another downside to JSR 303 validation is the required dependency overhead.
For the `Player` class to compile, you would need to add at least the following dependency to your
`pom.xml`:
<!---[CodeSnippet](jsr303dependency)-->
```xml
<dependency>
    <groupId>javax.validation</groupId>
    <artifactId>validation-api</artifactId>
    <version>2.0.1.Final</version>
</dependency>
```
Since validation is an overarching concern, you would have to add this dependency to all modules.
This includes core modules that should be shielded from dependencies as much as possible
to guarantee clean architecture.

## Conference talks

A brief introduction: [Øredev 2014 - Security For Developers](https://www.youtube.com/watch?v=CZZIoLZyqTM&t=1018)

A more in-depth presentation: [Domain Driven Security (Daniel Deogun - Dan Bergh Johnsson)](https://www.youtube.com/watch?v=9mGsLcruhwQ)

## FAQ

**Is all of this effort really necessary?**

Quality and security usually doesn't come free of charge and you have to weigh the risk and cost of mitigation.
A good source to get started is [OWASP Top 10-2017 A1-Injection](https://www.owasp.org/index.php/Top_10-2017_A1-Injection):

> Injection can result in data loss, corruption, or disclosure to unauthorized parties, loss of accountability,
 or denial of access. Injection can sometimes lead to complete host takeover. The business impact depends on
 the needs of the application and data.

As well as [Top 10-2017 A8-Insecure Deserialization](https://www.owasp.org/index.php/Top_10-2017_A8-Insecure_Deserialization):
> The impact of deserialization flaws cannot be overstated. These flaws can lead to remote code execution attacks,
 one of the most serious attacks possible. The business impact depends on the protection needs of the application
 and data.

We are trying our best to make the use of Domain-Driven Security as easy, fast and cheap as possible.

**Why do you use a factory method?**

Constructors are less powerful/more limited than factory methods. See
[*Replace Constructor with Factory Method*](https://refactoring.guru/replace-constructor-with-factory-method) for a brief explanation.


**Why is the input value to the factory method a `String`?**

MapMaid supports `Integer`s or `int`s in these cases as well. In comparison to `String`, `int` has the disadvantage
that your validation of the input is now happening in at least two different places:

1. The XML/JSON/YAML/etc. parser
2. The factory method

This makes it harder to write your validation error message generation in a uniform way that is also a nice experience for
the user.
