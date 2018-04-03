(ns wheel.test.generators
 (:require
  time.core
  #?(:clj [clj-time.core :as t]
     :cljs [cljs-time.core :as t])
  #?(:clj [clj-time.coerce :as c]
     :cljs [cljs-time.coerce :as c])
  [clojure.test.check.generators :as gen]))

(def date-time
 (gen/fmap
  (comp
   c/from-long
   ; hourly granularity
   (partial * 1000 60 60))
  (gen/large-integer*
   {:min 1
    :max 1000000})))

(def now-millis
 (gen/fmap #(%) (gen/return time.core/now-millis)))

(def iso8601
 (gen/fmap c/to-string date-time))

(def now-iso8601
 (gen/fmap #(c/to-string (%)) (gen/return t/now)))

(def keyword-string
 (gen/fmap str gen/keyword))

(def any-serialized
 (gen/fmap pr-str gen/any))
