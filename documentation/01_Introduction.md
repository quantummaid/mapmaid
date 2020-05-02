# User Guide
This guide walks you through the features of MapMaid, how to configure MapMaid, and how to get the most out of it.

## Quick Start

This chapter walks you through the necessary steps to use MapMaid.

### Maven 

In order to use MapMaid, just add the following dependency to your project:

<!---[CodeSnippet](mapmaidalldependency)-->
```xml
<dependency>
    <groupId>de.quantummaid.mapmaid.integrations</groupId>
    <artifactId>mapmaid-all</artifactId>
    <version>0.9.52</version>
</dependency>
```

MapMaid requires you to compile your project with the `-parameter` compile flag.
Doing so gives MapMaid [runtime access to parameter names](http://openjdk.java.net/jeps/118) and
enables it to map parameters automatically.
The `maven-compiler-plugin` can be easily configured to do this:
```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <parameters>true</parameters>
    </configuration>
</plugin>
```

Also, include this flag in your IDE's `javac` configuration, and make sure to rebuild your project after the 
configuration changes:

* [Configuration for IntelliJ IDEA](https://www.jetbrains.com/help/idea/java-compiler.html)
* [Configuration for Eclipse](http://help.eclipse.org/2019-03/topic/org.eclipse.jdt.doc.user/reference/preferences/java/ref-preferences-compiler.htm)


### Minimal configuration

You can now create a MapMaid serializer/deserializer for any class like this:
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
### Serialization

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

### Deserialization

If you want to deserialize a JSON string to an `Email` object, you can do so like this:

<!---[CodeSnippet](deserialization)-->
```java
final Email deserializedEmail = mapMaid.deserializeJson(json, Email.class);
```
