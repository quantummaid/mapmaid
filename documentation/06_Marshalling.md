# Marshalling

## (Un-)marshalling

To support multiple formats like JSON, XML, YAML, etc. Serialized Objects are converted into Maps of Maps and Strings.
This map is respectively deserialized from a specific format or serialized into that format.
The process of serializing that map into a format is what we call [Marshalling](https://en.wikipedia.org/wiki/Marshalling_(computer_science)) and the reverse operation Unmarshalling.
Examples of these frameworks include [Gson](https://github.com/google/gson), [Jackson](https://github.com/FasterXML/jackson), [X-Stream](https://x-stream.github.io/).

MapMaid is unaware of the format you chose to represent the string value of your objects.
Upon reception of the string input, MapMaid first asks the configured (un-)marshaller to parse the `String`
into a `Map<String, Object>`.
From now on, it operates with that `Map<String, Object>` by mapping its structure onto the pre-calculated
object hierarchy of the class to be deserialized.

Vice versa, the process is reverse on serialization.
MapMaid will take the object to be serialized and deconstructs its object hierarchy into
a `Map<String, Object>`.
This map is then passed on to the configured marshaller which will transform it into a `String`
of the chosen format (Json, XML, etc.).

## Common marshallers
MapMaid ships with integrated marshallers for the common formats Json, XML and YAML as well as
the so-called `x-www-form-urlencoded` (url-encoded) format used in the HTTP protocol.

### Pre-configured marshallers
MapMaid comes pre-configured with marshallers for Json, XML and YAML.
They will be automatically deregistered once you manually register a marshaller.
Alternatively, you can deactivate the pre-configured marshallers like this:
<!---[CodeSnippet](deactivateDefaultMarshallers)-->
```java
final MapMaid mapMaid = MapMaid.aMapMaid()
        .withAdvancedSettings(AdvancedBuilder::doNotAutoloadMarshallers)
        .build();
```

You can manually configure the default Json marshaller like this: 
<!---[CodeSnippet](json)-->
```java
final MapMaid mapMaid = aMapMaid()
        .usingRecipe(jacksonMarshallerJson())
        .withAdvancedSettings(AdvancedBuilder::doNotAutoloadMarshallers)
        .build();
```

Respectively, to only support XML:
<!---[CodeSnippet](xml)-->
```java
final MapMaid mapMaid = aMapMaid()
        .usingRecipe(jacksonMarshallerXml())
        .withAdvancedSettings(AdvancedBuilder::doNotAutoloadMarshallers)
        .build();
```

And to only support YAML:
<!---[CodeSnippet](yaml)-->
```java
final MapMaid mapMaid = aMapMaid()
        .usingRecipe(jacksonMarshallerYaml())
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

## Registering your own marshaller
If these marshallers do not fit your needs, you can easily provide your own by implementing the
[Marshaller](../core/src/main/java/de/quantummaid/mapmaid/serialization/Marshaller.java) and 
[Unmarshaller](../core/src/main/java/de/quantummaid/mapmaid/deserialization/Unmarshaller.java) interfaces.
In this section, we show the registration of some commonly used marshalling libraries.

### JSON with GSON

Assuming you have a configured instance of `Gson` class, adding it as a JSON Marshaller for MapMaid looks like:
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

Note: If you wish to marshall in/from XML, don't forget to add the appropriate dependency:

```xml
<dependency>
    <groupId>xstream</groupId>
    <artifactId>xstream</artifactId>
    <version>${xstream.version}</version>
</dependency>
```

### Yaml with ObjectMapper

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

Note: Don't forget to add the appropriate dependency to use the YAMLFactory with the ObjectMapper.
```xml
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-yaml</artifactId>
    <version>${jackson.version}</version>
</dependency>
```

MapMaid does not ship with these libraries, so you need to configure the marshaller of your choice also in the dependencies of your project.
