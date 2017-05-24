(ns wheel.clearfix.hoplon
 (:require
  [hoplon.core :as h]))

; Using a real DOM element should work as well as the CSS pseudo-elements, but
; without the overhead of bundling styles around everywhere.
; https://stackoverflow.com/questions/211383/what-methods-of-clearfix-can-i-use?rq=1
; https://codepen.io/anon/pen/wdRvPR

(defn clearfix [] (h/div :css {:clear "both"}))
