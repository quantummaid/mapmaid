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
