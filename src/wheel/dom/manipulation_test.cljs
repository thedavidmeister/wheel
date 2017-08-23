(ns wheel.dom.manipulation-test
 (:require
  wheel.dom.manipulation
  wheel.dom.traversal
  [hoplon.core :as h]
  [cljs.test :refer-macros [deftest is]]))

(deftest ??document-append-remove
 (let [el (h/div)
       doc-el (.-documentElement js/document)]
  (is (not (wheel.dom.traversal/contains? doc-el el)))
  (wheel.dom.manipulation/document-append! el)
  (is (wheel.dom.traversal/contains? doc-el el))
  (wheel.dom.manipulation/document-remove! el)
  (is (not (wheel.dom.traversal/contains? doc-el el)))))
