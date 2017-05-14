(ns wheel.abn.core
 (:require
  wheel.string
  [cljs.test :refer-macros [deftest is are]]))

(defn normalize
 [n]
 {:post [(= 11 (count (wheel.string/no-space %)))
         (= 14 (count %))]}
 (let [s (wheel.string/no-space (str n))]
  (assert (= 11 (count s)))
  (let [[head rest] (split-at 2 s)]
   (->> (into [head] (partition 3 rest))
    (map #(apply str %))
    (clojure.string/join " ")))))

(defn abr-search-url
 [n]
 (let [s (wheel.string/no-space (str n))]
  (str "https://abr.business.gov.au/SearchByAbn.aspx?SearchText=" s)))

; TESTS.

(deftest ??normalize
 (are [n] (= "12 345 678 910" (normalize n))
  12345678910
  "12345678910"
  "12 345 678 910"
  "1 2 3 4 5 6 7 8 9 1 0"))

(deftest ??abr-search-url
 (are [n] (= "https://abr.business.gov.au/SearchByAbn.aspx?SearchText=12345678910" (abr-search-url n))
  12345678910
  "12345678910"
  "12 345 678 910"
  "1 2 3 4 5 6 7 8 9 1 0"))
