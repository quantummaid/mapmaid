# User Guide
This guide walks you through the features of MapMaid, how to configure MapMaid, and how to get the most out of it.

Please skip to [Quick Start](02_QuickStart.md) if you want to jump right into coding.

## Motivation

The most generic form of data representation that is both human-friendly and serialization-friendly is arguably the string.
You use strings to transport information from one service to another, from server to client, from server to database, 
from client back to server or even from screen to paper or paper to screen. 

Keeping all data as a string comes with a drawback.
Since a string can contain pretty much anything, you don't have any validation on the actual content.
You end upo repeatedly verifying (i.e. doubting) whether the string entering your application is valid or not,
whether it is really an email address or not, whether it can be converted to an integer, etc.

To circumvent 

So you keep your validation and construction logic _inside_ the object and provide methods for converting it back to the transportation mechanism (i.e. string).


This approach also means MapMaid expects that the Unmarshaller does not attempt to guess and parse number literals into specific types. The parser's guesstimate might or might not be correct and even features targeting that problem (e.g. [USE_BIG_DECIMAL_FOR_FLOATS, USE_BIG_INTEGER_FOR_INTS](https://github.com/FasterXML/jackson-databind/wiki/Deserialization-Features)) don't cover all possible scenarios.
Having this in mind, we propose centralizing the parsing and validation of the Custom Primitive, at the same time giving the control to you. Validate once, use everywhere.

