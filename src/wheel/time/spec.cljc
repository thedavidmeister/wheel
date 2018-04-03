(ns time.spec
 (:require
  [clojure.spec.alpha :as spec]
  wheel.test.generators))

(spec/def :time/iso8601
 (spec/spec string?
  :gen (constantly wheel.test.generators/iso8601)))
