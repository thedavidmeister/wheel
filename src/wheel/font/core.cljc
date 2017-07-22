(ns wheel.font.core
 (:require
  wheel.font.config
  wheel.font.spec
  medley.core
  [clojure.spec.alpha :as spec]
  #?(:cljs [cljs.test :refer-macros [deftest is are]]
     :clj [clojure.test :refer [deftest is are]])))

(defn get-fallback
 "Looks up a fallback string from the config"
 ([] (get-fallback wheel.font.config/default-fallback))
 ([k] (get wheel.font.config/well-known-fallbacks k k)))

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

(deftest ??get-fallback
 ; oracle
 (is (= (get wheel.font.config/well-known-fallbacks wheel.font.config/default-fallback)
        (get-fallback wheel.font.config/default-fallback)))
 (is (= (get wheel.font.config/well-known-fallbacks "medium")
        (get-fallback "medium")))
 (is (= "sans-serif" (get-fallback "sans-serif"))))

(deftest ??font->family-str
 ; oracle
 (let [[i _] (rand-nth wheel.font.config/test-examples)
       n (:wheel.font/name i)]
  (is (= (str "\"" n "\", " (get-fallback))
         (font->family-str i)))

  (is (= (str "font-family: " (font->family-str i) ";")
         (font->css-str i)))
  (is (= {:font-family (font->family-str i)}
         (font->css-map i))))

 (let [[i _] (rand-nth wheel.font.config/test-examples)
       f (str (medley.core/random-uuid))
       i (merge i {:wheel.font/fallback f})
       n (:wheel.font/name i)]
  (is (= (str "\"" n "\", " f)
         (font->family-str i)))

  (is (= (str "font-family: " (font->family-str i) ";")
         (font->css-str i)))
  (is (= {:font-family (font->family-str i)}
         (font->css-map i)))))
