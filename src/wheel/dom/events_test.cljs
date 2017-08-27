(ns wheel.dom.events-test
 (:require
  wheel.dom.events
  wheel.dom.traversal
  [hoplon.core :as h]
  [javelin.core :as j]
  [cljs.test :refer-macros [deftest is]]))

; TESTS

(deftest ??events-set-get-data
 (let [result (j/cell nil)
       inner (h/div :input #(wheel.dom.events/set-data! % :foo :bar))
       dom (h/div
            :input #(reset! result (wheel.dom.events/get-data % :foo))
            inner)]
  (is (nil? @result))
  (wheel.dom.events/trigger-jq! inner "input")
  (is (= :bar @result))))

(deftest ??trigger-native!
 ; direct on an el
 (let [c (j/cell nil)
       el (h/div :click #(reset! c true))]
  (is (not @c))
  (wheel.dom.events/trigger-native! el "click")
  (is @c))

 ; navigate to a nested el
 (let [c (j/cell nil)
       el (h/div (h/span :click #(reset! c true)))]
  (is (not @c))
  (-> el
   (wheel.dom.traversal/find "span")
   first
   (wheel.dom.events/trigger-native! "click"))
  (is @c)))
