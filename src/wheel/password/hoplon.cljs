(ns wheel.password.hoplon
 (:require
  [hoplon.core :as h]
  [javelin.core :as j]
  zxcvbn.lib
  wheel.form.input.hoplon
  wheel.dom.traversal
  [cljs.test :refer-macros [deftest is are]]))

(defn zxcvbn
 ([password] (zxcvbn password nil))
 ([password user-inputs]
  {:pre [(string? password)]}
  (js->clj
   (.zxcvbn js/window password (clj->js user-inputs)))))

(defn suggestions
 ([value] (suggestions value nil))
 ([value user-inputs]
  (let [results (j/cell nil)
        warning (j/cell= (get-in results ["feedback" "warning"]))
        ss (j/cell= (get-in results ["feedback" "suggestions"]))
        good-password? (j/cell= (and (= "" warning) (not (seq ss))))]
   (h/do-watch value #(reset! results (zxcvbn %2 user-inputs)))

   (h/div
    :class "password-suggestions"
    (h/if-tpl good-password?
     (h/span "This password looks good 👍")
     (h/div
      (h/span
       :class "warning"
       (j/cell= (when (not (= "" warning))
                 (str "⚠️️ " warning))))
      (h/when-tpl ss
       (h/ul
        :class "suggestions"
        (h/for-tpl [s ss]
         (h/li
          :class "suggestion"
          s))))))))))

(h/defelem input
 [attributes children]
 (let [mask-password? (j/cell true)
       attributes (merge {:value (j/cell "") :label "Password:"} attributes)]
  (wheel.form.input.hoplon/input
   :name "password"
   :type (j/cell= (if mask-password? "password" "text"))
   (dissoc attributes :user-inputs)

   (h/a
    :click #(swap! mask-password? not)
    (j/cell= (str (if mask-password? "Show" "Hide") " password")))

   (suggestions (:value attributes) (:user-inputs attributes))
   children)))

; TESTS.

(def ?zxcvbn-examples
 [["" "" ["Use a few words, avoid common phrases" "No need for symbols, digits, or uppercase letters"]]
  ["foo" "" ["Add another word or two. Uncommon words are better."]]
  ["correct horse battery staple" "" []]
  ["aaa" "Repeats like \"aaa\" are easy to guess" ["Add another word or two. Uncommon words are better." "Avoid repeated words and characters"]]])

(deftest ??zxcvbn
 (doseq [[p w s] ?zxcvbn-examples]
  (= {"warning" w "suggestions" s}
   (get (zxcvbn p) "feedback"))))

(deftest ??suggestions
 (let [v (j/cell "")
       el (suggestions v)]
  (is (wheel.dom.traversal/is? el ".password-suggestions"))

  (doseq [[p w s] ?zxcvbn-examples]
   (reset! v p)
   (if (and (= "" w) (= [] s))
    (is (wheel.dom.traversal/text el "This password looks good 👍"))
    (do
     (is (= [(str (when (not (= "" w)) "⚠️️ ") w) (wheel.dom.traversal/find-text el ".warning")]))
     (is (= s (wheel.dom.traversal/find-text el ".suggestion"))))))))

(deftest ??input
 (let [v (j/cell "")
       el (input :value v)]
  (is (wheel.dom.traversal/is? el ".input--password"))
  (is (= ["Password:"] (wheel.dom.traversal/find-text el ".raw-label")))
  (is (= (-> ?zxcvbn-examples first last) (wheel.dom.traversal/find-text el ".suggestion")))

  ; Toggle masking.
  (is (= ["password"] (wheel.dom.traversal/find-attr el "input" "type")))
  (is (= ["Show password"] (wheel.dom.traversal/find-text el "a")))

  (wheel.dom.traversal/trigger-first! el "a" "click")
  (is (= ["text"] (wheel.dom.traversal/find-attr el "input" "type")))
  (is (= ["Hide password"] (wheel.dom.traversal/find-text el "a")))

  (wheel.dom.traversal/trigger-first! el "a" "click")
  (is (= ["password"] (wheel.dom.traversal/find-attr el "input" "type")))
  (is (= ["Show password"] (wheel.dom.traversal/find-text el "a")))

  ; user input => warning + suggestions
  (let [[p w s] (last ?zxcvbn-examples)]
   (wheel.dom.traversal/input-val-first! el p)
   (is (= p @v))
   (is (= [(str "⚠️️ " w)] (wheel.dom.traversal/find-text el ".warning")))
   (is (= s (wheel.dom.traversal/find-text el ".suggestion"))))))
