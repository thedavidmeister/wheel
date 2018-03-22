(ns wheel.javelin.throttle-cell
 (:require
  [javelin.core :as j]
  [hoplon.core :as h]
  [cljs.test :refer-macros [deftest is async]]))

(defn throttle-cell
 "Returns a javelin cell that will only update its value at most once per X ms"
 ([v ms] (throttle-cell v ms (j/cell false)))
 ([v ms locked?]
  {:pre [(number? ms) (j/cell? locked?)]}
  (let [c (j/cell v)
        lock! #(reset! locked? true)
        unlock! #(reset! locked? false)]
   ; Set the lock whenever c changes.
   (add-watch c (gensym) lock!)
   ; Unlock automatically after ms.
   (add-watch locked? (gensym) (fn [_ _ _ n] (when n (h/with-timeout ms (unlock!)))))
   ; Return the throttled cell.
   (j/cell= c #(when-not @locked? (reset! c %))))))

; TESTS

(deftest ??throttle-cell
 (async done
  (let [c (throttle-cell 1 20)]
   (is (= 1 @c))

   ; Should be able to inc c as it is not locked on init.
   (swap! c inc)
   (is (= 2 @c))

   ; Should not be able to inc c a second time as it is locked now.
   (swap! c inc)
   (is (= 2 @c))

   ; After the timeout, should be able to inc c.
   (h/with-timeout 50
    (do
     (swap! c inc)
     (is (= 3 @c))
     (done))))))
