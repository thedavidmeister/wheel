(ns wheel.javelin.debounce-cell
 (:require
  [javelin.core :as j]
  [hoplon.core :as h]
  wheel.env.data
  [cljs.test :refer-macros [deftest is]]))

(defn debounce-cell?
 [c]
 (and
  (j/cell? c)
  (::debounce-cell-ms (meta c))))

(defn debounce-cell
 "debounce a non-debounce cell"
 [c ms]
 {:pre [(j/cell? c)
        ; debouncing an already-debounced cell is almost certainly a mistake
        (not (debounce-cell? c))
        (number? ms)]
  :post [(debounce-cell? %)]}
 (j/with-let [r (if env.data/testing?
                 ; don't want to deal with debounce making almost everything async on CI.
                 (j/cell= c)
                 (let [t (j/cell nil)]
                  (j/with-let [d (j/cell @c)]
                   (add-watch c (gensym)
                    (fn [_ _ _ n]
                     (.clearTimeout js/window @t)
                     (reset! t
                      (h/with-timeout ms
                       (reset! d n))))))))]
  (alter-meta! r assoc ::debounce-cell-ms ms)))

(defn safe-debounce-cell
 "debounce a cell, or return an already debounced cell if ms is compatible"
 [c ms]
 {:post [(debounce-cell? %)]}
 (cond
  (not (debounce-cell? c))
  (debounce-cell c ms)

  (= ms (::debounce-cell-ms (meta c)))
  c

  :else
  (throw (js/Error. "Attempted to debounce a debounce cell with incompatible ms."))))

; TESTS

(deftest ??debounce-cell?
 (let [c (j/cell nil)
       ms (rand-int 1000)
       d (debounce-cell c ms)]
  (is (not (debounce-cell? c)))
  (is (= ms (::debounce-cell-ms (meta d))))
  (is (debounce-cell? d))))

(deftest ??safe-debounce-cell
 (let [c (j/cell nil)
       ms (rand-int 1000)
       d (safe-debounce-cell c ms)]
  (is (debounce-cell? d))
  (is (identical? d (safe-debounce-cell d ms)))))
