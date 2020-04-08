# Registering types
The following chapter explains how to create a `MapMaid` instance and register classes for serialization and deserialization.

## Registering types for autodetection

For MapMaid to be able to serialize and deserialize a given class, you need to register it in the configuration:
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

## Registering custom types
If you want to tell MapMaid exactly how to
serialize and deserialize a given type rather than rely on its autodetection mechanism,
you can do so by registering a custom type:

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

If you only need MapMaid to serialize the custom type, the respective configuration look like this:

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
Vice versa, to only deserialize the custom type, the configuration looks like this:

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

### Custom primitives
MapMaid supports [primitive inlining of classes](06_PrimitiveInlining.md), i.e. mapping a class to a (JSON/XML/etc.) primitive
instead of a (JSON/XML/etc.) object.
You can register a custom type as an inlined primitive like this:

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


## Registering injection-only types

Some types are needed to create certain objects, but should not be serialized or deserialized themselves. Examples for such classes
are database connections and loggers.
To tell MapMaid to never (de-)serialize types, you can register them as injection-only.
In order to always expect objects of a certain type to be provided via per-deserialization injections,
register it like this:   

<!---[CodeSnippet](normalInjection)-->
```java
final MapMaid mapMaid = MapMaid.aMapMaid()
        .injecting(MyInjectedValue.class)
        .build();
```

Alternatively, you can tell MapMaid how to create instances of a certain type on its own like this:

<!---[CodeSnippet](fixedInjection)-->
```java
final MapMaid mapMaid = MapMaid.aMapMaid()
        .injecting(MyInjectedValue.class, () -> new MyInjectedValue("this is injected"))
        .build();
```

## Recipes
To bundle together common configuration options, you can implement the `Recipe` interface: 
<!---[CodeSnippet](recipe)-->
```java
public final class MyRecipe implements Recipe {

    @Override
    public void cook(final MapMaidBuilder mapMaidBuilder) {
        mapMaidBuilder.serializingAndDeserializing(MyCustomClass.class);
    }
}
```

Once created, you can use it to configure a MapMaid instance like this:

<!---[CodeSnippet](recipeConfig)-->
```java
final MapMaid mapMaid = MapMaidBuilder.mapMaidBuilder()
        .usingRecipe(new MyRecipe())
        .build();
```

