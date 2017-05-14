(ns wheel.hoplon.stylesheet.dom
 (:require
  [hoplon.core :as h]
  [cljs.test :refer-macros [deftest is]]))

(defn link [href]
 (h/link
  :href href
  :rel "stylesheet"
  :type "text/css"))

; TESTS

(deftest ??link
 (let [href (str (random-uuid))]
  (is (= (.-outerHTML (link href))
         (str "<link href=\"" href "\" rel=\"stylesheet\" type=\"text/css\">")))))
