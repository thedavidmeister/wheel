(ns wheel.javelin.task-runner-cell
 (:require
  [hoplon.core :as h]
  [javelin.core :as j]
  time.core))

(defn task-runner-cell
 ([] (task-runner-cell 30))
 ([ms]
  (let [c (j/cell cljs.core.PersistentQueue.EMPTY)
        t (atom nil)
        pause-until (atom 0)
        process-item! (fn [[f & args]] (apply f args))
        process-timeout! (fn process-timeout!
                          []
                          (let [paused? (< (time.core/now-millis) @pause-until)]
                           ; there is something to process
                           (when (seq @c)
                            ; a timeout is active
                            (when (not paused?)
                             (j/dosync
                              (process-item! (first @c))
                              (swap! c pop)))
                            (.clearTimeout js/window @t)
                            (reset! t
                             (h/with-timeout ms
                              (process-timeout!))))))]
   (j/with-let [_ (j/cell= c #(swap! c conj %))]
    (add-watch c (gensym)
     (fn []
      ; debounce timeout processing by pushing out the pause when c changes
      (reset! pause-until (+ (time.core/now-millis) ms))
      (process-timeout!)))))))

(defn -named-task-runner
 ([n] (task-runner-cell))
 ([n ms] (task-runner-cell ms)))
(def named-task-runner (memoize -named-task-runner))
