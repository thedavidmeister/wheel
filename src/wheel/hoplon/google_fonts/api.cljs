(ns wheel.hoplon.google-fonts.api
 (:require
  wheel.hoplon.google-fonts.config
  [cljs.spec.alpha :as spec]
  [cljs.test :refer-macros [deftest is]]))

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

(defn font->uri-str
 "Given a font hash map, returns a string suitable in a Google Fonts URI"
 [font]
 {:pre [(spec/valid? :wheel.font/font font)]}
 (let [name (:wheel.font/name font)
       variants (:wheel.font/variants font)
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
 "Looks up a fallback string from the config"
 ([] (get-fallback wheel.hoplon.google-fonts.config/default-fallback))
 ([k] (get wheel.hoplon.google-fonts.config/well-known-fallbacks k k)))

(defn font->family-str
 "Given a font map, returns a CSS font family string, including the fallback"
 [font]
 {:pre [(spec/valid? :wheel.font/font font)]}
 (let [name (:wheel.font/name font)
       fallback (or (:wheel.font/fallback font) (get-fallback))]
  (str "\"" name "\", " fallback)))

(defn font->css-str
 "Given a font map, returns a CSS string, including the fallback"
 [font]
 {:post [(string? %)]}
 (str "font-family: " (font->family-str font) ";"))

(defn font->css-map
 "Given a font map, returns a Hoplon CSS map, including the fallback"
 [font]
 {:post [(map? %)]}
 {:font-family (font->family-str font)})

; TESTS

(def examples
 (partition 2
  [{:wheel.font/name ""} ""
   {:wheel.font/name "foo"} "foo"
   {:wheel.font/name "foo bar"} "foo+bar"
   {:wheel.font/name "foo" :wheel.font/variants []} "foo"
   {:wheel.font/name "foo" :wheel.font/variants ["1"]} "foo:1"
   {:wheel.font/name "foo" :wheel.font/variants ["1" "2"]} "foo:1,2"
   {:wheel.font/name "foo bar" :wheel.font/variants ["1" "2"]} "foo+bar:1,2"]))

(deftest ??spec
 ; examples
 (doseq [[i _] examples]
  (is (spec/valid? :wheel.font/font i))
  (is (spec/valid? :wheel.font/font (merge i {:wheel.font/fallback "baz"})))
  (is (not (spec/valid? :wheel.font/font (dissoc i :wheel.font/name))))))

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

(deftest ??font->family-str
 ; oracle
 (let [[i _] (rand-nth examples)
       n (:wheel.font/name i)]
  (is (= (str "\"" n "\", " (get-fallback))
         (font->family-str i)))

  (is (= (str "font-family: " (font->family-str i) ";")
         (font->css-str i)))
  (is (= {:font-family (font->family-str i)}
         (font->css-map i))))

 (let [[i _] (rand-nth examples)
       f (str (random-uuid))
       i (merge i {:wheel.font/fallback f})
       n (:wheel.font/name i)]
  (is (= (str "\"" n "\", " f)
         (font->family-str i)))

  (is (= (str "font-family: " (font->family-str i) ";")
         (font->css-str i)))
  (is (= {:font-family (font->family-str i)}
         (font->css-map i)))))
