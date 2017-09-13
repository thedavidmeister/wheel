(ns wheel.ui-gradients.core
 (:require
  wheel.json.core
  [cljs.test :refer-macros [deftest is]])
 (:require-macros wheel.slurp.core))

(defn colors
 "Fetch stops from uigradients.com"
 [name]
 (some->>
  (wheel.slurp.core/slurp "https://raw.githubusercontent.com/ghosh/uiGradients/master/gradients.json")
  wheel.json.core/parse
  (filter #(= name (:name %)))
  first
  :colors))

; TESTS.

(deftest ??colors
 (is (= ["#4DA0B0" "#D39D38"] (colors "Miami Dolphins"))))
