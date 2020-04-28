[![CII Best Practices](https://bestpractices.coreinfrastructure.org/projects/3873/badge)](https://bestpractices.coreinfrastructure.org/projects/3873)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=de.quantummaid.mapmaid%3Amapmaid-parent&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=de.quantummaid.mapmaid%3Amapmaid-parent)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=de.quantummaid.mapmaid%3Amapmaid-parent&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=de.quantummaid.mapmaid%3Amapmaid-parent)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=de.quantummaid.mapmaid%3Amapmaid-parent&metric=security_rating)](https://sonarcloud.io/dashboard?id=de.quantummaid.mapmaid%3Amapmaid-parent)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=de.quantummaid.mapmaid%3Amapmaid-parent&metric=alert_status)](https://sonarcloud.io/dashboard?id=de.quantummaid.mapmaid%3Amapmaid-parent)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=de.quantummaid.mapmaid%3Amapmaid-parent&metric=bugs)](https://sonarcloud.io/dashboard?id=de.quantummaid.mapmaid%3Amapmaid-parent)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=de.quantummaid.mapmaid%3Amapmaid-parent&metric=code_smells)](https://sonarcloud.io/dashboard?id=de.quantummaid.mapmaid%3Amapmaid-parent)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=de.quantummaid.mapmaid%3Amapmaid-parent&metric=sqale_index)](https://sonarcloud.io/dashboard?id=de.quantummaid.mapmaid%3Amapmaid-parent)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=de.quantummaid.mapmaid%3Amapmaid-parent&metric=coverage)](https://sonarcloud.io/dashboard?id=de.quantummaid.mapmaid%3Amapmaid-parent)
[![Last Commit](https://img.shields.io/github/last-commit/quantummaid/mapmaid)](https://github.com/quantummaid/mapmaid)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.quantummaid.mapmaid/core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.quantummaid.mapmaid/core)
[![Code Size](https://img.shields.io/github/languages/code-size/quantummaid/mapmaid)](https://github.com/quantummaid/mapmaid)
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
outputting resulting domain objects to a specific format (typically JSON, XML or YAML). 

As a result, we keep writing the same code over and over again:
* Parse strings into domain objects
* Validate the objects on initialization 
* Process the requests 
* Serialize the response objects
 
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
    - You will know precisely which field of which object was faulty
    - MapMaid also offers ways to detect redundant validation exceptions
    
 - **Non-intrusive usage and configuration:**
    - Configuration in one place
    - No annotations 
    - Simple adaptation to your specific style of defining objects
    - No dependencies to serialization and validation frameworks in your domain code
    
  - **Flexible:**
    - Support for all common data formats (JSON, XML and YAML)
    - Serialize and deserialize any object of any class - no matter how weird their structure    

## Getting started
MapMaid is part of the QuantumMaid framework which provides [easy-to-follow and
interesting tutorials](https://github.com/quantummaid/quantummaid-tutorials/blob/master/README.md).

The MapMaid documentation can be found [here](https://quantummaid.de/docs.html).

## Get in touch
Feel free to join us on [Slack](https://join.slack.com/t/quantummaid/shared_invite/zt-cx5qd605-vG10I~WazfgH9WOnXMzl3Q)
or [Gitter](https://gitter.im/quantum-maid-framework/community) to ask questions, give feedback or just discuss software
architecture with the team behind MapMaid. Also, don't forget to visit our [website](https://quantummaid.de) and follow
us on [Twitter](https://twitter.com/quantummaid)!