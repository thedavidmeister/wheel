(ns wheel.phone.hoplon
 (:require
  [hoplon.core :as h]
  cuerdas.core
  wheel.dom.traversal
  wheel.string.core
  [cljs.test :refer-macros [deftest is]]))

(defn phone
 [p]
 (let [p (j/cell= (cuerdas.core/collapse-whitespace (or p phone.config/default)))]
  (h/a
   :class #{"phone"}
   :href (j/cell= (str "tel:" (wheel.string.core/no-space p)))
   p)))

; TESTS

(deftest ??phone
 (let [el (phone "+61444 123 456")]
  (is (wheel.dom.traversal/is? el "a[href=\"tel:+61444123456\"]"))
  (is (wheel.dom.traversal/is? el "a.phone"))
  (is (= "+61444 123 456" (wheel.dom.traversal/text el))))

 (let [el (phone "1\n 2   3")]
  (is (wheel.dom.traversal/is? el "a[href=\"tel:123\"]"))
  (is (wheel.dom.traversal/is? el "a.phone"))
  (is (= "1 2 3" (wheel.dom.traversal/text el)))))
