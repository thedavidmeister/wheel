(ns wheel.device.size
 (:require
  [javelin.core :as j]
  [hoplon.core :as h]))

(defn dimensions
 [dom-element]
 {:height (.-innerHeight dom-element)
  :width  (.-innerWidth dom-element)})

(defn -window-size-cell
 []
 (let [c (j/cell (dimensions js/window))]
  (j/with-let [_ (j/cell= c)]
   (js/window
    :resize #(reset! c (dimensions js/window))))))
(def window-size-cell (memoize -window-size-cell))
