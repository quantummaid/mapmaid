# Quick Start

## Maven 

In order to use MapMaid, just add the following dependency to your project:

<!---[CodeSnippet](mapmaidalldependency)-->
```xml
<dependency>
    <groupId>de.quantummaid.mapmaid.integrations</groupId>
    <artifactId>mapmaid-all</artifactId>
    <version>0.9.31</version>
</dependency>
```

## Compiler Configuration

MapMaid uses method parameter names to construct your objects, hence requires you to compile with parameter names.
This is configured by passing the `-parameters` flag to the java compiler. The `maven-compiler-plugin`
can be easily configured to do this:
```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <parameters>true</parameters>
    </configuration>
</plugin>
```

For your IDE it's as simple as having it compile using the `-parameters` command-line argument of the Java compiler.

## Minimal Configuration

You can now create a MapMaid (de-)serializer for any class like this:
<!---[CodeSnippet](instance)-->
```java
final MapMaid mapMaid = MapMaid.aMapMaid()
        .serializingAndDeserializing(Email.class)
        .build();
```
MapMaid will intelligently analyze the `Email` class and make an educated guess on how the class should be serialized and
deserialized. It will take into account features like public constructors, static factory methods, public fields,
getters/setters and more.
If necessary, it will continue to apply the same logic recursively to all referenced classes in `Email`.
If it is unable to find a way to (de-)serialize any class during the process or a decision is ambiguous,
it will complain by throwing an exception with a detailed explanation.
MapMaid then offers intuitive but powerful ways to quickly resolve these conflicts.
#### Serialization

Serializing an object of type `Email` works like this:

<!---[CodeSnippet](serialization)-->
```java
final Email email = Email.deserialize(
        EmailAddress.fromStringValue("sender@example.com"),
        EmailAddress.fromStringValue("receiver@example.com"),
        Subject.fromStringValue("Hello"),
        Body.fromStringValue("Hello World!!!")
);

final String json = mapMaid.serializeToJson(email);
```

It will yield the following JSON representation:

```json
{
  "receiver": "receiver@example.com",
  "body": "Hello World!!!",
  "sender": "sender@example.com",
  "subject": "Hello"
}
```

#### Deserialization

If you want to deserialize a JSON string to an `Email` object, you can do so like this:

<!---[CodeSnippet](deserialization)-->
```java
final Email deserializedEmail = mapMaid.deserializeJson(json, Email.class);
```
