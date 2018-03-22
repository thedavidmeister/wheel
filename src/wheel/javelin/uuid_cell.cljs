(ns wheel.javelin.uuid-cell
 (:require
  [javelin.core :as j]
  medley.core
  [cljs.spec.alpha :as spec]
  [cljs.test :refer-macros [deftest is]]))

(defn uuid-cell?
 [c]
 (and
  (j/cell? c)
  (::uuid-cell (meta c))
  (spec/valid? (spec/nilable uuid?) @c)))

(defn uuid-cell
 "returns a lens cell that generates a new UUID whenever it or any watched cells are updated to a truthy value
  setting the lens cell directly to a falsey value will set it to nil
  setting _all_ of the watched cells to a falesy value will set the lens to nil
  the lens inits to nil"
 [& alts!]
 (let [c (j/cell nil)
       new-c! (fn [v]
               (if v
                (if (medley.core/uuid? v)
                 (reset! c v)
                 (swap! c medley.core/random-uuid))
                (reset! c nil)))]
  (j/cell= (assert (spec/valid? (spec/nilable uuid?) c)))
  (j/with-let [u (j/cell= c new-c!)]
   (alter-meta! u assoc ::uuid-cell ::uuid-cell)
   (add-watch
    (apply javelin.core/alts! alts!)
    (gensym)
    (fn [_ _ _ n]
     (new-c! (some identity n)))))))

; TESTS

(deftest ??uuid-cell?
 (is (not (uuid-cell? (j/cell nil))))
 (is (uuid-cell? (uuid-cell)))
 (is (uuid-cell? (uuid-cell (j/cell nil)))))

(deftest ??uuid-cell
 (let [u (uuid-cell)
       a @u]
  ; initial value should be nil
  (is (nil? a))

  ; setting a truthy value should roll the uuid
  (reset! u true)
  (is (not (= a @u)))
  (is (medley.core/uuid? @u))

  ; setting u a second time to the same truthy value should still gen a new uuid
  (let [b @u]
   (reset! u true)
   (is (not (= a b @u)))
   (is (medley.core/uuid? @u))

   ; setting a falsey value should set to nil
   (let [c @u]
    (reset! u false)
    (is (nil? @u)))))

 (let [x (j/cell nil)
       y (j/cell nil)
       u (uuid-cell x y)
       a @u]
  ; initial value should be nil
  (is (nil? a))

  ; setting a truthy value for x should roll the uuid
  (reset! x true)
  (is (not (= a @u)))
  (is (medley.core/uuid? @u))

  ; setting a truthy value for y should roll the uuid
  (let [b @u]
   (reset! x false)
   (is (nil? @u))

   (reset! y true)
   (is (not (= b @u)))
   (is (medley.core/uuid? @u))

   ; setting a truthy value for u should roll the uuid
   (let [c @u]
    (reset! u false)
    (is (nil? @u))

    (reset! u true)
    (is (not (= c @u)))
    (is (medley.core/uuid? @u)))))

 ; setting the same truthy value to x twice should roll the uuid-cell twice
 (let [x (j/cell nil)
       u (uuid-cell x)
       a @u]
  (is (nil? a))

  (reset! x true)
  (is (not (= a @u)))
  (is (medley.core/uuid? @u))))

  ; https://github.com/thedavidmeister/estimate-work/issues/3125
  ; (let [b @u]
  ;  (reset! x true)
  ;  (is (not (= b @u)))
  ;  (is (medley.core/uuid? @u)))))
