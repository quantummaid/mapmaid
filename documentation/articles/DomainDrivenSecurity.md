# Domain Driven Security

Domain-Driven Security is an approach to address technical security risks like injection attacks (SQL injection, 
cross-site scripting, ...), and **insecure deserialization** using core principals of Domain-Driven Design. 
If you've never heard of Domain Driven Design, Bounded Contexts, ubiquitous language, but know about Objects,
Constructors and Factory Methods - don't worry - you know all the building blocks already.

## Brief Introduction

Let's analyse an imaginary "Order Book" method signature taken from some interface:

```java
void orderBook(final String isbn, final int amount);
```

Given only that method signature, an eager unit tester will know that `amount` can be 0 or even -100. The impact of 0
is not as hazardous as of an automated payment system transferring money from the book store to the customers account
in case of -100, yet worth testing.

Of course the method can start with some `if` statements to ensure the value of the amount is within a valid range. But
the moment you add another method dealing with `amount`(e.g. your repository, DAO, another slightly different orderBook
method), you'd have to do the same thing again. A better approach would be to create a small class for `isbn` and
`amount`(We'll focus on OrderAmount to keep things short):

```java
public final class OrderAmount {
    private final int value;
    
    private OrderAmount(int value) {
        this.value = value;
    }
    
    public static OrderAmount fromStringValue(final String value) {
        final int valid = IntegerValidator.ensureIntBetween(1, 100, value, "Invalid order amount");
        return new OrderAmount(valid);
    }
    //...
}
```

The imaginary "Order Book" method signature would change into something like this:

```java
void orderBook(final Isbn isbn, final OrderAmount amount);
```
Now, the compiler will ensure that `isbn` and `amount` are either null or a valid isbn and amount - no matter where you
are using it.

## Conference Talks

A brief description of what that means is available here: [Øredev 2014 - Security For Developers](https://youtu.be/CZZIoLZyqTM?t=1018)

A more in depth presentation about the topic is available here: [Domain Driven Security (Daniel Deogun - Dan Bergh Johnsson)](https://www.youtube.com/watch?v=9mGsLcruhwQ)

## FAQ

**Is all of this effort really necessary?**

Quality/Security usually doesn't come free of charge and you have to weight the risk and cost of mitigation.
A good source to get started doing so is [OWASP Top 10-2017 A1-Injection](https://www.owasp.org/index.php/Top_10-2017_A1-Injection)

> Injection can result in data loss, corruption, or disclosure to unauthorized parties, loss of accountability,
 or denial of access. Injection can sometimes lead to complete host takeover. The business impact depends on
 the needs of the application and data.

and [Top 10-2017 A8-Insecure Deserialization](https://www.owasp.org/index.php/Top_10-2017_A8-Insecure_Deserialization)
> The impact of deserialization flaws cannot be overstated. These flaws can lead to remote code execution attacks,
 one of the most serious attacks possible. The business impact depends on the protection needs of the application
 and data.

We are trying our best to make the use of that technique as easy, fast and cheap as possible.
The example code can be further optimized in that regard by following our Best Practices.

**Why does it have to be a factory method?**

Constructors are less powerful/more limited than factory methods. See
[Replace Constructor with Factory Method](https://refactoring.guru/replace-constructor-with-factory-method) for a brief explanation.


**Why is the input value to the factory method a string?**

MapMaid supports Integers or int's in these cases as well. In caparison to using a string, the int has the disadvantage
that your validation of the input is now happening in at least 2 different places:

- The parser, that is parsing xml,json, yaml, ...
- The factory method

That makes it harder to write your validation error message generation in a way that is uniform and nice experience for
the user.
