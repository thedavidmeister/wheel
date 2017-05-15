(ns wheel.font.spec
 (:require
  wheel.font.config
  #?(:cljs [cljs.spec :as spec]
     :clj [clojure.spec :as spec])
  #?(:cljs [cljs.test :refer-macros [deftest is are]]
     :clj [clojure.test :refer [deftest is are]])))

; Human readable name exactly as it appears in Google Fonts (required).
(spec/def :wheel.font/name string?)

; A collection of variant strings, e.g. ["400" "400i" "900"].
(spec/def :wheel.font/variants sequential?)

; The fallback font(s) to use. Most commonly "serif" or "sans-serif" in the
; wild, but excluding the fallback uses the default fallback from
; wheel.hoplon.google-fonts.config which is more sophisticated, for sans-serif
; fonts at least.
(spec/def :wheel.font/fallback string?)

; A Google Font.
(spec/def :wheel.font/font
 (spec/keys
  :req [:wheel.font/name]
  :opt [:wheel.font/variants :wheel.font/fallback]))

; TESTS

(deftest ??spec
 ; examples
 (doseq [[i _] wheel.font.config/test-examples]
  (is (spec/valid? :wheel.font/font i))
  (is (spec/valid? :wheel.font/font (merge i {:wheel.font/fallback "baz"})))
  (is (not (spec/valid? :wheel.font/font (dissoc i :wheel.font/name))))))
