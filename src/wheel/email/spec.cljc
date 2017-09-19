(ns wheel.email.spec
 (:require
  [clojure.spec.alpha :as spec]
  com.gfredericks.test.chuck.generators))

; https://davidcel.is/posts/stop-validating-email-addresses-with-regex/
(def regex #".+@.+\..+")

(spec/def :wheel.email/email
 #?{:clj
    (spec/spec
     (spec/and
      string?
      (partial re-matches regex))
     :gen
     (constantly
      (com.gfredericks.test.chuck.generators/string-from-regex regex)))
    :cljs
    (spec/and
     string?
     (partial re-matches regex))})
