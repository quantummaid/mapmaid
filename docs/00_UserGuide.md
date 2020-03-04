# User Guide
This guide walks you through the features of MapMaid, how to configure MapMaid, and how to get the most out of it.


Check out also our [Quick Start](QuickStart.md) if you only want to get started coding or take a look into the 
definition of [Custom Primitives](Concepts.md#custom-primitives) and 
[Serialized Objects](Concepts.md#serialized-objects) if you are wondering what those are.

## Using MapMaid
Welcome to the most simple chapter of this user guide. Using MapMaid is straightforward given that a configured 
MapMaid instance is already at your disposal. The MapMaid instance gives you quick access to the most frequently used 
serialization and deserialization methods. It is thread safe, so we encourage you to use a single instance in your 
application (unless you see a need of separately configured instances). 
 
### Minimal Configuration
MapMaid needs to know the package where your [Custom Primitives](Concepts.md#custom-primitives) and [Serialized Objects](Concepts.md#serialized-objects) reside. MapMaid is also unaware 
of the chosen format, hence needs to be configured with [(Un)marshaller](Concepts.md#unmarshalling) to deal with the format conversion. 

If you are following the [default conventions](UserGuide.md#default-conventions-explained) and have chosen JSON as format along with Gson as marshaller, here is a minimal configuration: 

<!---[CodeSnippet](example1)-->
```java
final Gson gson = new Gson();
final MapMaid mapMaid = MapMaid.aMapMaid()
        .serializingAndDeserializing(Email.class)
        .withAdvancedSettings(advancedBuilder -> advancedBuilder
                .usingMarshaller(MarshallingType.marshallingType("YOUR_CUSTOM_FORMAT"), gson::toJson, gson::fromJson))
        .build();
```
Below you can find detailed information about using this builder and configuring MapMaid. 

### Beyond MapMaid
The MapMaid instance also gives you access to the 
[Serializer](../core/src/main/java/de/quantummaid/mapmaid/serialization/Serializer.java) and the
[Deserializer](../core/src/main/java/de/quantummaid/mapmaid/deserialization/Deserializer.java). These are the powerful,
uncharted and undocumented areas of MapMaid. Only the bravest of the bravest enter that area, and we are still looking
for a hero that will be brave enough to fill the black hole in its centre with documentation. Mysterious and powerful 
features like [Injection Support](../core/src/main/java/de/quantummaid/mapmaid/injector/InjectorLambda.java) or
`Function<Map<String, Object>, Map<String, Object>> serializedPropertyInjector` are rumoured to be found there, but 
beware, with great power comes great responsibility.

## Configuring the MapMaid instance
MapMaid comes with a builder that allows you to configure how the Custom Primitives and Serialized Objects are 
detected, how they are (de)serialized, how they are (un)marshalled, how to handle exceptions. This section addresses 
all the possible configurations and how they impact the (de)serialization of your objects. 

For a code example to start with and minimal configuration check out the [Quick Start](QuickStart.md) 
