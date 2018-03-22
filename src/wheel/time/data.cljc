(ns time.data
 (:require
  [clojure.test :refer [deftest is]]
  [clojure.spec.alpha :as spec]
  test.generators
  wheel.test.util
  time.spec
  #?(:cljs cljs-time.extend)
  #?(:clj [clj-time.core :as t]
     :cljs [cljs-time.core :as t])
  #?(:clj [clj-time.format :as f]
     :cljs [cljs-time.format :as f])
  #?(:clj [clj-time.coerce :as c]
     :cljs [cljs-time.coerce :as c])))

(defn date-time?
 [d]
 #?(:cljs (or (instance? js/Date d)
              (instance? goog.date.DateTime d)
              (number? d))
    :clj (instance? org.joda.time.DateTime d)))

(defn iso8601->date-time
 [s]
 {:pre [(spec/valid? :time/iso8601 s)]
  :post [(date-time? %)]}
 ; https://github.com/andrewmcveigh/cljs-time/issues/74
 ; don't coerce strings, assume iso8601
 (f/parse (f/formatters :date-time) s))

(defn normalize
 [d]
 {:post [(date-time? %)]}
 (cond
  (number? d)
  (c/from-long d)

  (string? d)
  (iso8601->date-time d)

  #?(:cljs (instance? js/Date d))
  #?(:cljs (normalize (.getTime d)))

  :else d))

(defn date-time->iso8601
 [d]
 ; https://github.com/andrewmcveigh/cljs-time/issues/74
 ; don't coerce strings, assume iso8601
 (if (spec/valid? :time/iso8601 d)
  d
  (f/unparse (f/formatters :date-time) (normalize d))))

; TESTS.

(deftest ??iso8601
 (let [n (t/date-time 2000 1 1)
       iso8601 "2000-01-01T00:00:00.000Z"]
  (is (= iso8601 (date-time->iso8601 n)))
  (is (= n (-> n date-time->iso8601 iso8601->date-time))))

 (is (date-time? (iso8601->date-time (wheel.test.util/fake :time/iso8601)))))

(deftest ??normalize
 (doseq [d [1488499200000
            "2017-03-03T00:00:00.000Z"]]
  (is (t/equal?
       (t/date-time 2017 3 3)
       (normalize d)))))
