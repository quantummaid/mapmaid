# JSON Primitive Data Types

MapMaid is best in the environments where you control both the sender and the receiver of the data. Examples include "client-service", "service-service", "service-database", "publish/subscriber" communications. If you find yourself in such an environment, and agree with the ["validate once"](Concepts.md#string-representation) approach, you would be sending and receiving Strings always and having full control upon how your objects are created and validated in the language that has received the data.

However, if you have no control over the data format being passed around and would still like to use MapMaid, we have provided a couple of Recipes the will pre-configure the MapMaid instance for you, by serializing the numeric types into Strings and deserializing them without parsing. 

## Serialization

The Recipe is called [BuiltInPrimitveSerializedAsStringSupport](../core/src/main/java/de/quantummaid/mapmaid/builder/recipes/primitives/BuiltInPrimitveSerializedAsStringSupport.java) and it registers a list of primitives as Custom Primitives so if you try to serialize let's say a JSON, containing a numeric type, you'll get it wrapped into a String.

```java
final ObjectMapper objectMapper = new ObjectMapper();
final MapMaid MAP_MAID = MapMaid.aMapMaid()
        .usingRecipe(builtInPrimitveSerializedAsStringSupport())
        .usingJsonMarshallers(objectMapper::writeValueAsString, objectMapper::readValue)
        .build();

final Map<String, Double> object = Map.of("key", 23.8D);
System.out.println(MAP_MAID.serializer().serializeToJson(object));
```

Here, we have configired the aforementioned Recipe alongside the "usual" configuration. Given a Serialized Object (here a Map as an example), the resulting JSON will be:

```json
{"key":"23.8"}
```

## Deserialization

The deserialization is a bit trickier, since one has to configure the Marshaller to _not_ perform parsing and let the engineer handle it.

We have found a way to do that with the [Jackson](https://github.com/FasterXML/jackson) framework and we'll happily receive contributions with more examples of Marshallers that allow this kind of flexibility.

Without using the recipe:

```java
final ObjectMapper objectMapper = new ObjectMapper();
final MapMaid MAP_MAID = MapMaid.aMapMaid()
        .usingJsonMarshallers(objectMapper::writeValueAsString, objectMapper::readValue)
        .build();

final String example = "{\"key\":23.8}";
final Map<String, Object> resultAsMap = MAP_MAID.deserializer()
                                                .deserializeToMap(example, MarshallingType.json());
System.out.println(resultAsMap.get("key").getClass());
```

This will output `class java.lang.Double`, which means the Unmarshaller has "guessed" that 23.8 is a Double. If we want to have the control over the validation, we would like it to be treated as String, hence replacing the objectMapper with the [JacksonMarshaller Recipe](../core/src/main/java/de/quantummaid/mapmaid/builder/recipes/marshallers/jackson/JacksonMarshaller.java)
 
```java
final MapMaid MAP_MAID = MapMaid.aMapMaid()
        .usingRecipe(jacksonMarshallerJson(new ObjectMapper()))
        .build();

final String example = "{\"key\":23.8}";
final Map<String, Object> resultAsMap = MAP_MAID
        .deserializer()
        .deserializeToMap(example, MarshallingType.json());
System.out.println(resultAsMap.get("key").getClass());
```

We'll get `class java.lang.String`. Now we can parse&validate the value in the factory method of a Custom Primitive as we please.

For more examples on both Recipes take a look into [WithPrimitivesBuilderTest](../core/src/test/java/de/quantummaid/mapmaid/builder/lowlevel/withPrimitives/WithPrimitivesBuilderTest.java)
