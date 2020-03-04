# Hints

Sometimes, MapMaid is not able to detect how to (de-)serialize a registered class.
Whenever this happens, MapMaid will throw an exception **during initialization**
(as opposed to during the actual (de-)serialialization). **An initialized MapMaid
instance will always be able to (de-)serialize all registered types.**

However, sometimes MapMaid complains during initialization about classes it is unsure how
to (de-)serialize. Or you are just unhappy with the conventions MapMaid uses.
In either case, it is possible to tweak MapMaids behaviour using so-called hints.
These hints are explained in the following paragraphs.

## Per-class hints
### Custom primitives

### Serialized objects




## Global hints
Sometimes, you have projects with conventions that are a bit different than MapMaid
assumptions.

### Preferring specific factories

### Ignoring methods

### Ignoring constructors

### Ignoring fields

### Adding fields
