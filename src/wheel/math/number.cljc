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

; TESTS

(deftest ??nan?
 (is (nan? nan))
 (are [i] (not (nan? i))
  1 nil "" "foo" 100 0 -1))
