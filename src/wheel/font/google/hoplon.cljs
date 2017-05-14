(ns wheel.font.google.hoplon
 (:require
  [hoplon.core :as h]
  wheel.stylesheet.hoplon
  wheel.font.google.core))

(defn link
 "Given a sequence of fonts returns the stylesheet link to Google Fonts"
 [fonts]
 (wheel.stylesheet.hoplon/link
  (wheel.font.google.core/fonts->url fonts)))
