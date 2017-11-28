(ns wheel.dom.traversal
 (:refer-clojure :exclude [find contains? exists? val])
 (:require
  hoplon.jquery
  wheel.dom.events
  goog.dom
  oops.core
  wheel.dom.data
  [hoplon.core :as h]
  [cljs.test :refer-macros [deftest is]]))

(defn is?
 [el sel]
 {:pre [(wheel.dom.data/el? el)]}
 ; http://youmightnotneedjquery.com/#matches_selector
 (let [possible-methods ["matches" "matchesSelector" "msMatchesSelector" "mozMatchesSelector" "webkitMatchesSelector" "oMatchesSelector"]
       matches (some
                #(when (oops.core/oget+ el (str "?" %)) %)
                possible-methods)]
  (oops.core/ocall+ el matches sel)))

(defn find
 [el sel]
 {:pre [(wheel.dom.data/el? el)
        (wheel.dom.data/sel? sel)]}
 (array-seq
  (.querySelectorAll el sel)))

(defn contains?
 [el el-or-sel]
 {:pre [(wheel.dom.data/el? el)
        (wheel.dom.data/el-or-sel? el-or-sel)]}
 (if (wheel.dom.data/el? el-or-sel)
  (and
   (not (= el el-or-sel))
   (goog.dom/contains el el-or-sel))
  (some? (find el el-or-sel))))

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
     (find el (str "[" (name attr ) "=\"" val "\"]")))))))

(defn attr
 [el attr-name]
 (-> el js/jQuery (.attr attr-name)))

(defn find-attr
 [el sel attr-name]
 {:post [(seq? %)]}
 (map #(attr % attr-name) (find el sel)))

(defn text
 [el]
 {:pre [(wheel.dom.data/el? el)]}
 (.-textContent el))

(defn find-text
 [el sel]
 {:pre [(wheel.dom.data/el? el)
        (wheel.dom.data/sel? sel)]
  :post [(seq? %)]}
 (map text (find el sel)))

(defn val
 [el]
 (-> el js/jQuery .val))

(defn find-val
 [el sel]
 {:pre [(wheel.dom.data/el? el)
        (wheel.dom.data/sel? sel)]
  :post [(seq? %)]}
 (map val (find el sel)))

(defn input-val!
 "Sets the val of el to the given v, but also triggers input, which is often necessary for tests."
 [el v]
 (-> el js/jQuery (.val v) (.trigger "input")))

(defn find-fn-input-val!
 ([el f v] (find-fn-input-val! el "input" f v))
 ([el sel f v]
  (let [target (-> el (find sel) f)]
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
  (assert target (str "Cannot find target " sel " in " el " to trigger " e))
  (wheel.dom.events/trigger-jq! target e)))

; TESTS

(deftest ??contains-attrs?
 (doseq [v [; Basic
            "bar"
            ; Need to be able to handle vals with spaces in them. Some versions
            ; of jQuery choke on this if not escaped correctly.
            "bar baz"]]
  (is
   (contains-attrs?
    (h/div (h/div :data-foo v))
    :data-foo
    v))))

(deftest ??find-text
 (let [el (h/div
           (h/span "foo")
           (h/span "bar")
           (h/p "baz"))]
  (is (= ["foo" "bar"]
       (find-text el "span")))))

(deftest ??input-val-first!
 (let [i1 (h/input)
       i2 (h/input)
       el (h/form i1 i2)]
  (is (= ["" ""] (find-val el "input")))
  (input-val-first! el "foo")
  (is (= ["foo" ""] (find-val el "input")))))
