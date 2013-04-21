;   Copyright (c) Pavel Prokopenko. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php).
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns clj-struct.core
  (:use
    [clj-struct codecs])
  (:import
    [java.io RandomAccessFile]
    [java.nio ByteBuffer]))

;
; Utility functions
;

(defn digit?
  "Checks if the given character is a digit."
  [c]
  (Character/isDigit c))

(defn str->int
  [s]
  (Integer/parseInt s))

;
;
;

(defn- lookup-codec
  [name times]
  (let [type (keyword name)
        codec (type primitive-codecs)]
    (cond (= :s type) [(string-codec times) times 1]
      (nil? codec) (throw (Exception. (format "Unkown type %s" type)))
      :else [codec (size-of codec) times])))

(defn- trans-fn
  [x]
  (let [times-raw (x 1)
        times (if (nil? times-raw)
                   1
                  (str->int (x 1)))]
    (lookup-codec (x 2) times)))

(defn- struct-parse
  [fmt]
  (map trans-fn (re-seq #"(\d+)?(\w|\?)" fmt)))

(defn- calc-size-int
  [pattern]
  (reduce #(+ %1 (* (%2 1) (%2 2))) 0 pattern))

(defn- pack-single
  [b codec times xs]
  (loop [res b x times v xs]
    (if (zero? x)
      [res v]
      (recur (write-bytes codec res (first v)) (dec x) (rest v)))))

(defn- unpack-single-into
  [source xs [codec size times]]
  (read-bytes-into codec source times xs))

(defn- byte-buffer
  "Coerce the value to a ByteBuffer of the given size. Values that are already ByteBuffers are returned untouched."
  [x size]
  (cond
    (instance? ByteBuffer x) x
    (instance? RandomAccessFile x) (let [channel (.getChannel x)
                                         bb (ByteBuffer/allocate size)
                                         _ (.read channel bb)
                                         _ (.rewind bb)]
                                     ; it's possible to check if the number of bytes read is the same as we want, but..
                                     ; probably it's not worth it, at least for now
                                     bb)
    :else (throw (Exception. (str "Cannot convert to ByteBuffer: " x)))))

;
; Public API
;

(defn calc-size
  "Returns the size of the struct corresponding to the given format."
  [fmt]
  (calc-size-int (struct-parse fmt)))

(defn pack
  "Returns a byte buffer containing the values packed according to the given format."
  [fmt xs]
  (let [pattern (struct-parse fmt)
        size (calc-size-int pattern)
        buffer (ByteBuffer/allocate size)]
    (loop [b buffer
           p pattern
           s xs]
      (if (empty? p)
        (do (.rewind b)
            b)
        (let [[codec size times] (first p)
              [res v] (pack-single b codec times s)]
          (recur res (rest p) v))))))

(defn unpack
  "Unpacks the source according to the given format. The result is a sequence even if it contains only one item. If the source doesn't have enough data to unpack the format, nils will be returned in place of missing values."
  [fmt source]
  (let [pattern (struct-parse fmt)
        size (calc-size-int pattern)
        bb (byte-buffer source size)]
    (reduce (partial unpack-single-into bb) [] pattern)))
