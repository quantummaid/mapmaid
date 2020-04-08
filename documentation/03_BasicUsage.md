# Basic Usage

Once you have configured a MapMaid instance, you can start serializing and deserializing
objects.
 
## Serializing to JSON
<!---[CodeSnippet](serializeToJson)-->
```java
final String json = mapMaid.serializeToJson(EMAIL);
```

## Deserializing from JSON
<!---[CodeSnippet](deserializeJson)-->
```java
final Email deserializedEmail = mapMaid.deserializeJson(json, Email.class);
```

## Serializing to YAML
<!---[CodeSnippet](serializeToYaml)-->
```java
final String yaml = mapMaid.serializeToYaml(EMAIL);
System.out.println(yaml);
```

## Deserializing from YAML
<!---[CodeSnippet](deserializeYaml)-->
```java
final Email deserializedEmail = mapMaid.deserializeYaml(yaml, Email.class);
```

## Serializing to XML
<!---[CodeSnippet](serializeToXml)-->
```java
final String xml = mapMaid.serializeToXml(EMAIL);
```

## Deserializing from XML
<!---[CodeSnippet](deserializeXml)-->
```java
final Email deserializedEmail = mapMaid.deserializeXml(xml, Email.class);
```

## Serializing to a custom format
<!---[CodeSnippet](serializeToCustomFormat)-->
```java
final String customFormat = mapMaid.serializeTo(EMAIL, MarshallingType.marshallingType("YOUR_CUSTOM_FORMAT"));
```

## Deserializing from a custom format
<!---[CodeSnippet](deserializeCustomFormat)-->
```java
final Email deserializedEmail = mapMaid.deserialize(customFormat, Email.class, MarshallingType.marshallingType("YOUR_CUSTOM_FORMAT"));
```
