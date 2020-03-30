# Detection

### Default Conventions Explained
MapMaid respects the access modifiers and does not use any non-public field or method. Ever. MapMaid scans the given 
package(s), visiting every class to identify whether it is a Custom Primitive or a Serialized Object.

#### Default Conventions for Custom Primitives
A class is considered to be a Custom Primitive if it has

a) a serialization method named "stringValue" AND

b) at least one of the following (interpreted in that order):
- a static deserialization method* named "fromStringValue"
- a static deserialization method** whose name contains the class name
- a public constructor that takes exactly one single String argument

*A serialization method is public, returns an instance of String and takes no arguments.

**A deserialization method is 
public, static, returns an instance of the class it is declared in and takes one parameter of type String. 

Example:
 
```java
public final class EmailAddress {
    private final String value;

    public static EmailAddress anEmailAddress(final String value) {
        final String validated = EmailAddressValidator.ensureEmailAddress(value, "emailAddress");
        return new EmailAddress(validated);
    }

    public String stringValue() {
        return this.value;
    }
}
```
The method "anEmailAddress" is a valid deserialization method, since it's name contains the className. Other valid 
names would be "deserialize", "theEmailAddressWithValue", etc.

#### Default Conventions for Serialized Objects 
A class is considered to be a Serialized Object if it has a public static factory method name "deserialize". 
Alternatively, it is also considered as such if the name of the class matches one of the patterns
```
.*DTO
.*Dto
.*Request
.*Response
.*State
```
AND MapMaid can find either a "conventional deserialization method" or, alternatively, a public constructor.

The detection of a "conventional deserialization method" follows this algorithm:

1. If the class has a single factory method -> that's the one
2. alternatively, if there are multiple factory methods, the one called "deserialize" wins
3. if, for some reason, you have multiple factory methods named "deserialize", the one that has all the fields as 
parameters wins
4. alternatively, if there is no factory method called "deserialize", the factory methods named after the class are
inspected with the same logic as point 3.

Example of the last point:

```java
public final class EmailDto {
    public final transient String saltInMySoup;
    public final EmailAddress sender;
    public final EmailAddress receiver;
    public final Subject subject;
    public final Body body;

    public static EmailDto emptyBodied(final EmailAddress sender,
                                       final EmailAddress receiver,
                                       final Subject subject) {
        return emailDto(sender, receiver, subject, Body.empty());
    }

    public static EmailDto emailDto(final EmailAddress sender,
                                    final EmailAddress receiver,
                                    final Subject subject,
                                    final Body body) {
        RequiredParameterValidator.ensureNotNull(sender, "sender");
        RequiredParameterValidator.ensureNotNull(receiver, "receiver");
        RequiredParameterValidator.ensureNotNull(body, "body");
        return new EmailDto("There it is", sender, receiver, subject, body);
    }
}
```
Here, the last method wins, since it is called `emailDto`. If we were to add another factory method called deserialize 
here, that one would be picked.

The detection of the public constructor follows this algorithm:
1. If the class has a single public constructor -> that's the one
2. if you have multiple public constructors, the one that has all the fields as 
parameters wins

Serialized Objects are serialized using the public fields(key:value) and deserialized using the same public factory
method that was used to determine the class being a Serialized Object

Example of usage of the _Conventional_ MapMaid can be found in [ConventionalBuilderTest](../core/src/test/java/de/quantummaid/mapmaid/builder/ConventionalBuilderTest.java)

### Overriding Default Conventions
We understand that not everybody agrees with the way we decided to name the default methods. We made sure to provide 
you with builder methods to override any default conventions.

### Using Different Names / Name Patterns
If you only want to override the default method names, and/or the Serialized Object detection patterns, you can use an 
instance of the 
[ConventionalDetector](../core/src/main/java/de/quantummaid/mapmaid/builder/conventional/ConventionalDetector.java)
and configure the preferred Custom Primitive serialization/deserialization method names, Serialized Object 
deserialization method name, and class name patterns to use for Serialized Object.

<!---[CodeSnippet](detector)-->
```java
MapMaid.aMapMaid()
        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(
                new Gson()::toJson,
                input -> new Gson().fromJson(input, Object.class)))
        .build();
```

Also take a look at [CustomConventionalBuilderTest](../core/src/test/java/de/quantummaid/mapmaid/builder/CustomConventionalBuilderTest.java) 
for some examples of customizing the conventions.
