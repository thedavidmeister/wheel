(ns wheel.env.data
 (:require
  [clojure.test :refer [deftest is]]
  #?(:clj environ.core)))

(def valid-environments #{"development" "test-runner" "production"})

#?(:cljs (goog-define environment "")
   :clj (def environment (or (environ.core/env :environment) "development")))

(assert (valid-environments environment) (str "Invalid environment: " environment))

(def testing? (= "test-runner" environment))
(def prod? (= "production" environment))
(def dev? (not prod?))

; TESTS

(deftest ??environment?
 (is (= "test-runner" environment))
 (is testing?)
 (is dev?)
 (is (not prod?)))
