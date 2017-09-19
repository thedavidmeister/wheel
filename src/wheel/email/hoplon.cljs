(ns wheel.email.hoplon
 (:require
  [hoplon.core :as h]
  [javelin.core :as j]
  [clojure.spec.alpha :as spec]
  wheel.email.spec
  wheel.dom.traversal
  [cljs.test :refer-macros [deftest is]]))

(h/defelem email
 [{:keys [address subject body]} children]
 (let [children (if (seq children) children address)]
  (h/a
   :href (j/cell= (str "mailto:" address "?subject=" subject "&body=" body))
   :class #{"email"}
   :css {:display "inline-block"}
   :target "_blank"
   :data-invalid-address (j/cell= (not (spec/valid? :wheel.email/email address)))
   children)))

; TESTS

(deftest ??email
 (let [example-email "foo@example.com"
       a (j/cell example-email)
       s (j/cell "Emails have subject lines")
       b (j/cell "This is the body of the email.")
       c (j/cell example-email)
       el (email
           :address a
           :subject s
           :body b
           c)]
  (is (wheel.dom.traversal/is? el "a.email"))
  (is (wheel.dom.traversal/is? el (str "a[href=\"mailto:" example-email "?subject=Emails have subject lines&body=This is the body of the email.\"] ")))
  (is (= @c (wheel.dom.traversal/text el)))
  (reset! a "as;lkfj")
  (reset! s "as;lkfj")
  (reset! b "as;lkfj")
  (reset! c "as;lkfj")
  (is (wheel.dom.traversal/is? el (str "a[href=\"mailto:as;lkfj?subject=as;lkfj&body=as;lkfj\"]")))
  (is (= @c (wheel.dom.traversal/text el)))))

(deftest ??email--invalid
 (let [valid "foo@example.com"
       invalid "foo"
       a (j/cell valid)
       el (email
           :address a)]
  (is (not (wheel.dom.traversal/is? el "[data-invalid-address]")))
  (reset! a invalid)
  (is (wheel.dom.traversal/is? el "[data-invalid-address]"))))
