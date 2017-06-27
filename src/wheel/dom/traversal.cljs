(ns wheel.dom.traversal
 (:refer-clojure :exclude [find contains? exists? val])
 (:require
  hoplon.jquery
  wheel.dom.events
  [hoplon.core :as h]
  oops.core
  goog.dom
  [cljs.test :refer-macros [deftest is]]))

(defn el? [el] (goog.dom/isElement el))

(defn sel? [sel] (string? sel))

(defn is?
 [el sel]
 {:pre [(el? el)]}
 ; http://youmightnotneedjquery.com/#matches_selector
 (let [possible-methods ["matches" "matchesSelector" "msMatchesSelector" "mozMatchesSelector" "webkitMatchesSelector" "oMatchesSelector"]
       matches (some
                #(when (oops.core/oget+ el (str "?" %)) %)
                possible-methods)]
  (oops.core/ocall+ el matches sel)))

(defn find
 [el sel]
 {:pre [(el? el) (sel? sel)]}
 (array-seq
  (.querySelectorAll el sel)))

(defn contains?
 [el el-or-sel]
 {:pre [(el? el) (or (sel? el-or-sel)
                     (el? el-or-sel))]}
 (if (el? el-or-sel)
  (and
   (not (= el el-or-sel))
   (goog.dom/contains el el-or-sel))
  (some? (find el el-or-sel))))

(defn children
 [el]
 {:pre [(el? el)]}
 (array-seq
  (.-children el)))

(def exists? contains?)

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
 {:pre [(el? el) (or (string? attr-name) (keyword? attr-name))]}
 (.getAttribute el (name attr-name)))

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

; TESTS

(deftest ??el?
 (is (el? (h/div)))
 (is (not (el? "div"))))

(deftest ??is?
 (is (is? (h/div) "div"))
 (is (not (is? (h/div) "span"))))

(deftest ??find
 (let [child-1 (h/div)
       child-2 (h/div)
       el (h/div child-1 child-2)]
  (is (= [child-1 child-2] (find el "div")))
  (is (= nil (find (h/div) "div")))))

(deftest ??contains?
 (let [child (h/div)
       el (h/div child)]
  (is (contains? el child))
  (is (contains? el "div"))
  (is (not (contains? el el)))))

(deftest ??children
 (let [child-1 (h/div)
       child-2 (h/div)
       el (h/div child-1 child-2)]
  (is (= [child-1 child-2] (children el)))
  (is (= nil (children (h/div))))))

(deftest ??attr
 (is (= "bar" (attr (h/div :foo "bar") "foo")))
 (is (= "bar" (attr (h/div :foo "bar") :foo)))
 
 (is (= nil (attr (h/div :foo "bar") "baz")))
 (is (= nil (attr (h/div :foo "bar") :baz))))

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
