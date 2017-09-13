(ns string.spec
 (:require
  [clojure.spec.alpha :as spec]
  [clojure.test :refer [deftest is]]
  wheel.test.util))

(spec/def :string/not-blank
 (spec/and string? (complement clojure.string/blank?)))

; TESTS
(deftest ??not-blank
 (is (string? (wheel.test.util/fake :string/not-blank)))
 (is (not (clojure.blank? (wheel.test.util/fake :string/not-blank)))))
