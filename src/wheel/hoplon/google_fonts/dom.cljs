(ns wheel.hoplon.google-fonts.dom
 (:require
  [hoplon.core :as h]
  wheel.hoplon.stylesheet.dom
  wheel.hoplon.google-fonts.api))

(defn link [fonts]
 (wheel.hoplon.stylesheet.dom/link
  (wheel.hoplon.google-fonts.api/fonts->url fonts)))
