(ns wheel.dom.traversal
 (:refer-clojure :exclude [find contains? exists? val])
 (:require cljsjs.jquery
  wheel.dom.events))

(defn is?
 [el sel]
 (-> el js/jQuery (.is sel)))

(defn find
 [el sel]
 (-> el js/jQuery (.find sel) array-seq))

(defn contains?
 [el sel]
 (some? (find el sel)))

(defn children
  [el]
  (-> el js/jQuery .children array-seq))

(defn exists?
  [el sel]
  (< 0 (count (find el sel))))

(defn contains-attrs?
  [el attrs vals]
  {:post [(boolean? %)]}
  (cond
    (not (coll? attrs))
    (contains-attrs? el [attrs] vals)

    (not (coll? vals))
    (contains-attrs? el attrs [vals])

    :else
      (every? true?
        (for [attr attrs val vals]
          (some?
            (find el (str "[" (name attr ) "=" val "]")))))))

(defn attr
  [el attr-name]
  (-> el js/jQuery (.attr attr-name)))

(defn find-attr
  [el sel attr-name]
  {:post [(seq? %)]}
  (map #(attr % attr-name) (find el sel)))

(defn text
  [el]
  (-> el js/jQuery .text))

(defn find-text
  [el sel]
  {:post [(seq? %)]}
  (map text (find el sel)))

(defn val
 [el]
 (-> el js/jQuery .val))

(defn find-val
 [el sel]
 {:post [(seq? %)]}
 (map val (find el sel)))

(defn input-val!
 "Sets the val of el to the given v, but also triggers input, which is often necessary for tests."
 [el v]
 (-> el js/jQuery (.val v) (.trigger "input")))

(defn find-fn-input-val!
 ([el f v] (find-fn-input-val! el "input" f v))
 ([el sel f v]
  (let [target (-> el js/jQuery (find sel) f)]
   (assert target)
   (input-val! target v))))

(defn input-val-first!
 ([el v] (find-fn-input-val! el first v))
 ([el sel v] (find-fn-input-val! el sel first v)))

(defn css
  [el k]
  (-> el js/jQuery (.css k)))

(defn trigger-first!
 "Use jQuery to trigger the given event on the first match of sel"
 [el sel e]
 (let [target (-> el (find sel) first)]
  (assert target)
  (wheel.dom.events/trigger-jq! target e)))
