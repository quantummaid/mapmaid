## Recipes
In the real world, a good recipe provides instructions and ingredients on how to cook stuff, pancakes, for instance.
If you've had some friends over for a pancake party, and you've made good pancakes, chances are, that you are asked
for a recipe. What happens if you share it is essentially making a copy of the recipe. Some IT books take the same approach; most of them can be identified by `book.name.contains("cookbook")`. The problem with that approach
becomes apparent when you update the ingredients or baking temperatures or both - you need to ship a new book and your
clients have to copy&paste the updated recipe again.
 
To avoid that problem for MapMaid, we ship with the `Recipe` interface, which allows everyone to craft tasty
MapMaid recipes and share them as code. This way, you can change the recipe and roll out the change using your favourite
distribution management - e.g. a versioned maven artifact. 
Recipes allow MapMaid to offer support for many different use cases, without polluting the builder interface as well.

In other words, Recipes are little MapMaid plugins for some common usecases such as (de)serialization of 
numeric data types or the configuration of Jackson and it's registration as Marshaller in MapMaid. They are also an excellent 
opportunity to contribute to MapMaid or to implement conventions across multiple projects of multiple teams.

### Using Recipes
Using a recipe is very simple and straight forward. Just call the `usingRecipe` builder method with an instance of your recipe. Let's
showcase that by listing and explaining the recipes MapMaid is shipping with.

#### Support for language primitives (double, int, float, String, ...)
Although we put much effort into stating that we discourage the use of primitives shipped with the language, we 
understand that sometimes things are different and for these times, MapMaid ships with a recipe that makes it map
your Serialized Objects even if they contain built-in primitives.

(That is not because we think there are times when Custom Primitives are not the preferred solution, but because we 
believe that a framework should be a slave to your code and not the other way around.) 

Using the [BuiltInPrimitiveSerializedAsStringSupport](../core/src/main/java/de/quantummaid/mapmaid/builder/recipes/primitives/BuiltInPrimitiveSerializedAsStringSupport.java) 
Recipe is straight forward:
```java
MapMaid.aMapMaid()
    //...
    .usingRecipe(builtInPrimitiveSerializedAsStringSupport())
    //...
    .build();
```

Check out [WithPrimitivesBuilderTest](../core/src/test/java/de/quantummaid/mapmaid/builder/lowlevel/withPrimitives/WithPrimitivesBuilderTest.java)
for a detailed example. 

#### Support for manually registered types
Scanning the classpath and analysing which classes are 
Custom Primitives, which are Serialized Objects and which are to ignore is a great way to trade development effort with
CPU effort. However, if you intend to run on a serverless platform like AWS Lambda, chances are high, that your project 
only contains a handful of Custom Primitives/Serialized Objects and you want a high-speed, optimized application startup.

Another reason to manually define which Objects are allowed to enter and/or leave your service is security. 

Yet another reason would be to make MapMaid work with a specifically unconventional Custom Primitive or 
Serialized Object.

In these cases or if you are just a control freak, the 
[ManualRegistry](../core/src/main/java/de/quantummaid/mapmaid/builder/recipes/manualregistry/ManualRegistry.java) 
is your recipe of choice.

Control/Security freaks and lazy fancy Lambdas will find it's usage straight forward:
```java
MapMaid.aMapMaid()
    .usingRecipe(manuallyRegisteredTypes()
            .withSerializedObjects(
                    de.quantummaid.mapmaid.builder.models.conventional.Email.class
            )
            .withCustomPrimitives(
                    de.quantummaid.mapmaid.builder.models.conventional.EmailAddress.class,
                    de.quantummaid.mapmaid.builder.models.conventional.Subject.class,
                    de.quantummaid.mapmaid.builder.models.conventional.Body.class)
    )
    //...
    .build();
```

Teaching MapMaid how to work with unconventional types is a bit more complex and requires a basic understanding
of how MapMaid is interacting with your types.

MapMaid is behaving like you'd behave when interacting with Custom Primitive: it's calling one of its methods to obtain
a String or passes a string into a factory method when creating a Custom Primitive. Both of the actions can be broken 
down to 3 simple pieces of information: the type, a method that converts the type into a string and a method that 
converts the string into the type. Given that information, manually registering an unconventional Custom Primitive becomes
straight forward:

```java
MapMaid.aMapMaid()
                .usingRecipe(manuallyRegisteredTypes()
                        .withCustomPrimitive(EmailAddress.class, EmailAddress::serialize, EmailAddress::deserialize)
                        .withCustomPrimitive(Subject.class, Subject::serialize, Subject::deserialize)
                        .withCustomPrimitive(customConventionBody, Body::serialize, Body::deserialize)
                )
                //...
                .build();
```

Instead of passing the type and 2 functions as parameters, you can also provide and instance of
[CustomPrimitiveDefinition](../core/src/main/java/de/quantummaid/mapmaid/builder/definitions/CustomPrimitiveDefinition.java).

