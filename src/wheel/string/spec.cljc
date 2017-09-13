(ns wheel.string.spec
 (:require
  [clojure.spec.alpha :as spec]
  [clojure.test :refer [deftest is]]
  wheel.test.util))

(spec/def :wheel.string/string string?)

(spec/def :wheel.string/not-blank
 (spec/and
  :wheel.string/string
  (complement clojure.string/blank?)))

; TESTS

(deftest ??not-blank
 (is (string? (wheel.test.util/fake :wheel.string/not-blank)))
 (is (not (clojure.string/blank? (wheel.test.util/fake :wheel.string/not-blank)))))
