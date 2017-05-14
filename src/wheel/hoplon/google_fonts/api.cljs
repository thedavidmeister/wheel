(ns wheel.hoplon.google-fonts.api
 (:require
  wheel.hoplon.google-fonts.config
  [cljs.test :refer-macros [deftest is]]))

; Most Google Fonts functions work with hash maps representing a font.
; The keys are as follows:
; :name = Human readable name exactly as it appears in Google Fonts (required).
; :variants = A collection of variant strings, e.g. ["400" "400i" "900"].
; :fallback = The fallback font to use. Most commonly "serif" or "sans-serif" in
;             the wild, but excluding the fallback uses the default fallback
;             from wheel.hoplon.google-fonts.config which is more sophisticated,
;             for sans-serif fonts at least.

(defn font->uri-str
 "Given a font hash map, returns a string suitable in a Google Fonts URI"
 [font]
 {:pre [(or (nil? variants) (coll? variants))]}
 (let [name (::name font)
       variants (::variants font)
       name-uri (clojure.string/replace name " " "+")
       variants-uri (when (seq variants)
                     (str ":" (clojure.string/join "," variants)))]
  (str name-uri variants-uri)))

(defn fonts->uri-str
 "Given a sequence of fonts, returns a string suitable in a Google Fonts URI"
 [fonts]
 {:pre [(sequential? fonts)]}
 (clojure.string/join "|" (map font->uri-str fonts)))

(defn fonts->url
 "Given a sequence of fonts, returns the entire Google Fonts URL"
 [fonts]
 (str wheel.hoplon.google-fonts.config/base-url (fonts->uri-str fonts)))

(defn get-fallback
 ([] (get-fallback wheel.hoplon.google-fonts.config/default-fallback))
 ([k] (get wheel.hoplon.google-fonts.config/well-known-fallbacks k k)))

(defn font->css-str
 [font]
 (let [name (::name font)
       fallback (or (::fallback font) (get-fallback))]
  (str "font-family: \"" name "\", " fallback ";")))

; TESTS

(def examples
 (partition 2
  [{::name ""} ""
   {::name "foo"} "foo"
   {::name "foo bar"} "foo+bar"
   {::name "foo" ::variants []} "foo"
   {::name "foo" ::variants ["1"]} "foo:1"
   {::name "foo" ::variants ["1" "2"]} "foo:1,2"
   {::name "foo bar" ::variants ["1" "2"]} "foo+bar:1,2"]))

(deftest ??font->uri-str
 ; examples
 (doseq [[i o] examples]
  (is (= o (font->uri-str i)))))

(deftest ??fonts->uri-str
 ; examples
 (is (= "" (fonts->uri-str [])))
 (let [[i o] (rand-nth examples)]
  (is (= o (fonts->uri-str [i]))))
 (let [[i o] (rand-nth examples)
       [i' o'] (rand-nth examples)]
  (is (= (str o "|" o') (fonts->uri-str [i i'])))))

(deftest ??fonts->url
 ; examples
 (is (= wheel.hoplon.google-fonts.config/base-url (fonts->url [])))
 (let [[i o] (rand-nth examples)]
  (is (= (str wheel.hoplon.google-fonts.config/base-url o)
         (fonts->url [i]))))
 (let [[i o] (rand-nth examples)
       [i' o'] (rand-nth examples)]
  (is (= (str wheel.hoplon.google-fonts.config/base-url o "|" o')
         (fonts->url [i i'])))))

(deftest ??get-fallback
 ; oracle
 (is (= (get wheel.hoplon.google-fonts.config/well-known-fallbacks wheel.hoplon.google-fonts.config/default-fallback)
        (get-fallback wheel.hoplon.google-fonts.config/default-fallback)))
 (is (= (get wheel.hoplon.google-fonts.config/well-known-fallbacks "medium")
        (get-fallback "medium")))
 (is (= "sans-serif" (get-fallback "sans-serif"))))

(deftest ??font->css-str
 ; oracle
 (let [[i _] (rand-nth examples)
       n (::name i)]
  (is (= (str "font-family: \"" n "\", " (get-fallback) ";")
         (font->css-str i))))

 (let [[i _] (rand-nth examples)
       f (str (random-uuid))
       i (merge i {::fallback f})
       n (::name i)]
  (is (= (str "font-family: \"" n "\", " f ";")
         (font->css-str i)))))
