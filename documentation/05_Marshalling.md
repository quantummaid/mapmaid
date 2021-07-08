# Marshalling

To support multiple data interchange formats like JSON, XML, YAML, etc., MapMaid does not work directly on strings.
Instead, objects are serialized and deserialized to and from nested maps (`Map<String, Object>`).
This `Map<String, Object` can then be easily converted into any data interchange format of choice. 
This map is respectively deserialized from a specific format or serialized into that format.
The process of serializing that map into a format is what we call [Marshalling](https://en.wikipedia.org/wiki/Marshalling_(computer_science)) and the reverse operation Unmarshalling.
Examples of frameworks that can perform this operation include [Gson](https://github.com/google/gson), [Jackson](https://github.com/FasterXML/jackson)
and [X-Stream](https://x-stream.github.io/).

MapMaid is unaware of the format you chose to represent the string value of your objects.
Upon reception of the string input, MapMaid first asks the configured (un-)marshaller to parse the `String`
into a `Map<String, Object>`.
From now on, it operates with that `Map<String, Object>` by mapping its structure onto the pre-calculated
object hierarchy of the class to be deserialized.

Vice versa, the process is reversed on serialization.
MapMaid will take the object to be serialized and deconstructs its object hierarchy into
a `Map<String, Object>`.
This map is then passed on to the configured marshaller which will transform it into a `String`
of the chosen format (JSON, XML, etc.).

## Common marshallers
MapMaid ships with integrated marshallers for the common formats JSON and YAML as well as
the so-called `x-www-form-urlencoded` (url-encoded) format used in the HTTP protocol.
It also ships with a marshaller that is specific to the AWS DynamoDB database (see below).

### Pre-configured marshaller
MapMaid comes pre-configured with a marshaller for JSON.
They will be automatically deregistered once you manually register a marshaller.
Alternatively, you can deactivate the pre-configured marshaller like this:
<!---[CodeSnippet](deactivateDefaultMarshallers)-->
```java
final MapMaid mapMaid = MapMaid.aMapMaid()
        .withAdvancedSettings(AdvancedBuilder::doNotAutoloadMarshallers)
        .build();
```

You can manually configure the default JSON marshaller like this: 
<!---[CodeSnippet](json)-->
```java
final MapMaid mapMaid = aMapMaid()
        .withAdvancedSettings(advancedBuilder ->
                advancedBuilder.usingMarshaller(MinimalJsonMarshallerAndUnmarshaller.minimalJsonMarshallerAndUnmarshaller()))
        .withAdvancedSettings(AdvancedBuilder::doNotAutoloadMarshallers)
        .build();
```

# YAML
In order to support YAML, you need to add this dependency to your project:
<!---[CodeSnippet](yamldependency)-->
```xml
<dependency>
    <groupId>de.quantummaid.mapmaid.integrations</groupId>
    <artifactId>mapmaid-snakeyaml</artifactId>
    <version>0.10.16</version>
</dependency>
```

YAML marshalling and unmarshalling can then be configured like this:
<!---[CodeSnippet](yaml)-->
```java
final MapMaid mapMaid = aMapMaid()
        .withAdvancedSettings(advancedBuilder ->
                advancedBuilder.usingMarshaller(SnakeYamlMarshallerAndUnmarshaller.snakeYamlMarshallerAndUnmarshaller()))
        .withAdvancedSettings(AdvancedBuilder::doNotAutoloadMarshallers)
        .build();
```


### application/x-www-form-urlencoded
MapMaid's url-encoded marshaller can be added to your configuration like this:
<!---[CodeSnippet](urlencoded)-->
```java
final MapMaid mapMaid = MapMaid.aMapMaid()
        .usingRecipe(UrlEncodedMarshallerRecipe.urlEncodedMarshaller())
        .build();
```

In order to use it, you need to use the corresponding `MarshallingType`:
<!---[CodeSnippet](urlencodedusage)-->
```java
final String urlEncoded = mapMaid.serializeTo(object, urlEncoded());
```

### AWS DynamoDB `AttributeValue`
MapMaid is able to marshal and unmarshal `software.amazon.awssdk.services.dynamodb.model.AttributeValue`
data structures that are used in the AWS DynamoDB SDK. To use it, add the following dependency:

<!---[CodeSnippet](dynamodbdependency)-->
```xml
<dependency>
    <groupId>de.quantummaid.mapmaid.integrations</groupId>
    <artifactId>mapmaid-dynamodb</artifactId>
    <version>0.10.16</version>
</dependency>
```

Configuration:
<!---[CodeSnippet](attributeValue)-->
```java
final MapMaid mapMaid = MapMaid.aMapMaid()
        .serializingAndDeserializing(ComplexPerson.class)
        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingMarshaller(AttributeValueMarshallerAndUnmarshaller.attributeValueMarshallerAndUnmarshaller()))
        .build();
```
Serialization:
<!---[CodeSnippet](attributeValueSerialization)-->
```java
final AttributeValue attributeValue = mapMaid.serializeTo(object, AttributeValueMarshallerAndUnmarshaller.DYNAMODB_ATTRIBUTEVALUE);
```

Deserialization:
<!---[CodeSnippet](attributeValueDeserialization)-->
```java
final ComplexPerson deserialized = mapMaid.deserialize(attributeValue, ComplexPerson.class, AttributeValueMarshallerAndUnmarshaller.DYNAMODB_ATTRIBUTEVALUE);
```

## Registering your own marshaller
If these marshallers do not fit your needs, you can easily provide your own by implementing the
`Marshaller` and `Unmarshaller` interfaces.
In this section, we show the registration of some commonly used marshalling libraries.

### JSON with GSON

Assuming you have a configured instance of `Gson` class, adding it as a JSON marshaller for MapMaid looks like:
<!---[CodeSnippet](jsonWithGson)-->
```java
final Gson gson = new Gson(); // can be further configured depending on your needs.
final MapMaid mapMaid = MapMaid.aMapMaid()
        .serializingAndDeserializing(ComplexPerson.class)
        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(gson::toJson, input -> gson.fromJson(input, Object.class)))
        .build();
```

### JSON with ObjectMapper
<!---[CodeSnippet](jsonWithObjectMapper)-->
```java
final ObjectMapper objectMapper = new ObjectMapper();
final MapMaid mapMaid = MapMaid.aMapMaid()
        .serializingAndDeserializing(ComplexPerson.class)
        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(objectMapper::writeValueAsString, input -> objectMapper.readValue(input, Object.class)))
        .build();
```

### XML with X-Stream
<!---[CodeSnippet](xmlWithXStream)-->
```java
final XStream xStream = new XStream(new DomDriver());
xStream.alias("root", Map.class);

final MapMaid mapMaid = MapMaid.aMapMaid()
        .serializingAndDeserializing(ComplexPerson.class)
        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingXmlMarshaller(xStream::toXML, xStream::fromXML))
        .build();
```

**Note:** If you wish to marshall in/from XML, don't forget to add the appropriate dependency:

```xml
<dependency>
    <groupId>xstream</groupId>
    <artifactId>xstream</artifactId>
    <version>${xstream.version}</version>
</dependency>
```

### YAML with ObjectMapper

<!---[CodeSnippet](yamlWithObjectMapper)-->
```java
final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

final MapMaid mapMaid = MapMaid.aMapMaid()
        .serializingAndDeserializing(ComplexPerson.class)
        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(objectMapper::writeValueAsString, input -> {
            return objectMapper.readValue(input, Object.class);
        }))
        .build();
```

**Note:** Don't forget to add the appropriate dependency to use the `YAMLFactory` with the `ObjectMapper`.
```xml
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-yaml</artifactId>
    <version>${jackson.version}</version>
</dependency>
```

MapMaid does not ship with these libraries, so you need to configure the marshaller of your choice also in the dependencies of your project.
