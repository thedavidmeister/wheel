(ns wheel.font.google.core
 (:require
  wheel.font.config
  wheel.font.google.config
  #?(:cljs [cljs.spec.alpha :as spec]
     :clj [clojure.spec :as spec])
  #?(:cljs [cljs.test :refer-macros [deftest is are]]
     :clj [clojure.test :refer [deftest is are]])))

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
 (str wheel.font.google.config/base-url (fonts->uri-str fonts)))

; TESTS

(deftest ??font->uri-str
 ; examples
 (doseq [[i o] wheel.font.config/test-examples]
  (is (= o (font->uri-str i)))))

(deftest ??fonts->uri-str
 ; examples
 (is (= "" (fonts->uri-str [])))
 (let [[i o] (rand-nth wheel.font.config/test-examples)]
  (is (= o (fonts->uri-str [i]))))
 (let [[i o] (rand-nth wheel.font.config/test-examples)
       [i' o'] (rand-nth wheel.font.config/test-examples)]
  (is (= (str o "|" o') (fonts->uri-str [i i'])))))

(deftest ??fonts->url
 ; examples
 (is (= wheel.font.google.config/base-url (fonts->url [])))
 (let [[i o] (rand-nth wheel.font.config/test-examples)]
  (is (= (str wheel.font.google.config/base-url o)
         (fonts->url [i]))))
 (let [[i o] (rand-nth wheel.font.config/test-examples)
       [i' o'] (rand-nth wheel.font.config/test-examples)]
  (is (= (str wheel.font.google.config/base-url o "|" o')
         (fonts->url [i i'])))))
