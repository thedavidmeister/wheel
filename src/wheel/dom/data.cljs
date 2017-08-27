(ns wheel.dom.data
 (:require
  goog.dom
  [clojure.spec.alpha :as spec]))

(spec/def :wheel.dom/element goog.dom/isElement)
(spec/def :wheel.dom/selector string?)

(defn el? [el] (spec/valid? :wheel.dom/element el))
(defn sel? [s] (spec/valid? :wheel.dom/selector s))
(defn el-or-sel?
 [el-or-sel]
 (or
  (el? el-or-sel)
  (sel? el-or-sel)))
