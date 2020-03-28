# Configuring a MapMaid instance
Before you can use MapMaid to serialize and/or deserialize objects, you need to create a `MapMaid` instance.
This chapter explains all possible ways to configure that instance. 

## Registering types

In order for MapMaid to be able to (de-)serialize a given class, you need to register it in the configuration:
<!---[CodeSnippet](duplexConfig)-->
```java
final MapMaid mapMaid = MapMaid.aMapMaid()
        .serializingAndDeserializing(MyCustomClass.class)
        .build();
```

If you need MapMaid to only serialize a given class, but not deserialize it, you can alternatively
register it like this:

<!---[CodeSnippet](serializationConfig)-->
```java
final MapMaid mapMaid = MapMaid.aMapMaid()
        .serializing(MyCustomClass.class)
        .build();
```

Vice versa, if you only need deserialization, register the class like this:

<!---[CodeSnippet](deserializationConfig)-->
```java
final MapMaid mapMaid = MapMaid.aMapMaid()
        .deserializing(MyCustomClass.class)
        .build();
```

**Note:** `String`, language primitives (`int`, `boolean`, etc.), standard collections (`List`, `Set`, `ArrayList`, etc.) and
arrays are supported out of the box and do not need to be registered.

## Registering custom types
If you want to tell MapMaid exactly how to
(de-)serialize a given type rather than rely on its autodetection mechanism,
you can do so by registering a custom type. You can register it as a [Custom Primitive](Concepts.md) like this:

<!---[CodeSnippet](duplexCustomCustomPrimitiveConfig)-->
```java
final MapMaid mapMaid = MapMaid.aMapMaid()
        .serializingAndDeserializing(
                DuplexType.customPrimitive(
                        MyCustomPrimitive.class,
                        MyCustomPrimitive::value,
                        MyCustomPrimitive::new
                )
        )
        .build();
```

To register a custom type as a [Serialized Object](Concepts.md), the configuration would look
like this:

<!---[CodeSnippet](duplexCustomSerializedObjectConfig)-->
```java
final MapMaid mapMaid = MapMaid.aMapMaid()
        .serializingAndDeserializing(
                DuplexType.serializedObject(MySerializedObject.class)
                        .withField("field1", String.class, MySerializedObject::getField1)
                        .withField("field2", String.class, MySerializedObject::getField2)
                        .withField("field3", String.class, MySerializedObject::getField3)
                        .deserializedUsing(MySerializedObject::new)
        )
        .build();
```

If you only need MapMaid to serialize the custom type, the respective configurations look like this:

<!---[CodeSnippet](serializationCustomCustomPrimitiveConfig)-->
```java
final MapMaid mapMaid = MapMaid.aMapMaid()
        .serializing(
                SerializationOnlyType.customPrimitive(
                        MyCustomPrimitive.class,
                        MyCustomPrimitive::value
                )
        )
        .build();
```

<!---[CodeSnippet](serializationCustomSerializedObjectConfig)-->
```java
final MapMaid mapMaid = MapMaid.aMapMaid()
        .serializing(
                SerializationOnlyType.serializedObject(MySerializedObject.class)
                        .withField("field1", String.class, MySerializedObject::getField1)
                        .withField("field2", String.class, MySerializedObject::getField2)
                        .withField("field3", String.class, MySerializedObject::getField3)
        )
        .build();
```
Vice versa, to only deserialize the custom type, the configurations look like this:

<!---[CodeSnippet](deserializationCustomCustomPrimitiveConfig)-->
```java
final MapMaid mapMaid = MapMaid.aMapMaid()
        .deserializing(
                DeserializationOnlyType.customPrimitive(
                        MyCustomPrimitive.class,
                        MyCustomPrimitive::new
                )
        )
        .build();
```

<!---[CodeSnippet](deserializationCustomSerializedObjectConfig)-->
```java
final MapMaid mapMaid = MapMaid.aMapMaid()
        .deserializing(
                DeserializationOnlyType.serializedObject(MySerializedObject.class)
                        .withField("field1", String.class)
                        .withField("field2", String.class)
                        .withField("field3", String.class)
                        .deserializedUsing(MySerializedObject::new)
        )
        .build();
```

## Registering injection-only types

## Exception handling

## Changing detection preferences
Whenever MapMaid does not 

<!---[CodeSnippet](preferredCustomPrimitiveFactoryName)-->
```java
MapMaid.aMapMaid()
        .withAdvancedSettings(advancedBuilder -> {
            advancedBuilder.withPreferredCustomPrimitiveFactoryName("instantiate");
        })
        .build();
```

<!---[CodeSnippet](preferredCustomPrimitiveSerializationMethodName)-->
```java
MapMaid.aMapMaid()
        .withAdvancedSettings(advancedBuilder -> {
            advancedBuilder.withPreferredCustomPrimitiveSerializationMethodName("serializeToString");
        })
        .build();
```

<!---[CodeSnippet](preferredSerializedObjectFactoryName)-->
```java
MapMaid.aMapMaid()
        .withAdvancedSettings(advancedBuilder -> {
            advancedBuilder.withPreferredSerializedObjectFactoryName("instantiate");
        })
        .build();
```

## Marshalling

## Recipes