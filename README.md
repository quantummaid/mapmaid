[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.quantummaid.mapmaid/core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.quantummaid.mapmaid/core)
[![Code Size](https://img.shields.io/github/languages/code-size/quantummaid/mapmaid)](https://github.com/quantummaid/httpmaid)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Slack](https://img.shields.io/badge/chat%20on-Slack-brightgreen)](https://join.slack.com/t/quantummaid/shared_invite/zt-cx5qd605-vG10I~WazfgH9WOnXMzl3Q)
[![Gitter](https://img.shields.io/badge/chat%20on-Gitter-brightgreen)](https://gitter.im/quantum-maid-framework/community)
[![Twitter](https://img.shields.io/twitter/follow/quantummaid)](https://twitter.com/quantummaid)


<img src="mapmaid_logo.png" align="left"/>

# MapMaid
MapMaid serializes, deserializes and validates Java objects in a clean and non-invasive way.
It deeply understands and respects the concepts of Domain-Driven Design.

## Rationale

Almost any modern application nowadays is dealing with mapping incoming requests to domain objects and 
outputting resulting domain objects to a specific format (typically Json, XML or YAML). 

As a result, we keep writing the same code over and over again. We
* parse strings into domain objects, 
* we validate these on initialization, 
* we process the requests and 
* we do the mapping again to output a certain response.
 
While there are numerous frameworks that help you on some of the stages of this process, we believe there is a need for a simple,
 non-invasive library that would allow you to integrate this process into your application seamlessly and concentrate on 
 your business logic.

## Highlighted features 

 - **Designed for Domain-Driven Design**:
    - MapMaid automatically understands factory methods
    - MapMaid understands the concept of **Custom Primitives** or **Value Types**
    - MapMaid understands the concept of **Serialized Objects**

 -  **No Magic** - MapMaid is using your objects the same way as a programmer:
    - MapMaid will not read nor write private fields
    - MapMaid will not write values into final fields 
    - MapMaid will not invoke private methods
    - MapMaid will not generate dynamic proxies
    - MapMaid will not generate bytecode
    - MapMaid does not favour the use of annotations
 
 - **Domain-Driven Validation**:
    - MapMaid allows you to check for validation exceptions and aggregates them accordingly
    - you will know precisely which field of which object was faulty
    - MapMaid also offers ways to detect redundant validation exceptions
    
 - **Non-intrusive usage and configuration:**
    - configuration in one place
    - no annotations 
    - simple adaptation to your specific style of defining objects
    - no dependencies to serialization and validation frameworks in your domain code
    
  - **Flexible:**
    - support for all common data formats (Json, XML and YAML)
    - (de-)serialize any object of any class - no matter how weird their structure    

## Getting started
MapMaid is part of the QuantumMaid framework. You can find easy-to-follow and
interesting tutorials [here](https://github.com/quantummaid/quantummaid-tutorials/blob/master/README.md).

## Get in touch
Feel free to join us on [Slack](https://join.slack.com/t/quantummaid/shared_invite/zt-cx5qd605-vG10I~WazfgH9WOnXMzl3Q)
or [Gitter](https://gitter.im/quantum-maid-framework/community) to ask questions, give feedback or just discuss software
architecture with the team behind MapMaid. Also, don't forget to visit our [website](https://quantummaid.de) and follow
us on [Twitter](https://twitter.com/quantummaid)!