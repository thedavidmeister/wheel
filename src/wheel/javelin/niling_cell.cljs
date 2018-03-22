(ns wheel.javelin.niling-cell
 (:require
  [javelin.core :as j]
  [hoplon.core :as h]))

(defn niling-cell
 ([] (niling-cell 0))
 ([ms]
  (j/with-let [c (j/cell nil)]
   (let [t (atom nil)]
    (h/do-watch c
     (fn [_ _]
      (.clearTimeout js/window @t)
      (reset! t (h/with-timeout ms (reset! c nil)))))))))
