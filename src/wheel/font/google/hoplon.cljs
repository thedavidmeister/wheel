(ns wheel.font.google.hoplon
 (:require
  [hoplon.core :as h]
  wheel.hoplon.stylesheet.dom
  wheel.font.google.core))

(defn link
 "Given a sequence of fonts returns the stylesheet link to Google Fonts"
 [fonts]
 (wheel.hoplon.stylesheet.dom/link
  (wheel.font.google.core/fonts->url fonts)))
