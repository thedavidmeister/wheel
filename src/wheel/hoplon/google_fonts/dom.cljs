(ns wheel.hoplon.google-fonts.dom
 (:require
  [hoplon.core :as h]
  wheel.hoplon.stylesheet.dom
  wheel.hoplon.google-fonts.api))

(defn link
 "Given a sequence of fonts returns the stylesheet link to Google Fonts"
 [fonts]
 (wheel.hoplon.stylesheet.dom/link
  (wheel.hoplon.google-fonts.api/fonts->url fonts)))
