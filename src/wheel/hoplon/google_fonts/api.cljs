(ns wheel.hoplon.google-fonts.api
 (:require
  [cljs.test :refer-macros [deftest is are]]))

; Most Google Fonts functions work with hash maps representing a font.
; The keys are as follows:
; :name = Human readable name exactly as it appears in Google Fonts (required).
; :variants = A collection of variant strings, e.g. ["400" "400i" "900"].
; :fallback = The fallback font to use. Most commonly "serif" or "sans-serif".

(defn font->uri-str
 "Given a font hash map, returns a string suitable in a Google Fonts URI"
 [{:keys [name variants]}]
 {:pre [(or (nil? variants) (coll? variants))]}
 (let [name-uri (clojure.string/replace name " " "+")
       variants-uri (when (seq variants)
                     (str ":" (clojure.string/join "," variants)))]
  (str name-uri variants-uri)))

; TESTS

(deftest ??font->uri-str
 (are [i o] (= o (font->uri-str i))
  {:name ""} ""
  {:name "foo"} "foo"
  {:name "foo bar"} "foo+bar"
  {:name "foo" :variants []} "foo"
  {:name "foo" :variants ["1"]} "foo:1"
  {:name "foo" :variants ["1" "2"]} "foo:1,2"
  {:name "foo bar" :variants ["1" "2"]} "foo+bar:1,2"))
