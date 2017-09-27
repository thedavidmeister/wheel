(ns wheel.font.core
 (:require
  wheel.font.config
  wheel.font.spec
  medley.core
  [clojure.spec.alpha :as spec]
  [clojure.test :refer [deftest is]]))

(defn get-fallback
 "Looks up a fallback string from the config"
 ([] (get-fallback wheel.font.config/default-fallback))
 ([k] (get wheel.font.config/well-known-fallbacks k k)))

(defn font->family-str
 "Given a font map, returns a CSS font family string, including the fallback"
 [f]
 {:pre [(spec/valid? :wheel.font/font f)]}
 (let [n (:wheel.font/name f)
       fallback (or (:wheel.font/fallback f) (get-fallback))]
  (str "\"" n "\", " fallback)))

(defn font->css-str
 "Given a font map, returns a CSS string, including the fallback"
 [f]
 {:post [(string? %)]}
 (str "font-family: " (font->family-str f) ";"))

(defn font->css-map
 "Given a font map, returns a Hoplon CSS map, including the fallback"
 [f]
 {:post [(map? %)]}
 {:font-family (font->family-str f)})

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
