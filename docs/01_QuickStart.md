# Quick Start

## Maven 

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.quantummaid.mapmaid/core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.quantummaid.mapmaid/core)

```xml
<dependency>
    <groupId>de.quantummaid.mapmaid</groupId>
    <artifactId>core</artifactId>
    <version>${mapmaid.version}</version>
</dependency>
```

## Compiler Configuration

MapMaid uses method parameter names to construct your objects, hence requires you to compile with parameter names.
This is configured by passing the `-parameters` flag to the java compiler.

Maven configuration:
```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <compilerArgs>
            <arg>-parameters</arg>
        </compilerArgs>
    </configuration>
</plugin>
```

For your IDE it's as simple as having it compile using the `-parameters` command-line argument of java compiler.

## Minimal Configuration

MapMaid needs to know the package where your [Custom Primitives](Concepts.md#custom-primitives) and [Serialized Objects](Concepts.md#serialized-objects) reside. MapMaid is also unaware 
of the chosen format, hence needs to be configured with [(Un)marshaller](Concepts.md#unmarshalling) to deal with the format conversion. 

If you are following the [default conventions](UserGuide.md#default-conventions-explained), and have chosen JSON as format, along with Gson as marshaller, here is the minimal configuration you need to get access to `serializer` and `deserializer` 

<!---[CodeSnippet](instance)-->
```java
final MapMaid mapMaid = MapMaid.aMapMaid()
        .serializingAndDeserializing(Email.class)
        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(new Gson()::toJson, new Gson()::fromJson))
        .build();
```

Read the [User Guide](UserGuide.md#configuring-mapmaid-instance) for detailed description on how to further configure the MapMaid instance.

#### Serialization

Now serializing the object

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

will produce

```json
{
  "receiver": "receiver@example.com",
  "body": "Hello World!!!",
  "sender": "sender@example.com",
  "subject": "Hello"
}
```

#### Deserialization

Using same `mapMaid` instance

<!---[CodeSnippet](deserialization)-->
```java
final Email deserializedEmail = mapMaid.deserializeJson(json, Email.class);
```

will produce an object equal to `email`.
