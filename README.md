# clj-struct

A simple Clojure library that packs/unpacks sequences of values to/from byte buffers.
The library was inspired by Python [struct] [1] module.

## Usage

Include the library in your leiningen project dependencies:

[clj-struct "0.1.0-SNAPSHOT"]

## Supported Format Characters

| Format |        C Type      | Clojure type | Size |
| ------ | ------------------ | ------------ | ---- |
| c      | char               | char         |  1   |
| b	     | signed char        | byte         |  1   |
| B      | unsigned char      | int          |  1   |
| ?      | _Bool              | boolean      |  1   |
| h      | short              | short        |  2   |
| H      | unsigned short     | int          |  2   |
| i      | int                | int          |  4   |
| I      | unsigned int       | long         |  4   |
| l      | long               | int          |  4   |
| L      | unsigned long      | long         |  4   |
| q      | long long          | long         |  8   |
| Q      | unsigned long long | BigInt       |  8   |
| f      | float              | float        |  4   |
| d      | double             | double       |  8   |
| s      | char[]             | string       |      |

## Examples

```clojure
(use 'clj-struct.core)

; calculates the size of the struct corresponding to the given format
(calc-size "11sii?")

; returns a byte buffer containing the values packed according to the given format
(def bytes-buf (pack "11sii?" ["some string" -28 499 false]))

; returns a sequence of unpacked values
(den unpacked-seq (unpack "11sii?" bytes-buf))
```

## License

Copyright © 2013 Pavel Prokopenko

Distributed under the Eclipse Public License, the same as Clojure.

[1]: http://docs.python.org/2/library/struct.html "struct - Interpret strings as packed binary data"