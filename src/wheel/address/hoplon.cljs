(ns wheel.address.hoplon
 (:require
  [hoplon.core :as h]
  wheel.dom.traversal
  [cljs.test :refer-macros [deftest is]]))

(defn simple
 "The simplest address element that could possibly work"
 [a]
 (h/span :class "address" a))

; TESTS

(defn simple?
 [el a]
 (is (wheel.dom.traversal/is? el "span.address"))
 (is (= a (wheel.dom.traversal/text el))))

(deftest ??simple
 (simple? (simple "foo") "foo"))
