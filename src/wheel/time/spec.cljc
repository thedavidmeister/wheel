(ns time.spec
 (:require
  [clojure.spec.alpha :as spec]
  test.generators))

(spec/def :time/iso8601
 (spec/spec string?
  :gen (constantly test.generators/iso8601)))
