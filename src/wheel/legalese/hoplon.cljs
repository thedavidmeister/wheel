(ns wheel.legalese.hoplon
 (:require
  [hoplon.core :as h]
  [clojure.spec.alpha :as spec]
  [cljs.test :refer-macros [deftest is]]
  wheel.dom.traversal))

(defn legislature
 [name]
 (h/em
  :class "legislature"
  name))

(defn clause-list
  "A list of clauses with preamble and optional postamble."
 ([pre items] (clause-list pre items nil))
 ([pre items post]
  {:pre [(string? pre)
         (sequential? items)
         (spec/valid? (spec/nilable string?) post)]}
  (h/section
   :class "clause-list"
   (h/span
    :class "preamble"
    pre)
   (h/ul
    (map
     (partial h/li :class "clause")
     items))
   (when post
    (h/span
     :class "postamble"
     post)))))

; Clause list.

(deftest ??legislature
 (let [n "foooos"
       el (legislature n)]
  (is (wheel.dom.traversal/is? el "em.legislature"))
  (is (= n (wheel.dom.traversal/text el)))))

(deftest ??clause-list
  (let [pre "foo"
        items ["one" "two" "three"]
        post "bar"
        without-post (clause-list pre items)
        with-post (clause-list pre items post)]
    (is (wheel.dom.traversal/is? without-post "section.clause-list"))
    (is (wheel.dom.traversal/is? with-post "section.clause-list"))

    (is (= ["foo"]
         (wheel.dom.traversal/find-text without-post "span.preamble")))
    (is (= ["foo"]
         (wheel.dom.traversal/find-text with-post "span.preamble")))

    (is (= ["one" "two" "three"]
         (wheel.dom.traversal/find-text without-post "li.clause")))
    (is (= ["one" "two" "three"]
         (wheel.dom.traversal/find-text with-post "li.clause")))

    (is (= []
         (wheel.dom.traversal/find-text without-post "span.postamble")))
    (is (= ["bar"]
         (wheel.dom.traversal/find-text with-post "span.postamble")))))
