(ns time.core
 (:require
  [clojure.test :refer [deftest is]]))

(defn now-millis
 []
 #?(:cljs (.getTime (js/Date.))
    :clj (System/currentTimeMillis)))

; TESTS

(deftest ??now-millis
 (is (pos-int? (now-millis))))
