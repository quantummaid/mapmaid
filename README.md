[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.quantummaid.mapmaid/core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.quantummaid.mapmaid/core)
[![CII Best Practices](https://bestpractices.coreinfrastructure.org/projects/2894/badge)](https://bestpractices.coreinfrastructure.org/projects/2894)

<img src="mapmaid_logo.png" align="left"/>

# MapMaid

MapMaid solves the problem of (de)serialization and validation of Custom Primitives and general Serializable Objects 
in a clean and non-invasive way. MapMaid enables you to 

* Keep validation of your domain in your domain - the factory methods
* Register your validation exceptions and report them aggregated upon deserialization
* Stay independent of the final format of your serialization (YAML/JSON/XML/... - you control)

## Rationale

Almost any modern application nowadays is dealing with mapping incoming requests to a subset of Domain Objects and 
outputting the resulting subset of Domain Objects to a specific format (be that json, yaml, xml, ...). 

As a result, we keep writing the "same" code over and over again. We 

* parse the Strings into Domain Objects, 
* we validate those upon initialization, 
* we process the requests and 
* we do the mapping again to output a certain Response.
 
While there are numerous frameworks that help you on some of the stages of this process, we believe there is a need for a simple,
 non-invasive library that would allow you to integrate this process into your application seamlessly and concentrate on 
 your Business logic.

## Highlighted features 
Some features MapMaid offers:

 -  **No Magic** - MapMaid is using your Objects the same way you would use them, which means:
    - MapMaid will not 
        - read nor write private fields
        - write values into final fields 
        - invoke private methods
    - MapMaid will not generate dynamic proxies
    - MapMaid does not favour the use of annotations
        
 - **Support for validating your domain**
    - MapMaid allows you to check for validation exceptions and aggregates them accordingly.
    - You will know precisely which field of which domain was faulty.
    - MapMaid also offers ways of detecting redundant validation exceptions.
 - **Non-intrusive usage and configuration** 
    - MapMaid instance can be configured in a single place and offers detection of Custom Primitives and Serialized 
        Objects without the use of annotations.
    - As mentioned above, MapMaid creates and validates your Objects the way you would; hence you don't need to change
    anything in your domain when using MapMaid. Since automatic configuration always requires conventions, MapMaid allows
    you to follow _your_ conventions, instead of the ones it's coders came up with.
    -  That means your domain stays free of dependencies(direct or conventional) to the (de)serialization and validation 
    frameworks.  
 - **Highly customizable** 
    - MapMaid is highly configurable, allowing configuration for 
        - whitelisting and blacklisting packages and/or classes(or even disable the whole classpath scanning and configure 
        objects one-by-one)
        - the detection mechanism of Custom Primitives and Serialized Objects
        - manual definition of Custom Primitives and Serialized Objects
        - customizing the Validation Errors

## Resources

Check out these resources, and let us know if you don't find the information you are looking for, 
we'll be happy to address that.

* [QuickStart](documentation/QuickStart.md) - Minimal configuration and example usage of MapMaid
* [User Guide](documentation/UserGuide.md) - Detailed description of MapMaid functionality, walkthrough features and configuration possibilities
* [Introduction Blogpost](https://github.com/quantummaid/mapmaid) - Example application, rationale behind MapMaid
* [Concepts](documentation/Concepts.md) - Terminology we use explained. Check this document out to understand what we call [Custom Primitive](documentation/Concepts.md#custom-primitives) and [Serialized Object](documentation/Concepts.md#serialized-objects) and why we choose [String as the representation method](documentation/Concepts.md#string-representation).

Also, take a look into these articles to get an idea of why we created MapMaid in the first place:

* [Domain Driven Security](documentation/articles/DomainDrivenSecurity.md)

## Contributing

[General Contribution Guidelines](https://github.com/quantummaid/mapmaid/.github/blob/master/CONTRIBUTING.md)

MapMaid is quite young, and the best contribution is using it and giving us feedback.
 
Open issues, or drop an email to mapmaid@quantummaid.de, let us know how you use it and which features you would like to see.
