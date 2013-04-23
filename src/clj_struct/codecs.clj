;   Copyright (c) Pavel Prokopenko. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php).
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns clj-struct.codecs
  (:import
    [java.util Arrays]
    [java.nio ByteBuffer]))

(defn byte->ubyte
  "Returns the value of the given byte as an integer, when treated as unsigned."
  [x]
  (bit-and 0xFF (int x)))

(defn ubyte->byte
  "Returns the value of the given unsigned byte as an byte."
  [x]
  (.byteValue (Integer/valueOf (int x))))

(defn byte->boolean
  "Returns the value of the given byte as a boolean."
  [x]
  (if (zero? x)
      false
      true))

(defn boolean->byte
  "Returns the value of the given boolean as a byte."
  [x]
  (if (true? x)
      (byte 1)
      (byte 0)))

(defn short->ushort
  "Returns the value of the given short as an integer, when treated as unsigned."
  [x]
  (bit-and 0xFFFF (int x)))

(defn ushort->short
  "Returns the value of the given unsigned short as a short."
  [x]
  (.shortValue (Integer/valueOf (int x))))

(defn int->uint
  "Returns the value of the given integer as a long, when treated as unsigned."
  [x]
  (bit-and 0xFFFFFFFF (long x)))

(defn uint->int
  "Returns the value of the given unsigned integer as an integer."
  [x]
  (.intValue (Long/valueOf (long x))))

(defn long->ulong
  "Returns the value of the given long as a bigint, when treated as unsigned."
  [x]
  (let [bytes (-> (ByteBuffer/allocate 8)
                  (.putLong x)
                  (.array))]
    (BigInteger. 1 bytes)))

(defn ulong->long
  "Returns the value of the given unsigned long as a bigint."
  [x]
  (.longValue (bigint x)))

(defn first-n-letters
  "Returns first n letters (or less, if the string is too short) of the given string. "
  [s n]
  (.substring s 0 (min (.length s) n)))

;
; Some helper functions
;

(defn- str->bytes
  [value length]
  (let [str (first-n-letters value length)
        str-len (count value)
        bytes (.getBytes str)]
    (if (< str-len length)
      (let [buffer (byte-array length)
            _ (System/arraycopy bytes 0 buffer 0 str-len)]
        buffer)
      bytes)))

(defn- get-string
  [buffer length]
  (let [b (byte-array length)
        _ (.get buffer b)]
    (.trim (String. b))))

;
;
;

(defprotocol Codec
  (size-of [this])
  (read-bytes [this buffer])
  (write-bytes [this buffer value]))

(defmacro basic-codec [get put size get-transformer put-transformer]
  "TODO: move position to the end if there are not enough bytes, otherwise it will cause incorrect read/write of remaining structure"
  `(reify
     Codec
     (size-of [_]
       ~size)
     (read-bytes [this# buffer#]
       (when (<= ~size (.remaining buffer#))
         (~get-transformer (~get buffer#))))
     (write-bytes [this# buffer# value#]
       (when (<= ~size (.remaining buffer#))
         (~put buffer# (~put-transformer value#)))
       buffer#)))

(defn string-codec
  [length]
  (reify
    Codec
    (size-of [_]
      length)
    (read-bytes [this buffer]
      (when (<= length (.remaining buffer))
        (get-string buffer length)))
    (write-bytes [_ buffer value]
      (when (>= (.remaining buffer) length)
        (.put buffer (str->bytes value length)))
      buffer)))

(def primitive-codecs
  {:c (basic-codec .get .put 1 char byte) ; char
   :b (basic-codec .get .put 1 identity byte) ; signed byte
   :B (basic-codec .get .put 1 byte->ubyte ubyte->byte) ; unsigned byte
   :? (basic-codec .get .put 1 byte->boolean boolean->byte) ; boolean
   :h (basic-codec .getShort .putShort 2 identity short) ; short
   :H (basic-codec .getShort .putShort 2 short->ushort ushort->short) ; unsigned short
   :i (basic-codec .getInt .putInt 4 identity int) ; int
   :I (basic-codec .getInt .putInt 4 int->uint uint->int) ; unsigned int
   :l (basic-codec .getInt .putInt 4 long int) ; long
   :L (basic-codec .getInt .putInt 4 int->uint uint->int) ; unsigned long
   :q (basic-codec .getLong .putLong 8 identity long) ; long long
   :Q (basic-codec .getLong .putLong 8 long->ulong ulong->long) ; unsigned long long
   :f (basic-codec .getFloat .putFloat 4 identity float) ; float
   :d (basic-codec .getDouble .putDouble 8 identity double) ; double
   })