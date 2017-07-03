(ns wheel.ui-gradients.core
 (:require
  [cljs.test :refer-macros [deftest is]])
 (:require-macros wheel.slurp.core))

(defn colors
 "Fetch stops from uigradients.com"
 [name]
 (let [parse (.-parse js/JSON)]
  (get
   (some->>
    (wheel.slurp.core/slurp "https://raw.githubusercontent.com/ghosh/uiGradients/master/gradients.json")
    parse
    js->clj
    (filter #(= name (get % "name")))
    first)
   "colors")))

; TESTS.

(deftest ??stops
 (is (= ["#4DA0B0" "#D39D38"] (colors "Miami Dolphins"))))
