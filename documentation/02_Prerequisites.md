
## Prerequisites
MapMaid is designed to be a servant of your code base. Hence it is very customizable and non-invasive and does not come
with many prerequisites. The ones it comes with are explained in this chapter.

### Dependencies
To use MapMaid, you need to include the core jar as a dependency to your project. The latest version, with
examples of how to include it in the dependency management tool of your choice, can be found in the 
[Maven Repository](https://maven-badges.herokuapp.com/maven-central/de.quantummaid.mapmaid/core).

```xml
<dependency>
    <version><!---[Version]-->1.6.27</version>
</dependency>
```

### Compiler Configuration
To deserialize a Serialized Object, MapMaid needs to know the deserialization method's parameter names so that it can 
map the input field names to the method parameters. This means you need to compile your code with parameter names. That
is achieved by passing the `-parameters` flag to the java compiler. You can find out more about this flag in
[javac official documentation](https://docs.oracle.com/en/java/javase/12/tools/javac.html).

If your project is built with Maven, you must pass the flag to the compiler plugin:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <compilerArgs>
            <arg>-parameters</arg>
        </compilerArgs>
    </configuration>
</plugin>
```

Also, include this flag in your IDE's javac configuration, and make sure to rebuild your project after the 
configuration changes.

* [Javac configuration in IntelliJ](https://www.jetbrains.com/help/idea/java-compiler.html#javac_eclipse)
* [Store information about method parameters in Eclipse](http://help.eclipse.org/2019-03/topic/org.eclipse.jdt.doc.user/reference/preferences/java/ref-preferences-compiler.htm)

### Optionally use Lombok
[Project Lombok](https://projectlombok.org) is giving the lazy coder a little relieve when coding Custom Primitives and
Serialized Objects by generating private constructors as well as `equals`, `hashCode` and `toString` methods. Check out their website 
for detailed instructions on how to include and use it in your project.  