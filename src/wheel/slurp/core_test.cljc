(ns wheel.slurp.core-test
 #?(:cljs (:require-macros wheel.slurp.core))
 (:require
  #?(:clj wheel.slurp.core)
  #?(:cljs [cljs.test :refer-macros [deftest is are]]
     :clj [clojure.test :refer [deftest is are]])
  #?(:clj [clojure.edn :as edn]
     :cljs [cljs.reader :as edn])))

(deftest ??slurp
 (is (= "foo" (edn/read-string (wheel.slurp.core/slurp "src/wheel/slurp/test_data.edn")))))
