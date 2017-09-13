(ns wheel.json.core
 (:require
  wheel.test.util
  wheel.json.spec
  clojure.test.check.generators
  #?(:clj cheshire.core)
  [clojure.spec.alpha :as spec]
  [clojure.test :refer [deftest is]]))

(defn string
 [data]
 {:post [(spec/valid? :wheel.json/json-string %)]}
 #?(:cljs (.stringify js/JSON data)
    :clj (cheshire.core/generate-string data)))

(defn parse
 [json-string]
 {:pre [(spec/valid? :wheel.json/json-string json-string)]}
 #?(:cljs (.parse js/JSON json-string)
    :clj (cheshire.core/parse-string json-string)))

; TESTS

(deftest ??round-trip
 (let [d (wheel.test.util/fake clojure.test.check.generators/any)]
  (is (= d (parse (string d))))))
