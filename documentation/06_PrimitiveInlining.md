# Primitive inlining
Let's look at how Java objects are typically serialized to JSON.
We can take this class as an example:
<!---[CodeSnippet](myJsonModel)-->
```java
public final class MyJsonModel {
    public final String field1;
    public final String field2;
    public final String field3;

    public MyJsonModel(final String field1, final String field2, final String field3) {
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
    }
}
```

If we now serialize `new MyJsonModel("foo", "bar", "xyzzy")` to JSON, we would end up with this
representation:
```json
{
   "field1": "foo",
   "field2": "bar",
   "field3": "xyzzy"
}
```

As you can see, the class is mapped to a JSON object (indicated by the curly braces `{}`).
The JSON object contains each field of the class as a key/value pair.

## Inlining classes to primitive objects
Let's take another example:

<!---[CodeSnippet](myOtherJsonModel)-->
```java
public final class MyOtherJsonModel {
    public final String value;

    public MyOtherJsonModel(final String value) {
        this.value = value;
    }
}
```
When we serialize `new MyOtherJsonModel("foo")` to JSON, we would analogously expect this result:

```json
{
   "value": "foo"
}
```

But instead, MapMaid will return this:
```json
"foo"
```
Because the class `MyOtherJsonModel` consists of only one field of type `String`, MapMaid will inline
the value and treat the entire class as a primitive object.

## Inlining classes to collections
Analogously, if the class only contains one single collection it can be inlined to a JSON (or XML, YAML etc) collection.
Given the following example:

<!---[CodeSnippet](myCollectionJsonModel)-->
```java
public final class MyCollectionJsonModel {
    public final List<String> values;

    public MyCollectionJsonModel(final List<String> values) {
        this.values = values;
    }
}
```
Without inlining, the JSON would look like this:
```json
{
   "values": ["a", "b", "c"]
}
```

With inlining, the JSON can be shortened to this:

```json
["a", "b", "c"]
```

Currently, collection inlining needs to be enabled on a per-type basis. See <!---[Link] ( 02_RegisteringTypes.md "custom collections") -->
[custom collections](02_RegisteringTypes.md).

## Why is this important?
Popular approaches to software development like Domain-Driven Design discourage the direct use of language primitives
such as `int`, `boolean` and the quasi-primitive `String` (see [*Primitive Obsession*](https://blog.ploeh.dk/2011/05/25/DesignSmellPrimitiveObsession/)).
Instead, they advocate the creation of so-called [value types](https://en.wikipedia.org/wiki/Domain-driven_design#Building_blocks) that model the specific
use of a value.
As an example, you would choose an `EmailAddress` object over a `String` to represent an
email address. Accordingly, a `Prize` object would be a better model for the prize of a shop item
than an `int`.

Developers that follow these principles typically run into problems when serializing or deserializing
objects of classes that have been written in that fashion.
Most frameworks that have effectively become industry standards rely on [JavaBeans style objects](http://www.javapractices.com/topic/TopicAction.do?Id=84)
and offer therefore little to no support.
MapMaid is aware of these concepts and treats value types the way they should be treated - as primitives.
