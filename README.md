BAHCO
=====
Utility methods general computing purposes.  The API covers utility methods for expressing code efficiently through the use of a more compact syntax while still adhering to the constraints of the Java language structs so that it abstracts rather than redefines.

## Overview of classes
Below is an introduction to the main utility classes and which area they cover

### Bahco
General purpose utility methods.  Wrap-, unwrap-, and swallow methods provide a functional control flow for dealing with exceptions.

Collection methods, such as list, set, sortedSet, first, mat, and entry serve the purpose of making compact initialization and population of varous collection types, which brings with it the possibility of inlining the declarations, which greatly reduces the closure scope of the objects, hence also reducing the cognitive complexity of the code.

Methods s or subst are interchangeable and are merely shorthand for String.format.  getResourceAsString does what it says.

Flatten methods transform a potentially hierarchical datastructure to a flat, Properies-like datastructure where the key is computed from the hierarchical path of the entry.

Methods bd and normalize are utility methods for working with BigDecimals, which is the superior way of working with floating point numbers in Java.

### Glob
Globbing is a common and popular way of expressing wildcard pattern matching.  While not available in Java, this class converts an ANT Glob to a regex.

### Json
Utility class for dealing with JSON data.  There are both methods for parsing and serializing JSON.