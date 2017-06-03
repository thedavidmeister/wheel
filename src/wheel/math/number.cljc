(ns wheel.math.number
 (:require
  #?(:cljs [cljs.test :refer-macros [deftest is are]]
     :clj [clojure.test :refer [deftest is are]])))

(def pi #?(:clj Math/PI
           :cljs (.-PI js/Math)))

(def nan ((fn [] #?(:clj Double/NaN :cljs js/NaN))))

(defn nan?
 [n]
 ; http://adripofjavascript.com/blog/drips/the-problem-with-testing-for-nan-in-javascript.html
 (if (number? n)
  (not (== n n))
  false))

(defn safe-bigdec
 [n]
 (if (and n (not (nan? n)))
  #?(:clj (bigdec n)
     :cljs n)
  n))

(defn parse-int
 "Parse a string to an int using native language parsing logic. Forces radix 10 for JS."
 [s]
 #?(:clj (Integer/parseInt s)
    :cljs (js/parseInt s 10)))

; TESTS

(deftest ??nan?
 (is (nan? nan))
 (are [i] (not (nan? i))
  1 nil "" "foo" 100 0 -1))

(deftest ??parse-int
 (are [i o] (= o (parse-int i))
  "0" 0
  "1" 1
  "1a" 1
  ; Old versions of ECMAScript assume strings with leading 0 are base 8 if radix
  ; is not set correctly.
  "01" 1
  "01234" 1234))
