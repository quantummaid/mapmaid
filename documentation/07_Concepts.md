# Concepts

MapMaid is based around two concepts: **Custom Primitives** and **Serialized Objects**.
You might already be familiar with them under a different name or in a different context.
Some call it [Value Object](http://wiki.c2.com/?ValueObject) or 
[Value Java Objects (VALJO)](https://blog.joda.org/2014/03/valjos-value-java-objects.html),
some call it [Data Value](https://refactoring.guru/replace-data-value-with-object) or [Custom Value Type](https://en.wikipedia.org/wiki/Value_object#Java).
You may have also heard about [Project Valhalla](https://en.wikipedia.org/wiki/Project_Valhalla_(Java_language) and the Value Types that will come with it.
The definition also differs in terms of whether or not to combine multiple primitive values into one object or create classes for each such field. 
Independent of how they are called, and even how the details of definition look like, all of them are there to fight [Primitive Obsession](https://blog.ploeh.dk/2011/05/25/DesignSmellPrimitiveObsession/).

The concept exists for a long time now. Yet a lot of frameworks that have become "industry standards" rely on [JavaBeans style objects]((http://www.javapractices.com/topic/TopicAction.do?Id=84)) and offer little to none support for this concept.

We think that creating your own classes and
[keeping the validation logic inside the class](https://enterprisecraftsmanship.com/2017/08/07/always-valid-vs-not-always-valid-domain-model/)
makes the code cleaner, more readable, [less dependant on the framework](https://blog.cleancoder.com/uncle-bob/2014/05/11/FrameworkBound.html) being used and most of
all [safer](10_DomainDrivenSecurity.md). 

On this page, we'd like to define what we call **Custom Primitives** and **Serialized Objects**, and which conventions we use to declare them. 

## Custom Primitives

**Custom Primitives** are - in a way - extending Java with your domain's language.
Ideally, they represent a single immutable value and validate this value upon creation so that they are always in a valid state.
Examples of custom primitive are an email address, a zip code, a first name, an amount, a price, a currency, an ID, etc. 

List of properties that describe a Custom Primitive:

* represents one logical property
* immutable, which implies
    * final modifier on the class
    * the field being private and final
    * no setters
* `equals` and `hashCode` implemented 
* only private constructors to enforce the use of factory methods that perform validation 
* factory method or constructor that validates and creates the object from a `String`
* a method that returns the String representation of the value for serialization

## Serialized Objects

A **Serialized Object** is anything in your domain that you aim to serialize at some point.
Anything you need to transport over the wire, save in the database, print to the console, etc. 

They are aggregates of Custom Primitives or other Serialized Objects and are essentially a [composite data type](https://en.wikipedia.org/wiki/Composite_data_type). 
You can also think of it as a "bag of custom primitives and serialized objects", a combination of those that aims to represent data. 

Examples of serialized objects are data transfer objects (DTOs): request/response objects, an order, an email, a receipt or just a
name consisting of a first name and a last name, etc.

List of properties that describe a Serialized Object:

* no primitive fields, all fields should be Custom Primitives, or other Serialized Objects
* all the fields that participate in the serialization are public
* immutable
* a factory method or constructor that takes all the public fields, validates their values in relation to each and constructs the instance
* `equals` and `hashCode` implemented 


Here are [some thoughts in this direction by other people](http://blog.cleancoder.com/uncle-bob/2019/06/16/ObjectsAndDataStructures.html).

