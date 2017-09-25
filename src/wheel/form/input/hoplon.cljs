(ns wheel.form.input.hoplon
 (:require
  [hoplon.core :as h]
  wheel.dom.traversal
  [cljs.test :refer-macros [deftest is]]))

(def input h/input)

; TESTS

(deftest ??input
 (is (wheel.dom.traversal/is (input) "input")))
