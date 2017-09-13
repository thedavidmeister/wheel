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
 #?(:cljs (.stringify js/JSON (clj->js data))
    :clj (cheshire.core/generate-string data)))

(defn parse
 [json-string]
 {:pre [(spec/valid? :wheel.json/json-string json-string)]}
 #?(:cljs
    (clojure.walk/keywordize-keys
     (js->clj (.parse js/JSON json-string)))
    :clj (cheshire.core/parse-string json-string true)))

; TESTS

(deftest ??round-trip
 ; test check finds things that don't really handle round trips well at all,
 ; like nested NaN and namespaced keywords or "weird" keys that JS doesn't like.
 (doseq [[t s p] [[1 "1" 1]
                  [true "true" true]
                  [false "false" false]
                  [{} "{}" {}]
                  [{"foo" "bar"} "{\"foo\":\"bar\"}" {:foo "bar"}]
                  [{:foo "bar"} "{\"foo\":\"bar\"}" {:foo "bar"}]]]
  (is (= s (string t)))
  (is (= p (parse (string t))))))
