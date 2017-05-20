(ns wheel.link.hoplon
 (:require
  [hoplon.core :as h]
  [wheel.dom.traversal :as dt]
  [cljs.test :refer-macros [deftest is]]))

(defn external
 ([href] (external href href))
 ([href text]
  {:pre [(string? href)]}
  (h/a
   :href href
   :target "_blank"
   text)))

; TESTS

(defn external? [l a t]
 (is (dt/is? l "a"))
 (is (dt/is? l "[target=\"_blank\"]"))
 (is (dt/is? l (str "[href=\"" a "\"]")))
 (is (= t (dt/text l))))

(deftest ??external
 (let [es [["foos" "bars" "bars"]
           ["foos" (h/div "bars") "bars"]]]
  (doseq [[a t e] es]
   (external? (external a) a a)
   (external? (external a t) a e))))
