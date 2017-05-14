(ns wheel.font.spec
 (:require
  #?(:cljs [cljs.spec.alpha :as spec]
     :clj [clojure.spec.alpha :as spec])))

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
(spec/def :wheel.font/font (spec/keys :req [:wheel.font/name] :opt [:wheel.font/variants :wheel.font/fallback]))
