;   Copyright (c) Pavel Prokopenko. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php).
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns clj-struct.codecs-test
  (:use [clojure test]
        [clj-struct codecs])
  (:import [java.nio ByteBuffer]))

(deftest byte-to-ubyte-test
  (testing "Correctly converts the given byte to an integer"
    (is (= 0 (byte->ubyte 0)))
    (is (= 1 (byte->ubyte 1)))
    (is (= 127 (byte->ubyte 127)))
    (is (= 128 (byte->ubyte -128)))
    (is (= 129 (byte->ubyte -127)))
    (is (= 255 (byte->ubyte -1)))))

(deftest ubyte-to-byte-test
  (testing "Correctly converts the given unsigned byte to a byte"
    (is (= 0 (ubyte->byte 0)))
    (is (= 1 (ubyte->byte 1)))
    (is (= 127 (ubyte->byte 127)))
    (is (= -128 (ubyte->byte 128)))
    (is (= -127 (ubyte->byte 129)))
    (is (= -1 (ubyte->byte 255)))))

(deftest byte-to-boolean-test
  (testing "Correctly converts the given byte to a boolean"
    (is (= false (byte->boolean 0)))
    (is (= true (byte->boolean 1)))
    (is (= true (byte->boolean 10)))
    (is (= true (byte->boolean -1)))))

(deftest boolean-to-byte-test
  (testing "Correctly converts the given boolean to a byte"
    (is (= 1 (boolean->byte true)))
    (is (= 0 (boolean->byte false)))))

(deftest short-to-ushort-test
  (testing "Correctly converts the given short to an integer"
    (is (= 0 (short->ushort 0)))
    (is (= 1 (short->ushort 1)))
    (is (= 32767 (short->ushort 32767)))
    (is (= 32768 (short->ushort -32768)))
    (is (= 32769 (short->ushort -32767)))
    (is (= 65535 (short->ushort -1)))))

(deftest ushort-to-short-test
  (testing "Correctly converts the given unsigned short to a short"
    (is (= 0 (ushort->short 0)))
    (is (= 1 (ushort->short 1)))
    (is (= 32767 (ushort->short 32767)))
    (is (= -32768 (ushort->short 32768)))
    (is (= -32767 (ushort->short 32769)))
    (is (= -1 (ushort->short 65535)))))

(deftest int-to-uint-test
  (testing "Correctly converts the given integer to a long"
    (is (= 0 (int->uint 0)))
    (is (= 1 (int->uint 1)))
    (is (= 2147483647 (int->uint  2147483647)))
    (is (= 2147483648 (int->uint -2147483648)))
    (is (= 2147483649 (int->uint -2147483647)))
    (is (= 4294967295 (int->uint -1)))))

(deftest uint-to-int-test
  (testing "Correctly converts the given unsigned integer to an integer"
    (is (= 0 (uint->int 0)))
    (is (= 1 (uint->int 1)))
    (is (= 2147483647 (uint->int  2147483647)))
    (is (= -2147483648 (uint->int 2147483648)))
    (is (= -2147483647 (uint->int 2147483649)))
    (is (= -1 (uint->int 4294967295)))))

(deftest long->ulong-test
  (testing "Correctly converts the given long to an unsigned long"
    (is (= 0 (long->ulong 0)))
    (is (= 1 (long->ulong 1)))
    (is (= 9223372036854775807 (long->ulong  9223372036854775807)))
    (is (= 9223372036854775808 (long->ulong -9223372036854775808)))
    (is (= 9223372036854775809 (long->ulong -9223372036854775807)))
    (is (= 18446744073709551615 (long->ulong -1)))))

(deftest ulong->long-test
  (testing "Correctly converts the given unsigned long to a long"
    (is (= 0 (ulong->long 0)))
    (is (= 1 (ulong->long 1)))
    (is (= 9223372036854775807 (ulong->long  9223372036854775807)))
    (is (= -9223372036854775808 (ulong->long 9223372036854775808)))
    (is (= -9223372036854775807 (ulong->long 9223372036854775809)))
    (is (= -1 (ulong->long 18446744073709551615)))))