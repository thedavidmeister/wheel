(ns wheel.json.spec
 (:require
  [clojure.spec.alpha :as spec]))

(spec/def :wheel.json/json-string string?)