Serialized Objects are a bit more complicated to deal with, and we admit that there is still some simplicity to gain by 
enhancing the code. Since that is going to be a bit of effort, we decided to wait until we get more feedback and 
use cases from our users so that we can put in the effort where it brings the most benefit. With that in mind,
let's dive into the Lion's Den.

The "easy" way to help MapMaid understand how to deal with an unconventional Serialized Object is to provide the type,
a list of fields that are supposed to be serialized and a string representing the factory methods name:
```java
MapMaid.aMapMaid()
                .usingRecipe(manuallyRegisteredTypes()
                        .withSerializedObject(Email.class, Email.class.getFields(), "restore")
                )
                //...
                .build();
```

See [IndividuallyAddedModelsBuilderTest](../core/src/test/java/de/quantummaid/mapmaid/builder/IndividuallyAddedModelsBuilderTest.java) 
for more details.

If you want to dig even deeper read on. MapMaid is using the 
[DeserializationDTOMethod](../core/src/main/java/de/quantummaid/mapmaid/deserialization/methods/DeserializationDTOMethod.java)
interface to deserialize Serialized Objects:

```java
    Object deserialize(Class<?> targetType, Map<String, Object> elements) throws Exception;

    Map<String, Class<?>> elements(Class<?> targetType);
```

The method `elements` has to provide a Map representing the factory method's parameter list for a given type.
Examining the [Email](../core/src/test/java/de/quantummaid/mapmaid/builder/models/conventional/Email.java),
as an example for a Serialized Object, will provide some help understanding what that sentence means.
To create an instance of Email, MapMaid needs the `elements`:

```json
{
  "sender": "EmailAddress.class",
  "receiver": "EmailAddress.class",
  "subject": "Subject.class",
  "body": "Body.class"
}
```

Once MapMaid obtained all of the elements, it calls `deserialize` with the target type and a Map similar to 
the one provided by `elements`, but with actual values instead of types.

For serialization, MapMaid is using the 
[SerializationDTOMethod](../core/src/main/java/de/quantummaid/mapmaid/serialization/methods/SerializationDTOMethod.java)
interface. At this point, it carries too many internals, it is hard to understand and even harder to explain. If
you want/need to provide your own implementation, check out 
[SerializedObjectDefinition](../core/src/main/java/de/quantummaid/mapmaid/builder/definitions/SerializedObjectDefinition.java)
and read your way into the code.


### Crafting your own Recipes
To create a recipe, one has to understand the 
[Recipe interface](../core/src/main/java/de/quantummaid/mapmaid/builder/recipes/Recipe.java) and the process of how a 
MapMaid instance is built by the MapMaidBuilder.



### The Builder Process
One of the goals of MapMaid is to be ultra customizable. Another one is ultra short and straightforward conventions to reduce 
configuration effort. To satisfy both of the goals, MapMaid is built using a builder that implements the 
following process:

1. Allow all recipes to interact with the Builder itself by calling the `cook` method with the builder instance.
2. Allow all recipes to provide Custom Primitive and Serialized Object definitions.
3. Ask the `PackageScanner` to list all detection candidates a.k.a. the classes that might be a Custom Primitive, 
Serialized Object, or something else, MapMaid is not interested in.
4. Remove all known Custom Primitives and Serialized Objects (obtained in Step 2) from that list.
5. Use the `Detector` to obtain Custom Primitive and Serialized Object definitions from detection candidates.
6. Wrap up everything else and build the MapMaid instance.

Check out the code of [MapMaidBuilder](../core/src/main/java/de/quantummaid/mapmaid/builder/MapMaidBuilder.java) for more
little nifty details.
 
### Understanding the Recipe Interface 

```java
public interface Recipe {
    default void cook(final MapMaidBuilder mapMaidBuilder) {
    }

    default Map<Class<?>, CustomPrimitiveDefinition> customPrimitiveDefinitions() {
        return Map.of();
    }

    default Map<Class<?>, SerializedObjectDefinition> serializedObjectDefinitions() {
        return Map.of();
    }
}
```

The first thing to note is that the interface only contains methods, which implementation made is optional by
providing a NOOP default implementation.

The `cook` method is quite simple; it's essentially a call back with the MapMaidBuilder instance that can be used to wrap
multiple builder calls into a single Recipe. The
[JacksonMarshaller](../core/src/main/java/de/quantummaid/mapmaid/builder/recipes/marshallers/jackson/JacksonMarshaller.java)
is a great example of Recipes implementing only that method.

`customPrimitiveDefinitions` and `serializedObjectDefinitions` allows Recipes to provide Custom Primitive and 
Serialized Object Definitions. The 
[ManualRegistry](../core/src/main/java/de/quantummaid/mapmaid/builder/recipes/manualregistry/ManualRegistry.java)
is the example providing with more insights as to how these methods can be used.  

We'll be happy to receive your requests for new Recipes and contributions!