# Exception Handling

## Validation Errors

Typically, when used in a web(service) framework context, MapMaid is acting as a request/response (de)serialization framework. The validation of the request is then expected to return a clear message to the caller about the occurred validation errors. In case of multiple validation errors, communicating those one-by-one, especially in case of a UI, does not make sense to us. Hence, we have implemented a built-in aggregation of validation errors. These are based on the validation exception class provided by you during the configuration of MapMaid instance. The [ValidationError](../core/src/main/java/de/quantummaid/mapmaid/deserialization/validation/ValidationError.java) is then constructed, whenever the instance of that exception is thrown, and the message returned contains the dot-notation "path" to the invalid field.


During the deserialization of objects using MapMaid, exceptions might be thrown by the factory methods or
constructors used to instantiate these objects.
Whenever an exception is thrown, MapMaid will by default immediately rethrow that exception wrapped in an instance of 
[UnrecognizedExceptionOccurredException](../core/src/main/java/de/quantummaid/mapmaid/deserialization/validation/UnrecognizedExceptionOccurredException.java).

In order to catch all [validation errors](07_Concepts.md#validation-errors), MapMaid supports the aggregation of exceptions.
If used, MapMaid will continue deserialize even though it has caught an exception. In the end, it will throw
an instance of [AggregatedValidationException](../core/src/main/java/de/quantummaid/mapmaid/deserialization/validation/AggregatedValidationException.java)
with a detailed report of all encountered exception and where exactly in the object hierarchy they were thrown. 

MapMaid will only aggregate exceptions that are explicitly registered as validation errors.
For example:

<!---[CodeSnippet](aggregateException)-->
```java
final MapMaid mapMaid = MapMaid.aMapMaid()
        .serializingAndDeserializing(Email.class)
        .withExceptionIndicatingValidationError(CustomTypeValidationException.class)
        .build();
```

Given the custom primitive

**EmailAddress.class**
```java
public final class EmailAddress {
    private final String value;

    public static EmailAddress fromStringValue(final String value) {
        if(isValidEmailAddress(value)) {
            return new EmailAddress(value);
        } else {
            throw new CustomTypeValidationException(String.format("Invalid email address %s", value));
        }
    }
    ...
}
```

and the serialized object

**Email.class**

```java
public final class Email {
    public final EmailAddress sender;
    public final EmailAddress receiver;
    //...
}
```

MapMaid now tries to deserializes an `Email` object from an invalid input like the following:  
```json
{
  "sender": "not-a-valid-sender-value",
  "receiver": "not-a-valid-receiver-value"
}
```

Instead of returning an instance of type `Email`, MapMaid will throw this exception:  
```bash
de.quantummaid.mapmaid.deserialization.validation.AggregatedValidationException: deserialization encountered validation errors. Validation error at 'receiver', Invalid email address: 'not-a-valid-receiver-value'; Validation error at 'sender', Invalid email address: 'not-a-valid-sender-value';
```

You can further customize the message of this error by providing a lambda that maps your validation exception to an 
instance of
[ValidationError](../core/src/main/java/de/quantummaid/mapmaid/deserialization/validation/ValidationError.java):

<!---[CodeSnippet](mappedException)-->
```java
final MapMaid mapMaid = MapMaid.aMapMaid()
        .serializingAndDeserializing(Email.class)
        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(GSON::toJson, input -> GSON.fromJson(input, Object.class)))
        .withExceptionIndicatingValidationError(CustomTypeValidationException.class,
                (exception, propertyPath) -> new ValidationError("This is a custom message we are reporting about " + exception.getMessage(), propertyPath))
        .build();
```

With the same invalid input as before, you will receive:

```bash
de.quantummaid.mapmaid.deserialization.validation.AggregatedValidationException: deserialization encountered validation errors. Validation error at 'receiver', This is a custom message we are reporting about Invalid email address: 'not-a-valid-receiver-value'; Validation error at 'sender', This is a custom message we are reporting about Invalid email address: 'not-a-valid-sender-value';
```

Webservice frameworks usually offer a way to register global exception handlers that map an exception to an HTTP response.
This is the place where you can register a mapper that generates a response from the caught instance of
[AggregatedValidationException](../core/src/main/java/de/quantummaid/mapmaid/deserialization/validation/AggregatedValidationException.java).

