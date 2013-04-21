;   Copyright (c) Pavel Prokopenko. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php).
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns clj-struct.core-test
  (:use
    [clojure test]
    [clj-struct core])
  (:import [java.nio ByteBuffer]))

(defn buf->str
  [^ByteBuffer b]
  (String. (.array b)))

(deftest calc-size-test
  (testing "Should return correct structure size."
    (is (= 78 (calc-size "32shhIIIIII4s4sIIH")))
    (is (= 200 (calc-size "IIIIII40sIIIIIIIIIIIII36sIIII8sHHIIIII")))))

(defn- back-and-forth-test
  [fmt xs]
  (let [b (pack fmt xs)
        r (unpack fmt b)]
    (is (= xs r))))

(deftest complext-pack-test
  (testing "Test packing/unpacking with different combinations of data"
    (back-and-forth-test "4s c ?? 3s i" ["test" \g true false "dog" 123])
    (back-and-forth-test "2s?i" ["no" false 62345])
    (back-and-forth-test "3si" ["dog" 13])
    (back-and-forth-test "?i2s?i" [true 666 "no" false 12345])
    (back-and-forth-test "3i" [10 578 271])
    (back-and-forth-test "2c 2? 2s" [\a \b true false "sd"])
    (back-and-forth-test "5c" [\a \b \c \d \e])
    (back-and-forth-test "4?" [false true false true])
    (back-and-forth-test "hH" [-32768 65535])
    (back-and-forth-test "cbB?hHiIqQfd4s" [\d -2 254 false -32768 65535 -2147483648 4294967295 -9223372036854775807 18446744073709551615N (float 0.2) 0.56652 "text"])
    (back-and-forth-test "2s3s2s" ["no" "yes" "no"])))