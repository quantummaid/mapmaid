# Autodetection of types
This chapter explains on a very high level how MapMaid determines
how a class is to be serialized and deserialized. 

## Ignored features
When analyzing a class, there are some features (methods, fields or constructors) that
MapMaid will not consider to use for serialization and deserialization:
- Fields, methods or constructors that are not declared `public`
- The inherited or overwritten methods `toString()` and `hashCode`
- Fields declared `transient`

## Primitive inlining
MapMaid supports <!---[Link] ( 06_PrimitiveInlining.md "primitive inlining of classes") -->
[primitive inlining of classes](06_PrimitiveInlining.md), i.e. mapping a class to a (JSON/XML/etc.) primitive
instead of a (JSON/XML/etc.) object.
A class is primitive inlined if at least one of the following holds true:
- It has exactly one field of one of the following types:
    - `String`
    - `int` or `Integer`
    - `long` or `Long`
    - `float` or `Float`
    - `double` or `Double`
    - `boolean` or `Boolean`
- It features a preferred factory method for primitive objects
- It features a preferred serialization method for primitive objects
A class is not primitive inlined if at least one of the following holds true:
- It features a preferred factory method for composite objects

If none of the rules hold true, the class will be not inlined.
If the decision is ambiguous, MapMaid will throw a detailed exception with information on how to resolve the conflict.

### Preferred factory method for composite objects
If a class has a `public` and `static` factory method that is named `deserialize`, MapMaid will always
use this method to deserialize the class.
The presence of this method will ensure that the class is not <!---[Link] ( 06_PrimitiveInlining.md "primitive inlined") -->
[primitive inlined](06_PrimitiveInlining.md).
You can change the preferred name like this:

<!---[CodeSnippet](preferredSerializedObjectFactoryName)-->
```java
final MapMaid mapMaid = MapMaid.aMapMaid()
        .withAdvancedSettings(advancedBuilder -> {
            advancedBuilder.withPreferredSerializedObjectFactoryName("instantiate");
        })
        .build();
```


### Preferred factory method for primitive objects
If a class has a `public` and `static` factory method that is named `fromStringValue`, MapMaid
will always use this method to deserialize the class. The presence of this method will ensure
that the class is <!---[Link] ( 06_PrimitiveInlining.md "primitive inlined") -->
[primitive inlined](06_PrimitiveInlining.md).
You can change the preferred name like this:

<!---[CodeSnippet](preferredCustomPrimitiveFactoryName)-->
```java
final MapMaid mapMaid = MapMaid.aMapMaid()
        .withAdvancedSettings(advancedBuilder -> {
            advancedBuilder.withPreferredCustomPrimitiveFactoryName("instantiate");
        })
        .build();
```


### Preferred serialization method for primitive objects
If a class has a `public` method that is named `toStringValue`, MapMaid
will always use this method to serialize the class.
The presence of this method will ensure
that the class is <!---[Link] ( 06_PrimitiveInlining.md "primitive inlined") -->
[primitive inlined](06_PrimitiveInlining.md).
You can change the preferred name like this:

<!---[CodeSnippet](preferredCustomPrimitiveSerializationMethodName)-->
```java
final MapMaid mapMaid = MapMaid.aMapMaid()
        .withAdvancedSettings(advancedBuilder -> {
            advancedBuilder.withPreferredCustomPrimitiveSerializationMethodName("serializeToString");
        })
        .build();
```


### Factory method named after the class
If a class has a `public` and `static` factory method that has the same name as the class (case insensitive),
MapMaid will prefer this factory method over most other factory methods for deserialization.

### Constructors
If a class does not have any factory methods, MapMaid will fall back to use any `public` constructors for deserialization. 


### Getter methods
MapMaid will treat getter methods as fields. For example:
<!---[CodeSnippet](gettersExample)-->
```java
public final class GettersExample {
    public final String value1 = "value1";
    public final String value2 = "value2";
    public final String value3 = "value3";

    public String getValue1() {
        return this.value1;
    }

    public String getValue2() {
        return this.value2;
    }

    public String getValue3() {
        return this.value3;
    }
}
```
Serializing a new instance of the `GettersExample` class to JSON will result in:
```json
{
   "value1": "value1",
   "value2": "value2",
   "value3": "value3"
}
```

It is possible to mix getters and public fields:

<!---[CodeSnippet](mixedGettersAndPublicFieldsExample)-->
```java
public final class MixedGettersAndPublicFieldsExample {
    public final String value1 = "value1 from public field";

    public String getValue2() {
        return "value2 from getter method";
    }

    public String getValue3() {
        return "value3 from getter method";
    }
}
```
Serializing a new instance of the `MixedGettersAndPublicFieldsExample` class to JSON will result in:
```json
{
   "value1": "value1 from public field",
   "value2": "value2 from getter method",
   "value3": "value3 from getter method"
}
```

When a class features a public field and a getter method under the same name,
MapMaid will use the public field. Example:

<!---[CodeSnippet](gettersAndPublicFieldsExample)-->
```java
public final class GettersAndPublicFieldsExample {
    public final String value1 = "value1 from public field";
    public final String value2 = "value2 from public field";
    public final String value3 = "value3 from public field";

    public String getValue1() {
        return "value1 from getter method";
    }

    public String getValue2() {
        return "value2 from getter method";
    }

    public String getValue3() {
        return "value3 from getter method";
    }
}
```
Serializing a new instance of the `GettersAndPublicFieldsExample` class to JSON will result in:
```json
{
   "value1": "value1 from public field",
   "value2": "value2 from public field",
   "value3": "value3 from public field"
}
```

