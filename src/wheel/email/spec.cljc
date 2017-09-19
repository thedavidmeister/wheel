(ns wheel.email.spec
 (:require
  [clojure.spec.alpha :as spec]
  [clojure.test.check.generators :as gen]))

; https://davidcel.is/posts/stop-validating-email-addresses-with-regex/
(def regex #".+@.+\..+")

(spec/def :wheel.email/email
 (spec/spec
  (spec/and
   string?
   (partial re-matches regex))
  :gen
  (constantly
   (gen/fmap
    (fn [[a b c]]
     (str a "@" b "." c))
    (gen/vector
     gen/string
     3)))))
