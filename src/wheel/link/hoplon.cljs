(ns wheel.link.hoplon
 (:require
  [hoplon.core :as h]
  [wheel.dom.traversal :as dt]
  [cljs.test :refer-macros [deftest is]]))

(defn external
 ([href] (external href href))
 ([href text]
  {:pre [(string? href) (string? text)]}
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
 (let [a "foos"
       t "bars"]
  (external? (external a) a a)
  (external? (external a t) a t)))
