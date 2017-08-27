(ns wheel.dom.data
 (:require
  [hoplon.core :as h]
  goog.dom
  [clojure.spec.alpha :as spec]
  [cljs.test :refer-macros [deftest is]]
  [clojure.test.check.generators :as gen]))

(spec/def :wheel.dom/element
 (spec/spec
  goog.dom/isElement
  :gen
  (constantly
   (gen/fmap
    apply
    (gen/elements [h/div h/span h/p])))))

(spec/def :wheel.dom/selector string?)

(defn el? [el] (spec/valid? :wheel.dom/element el))
(defn sel? [s] (spec/valid? :wheel.dom/selector s))
(defn el-or-sel?
 [el-or-sel]
 (or
  (el? el-or-sel)
  (sel? el-or-sel)))
