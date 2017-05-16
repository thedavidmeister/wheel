(ns wheel.stylesheet.hoplon
 (:require
  medley.core
  [hoplon.core :as h]
  [cljs.test :refer-macros [deftest is]]))

(defn link [href]
 "Given an href returns a link with the correct attributes for a stylesheet"
 (h/link
  :href href
  :rel "stylesheet"
  :type "text/css"))

; TESTS

(deftest ??link
 (let [href (str (medley.core/random-uuid))]
  (is (= (.-outerHTML (link href))
         (str "<link href=\"" href "\" rel=\"stylesheet\" type=\"text/css\">")))))
