(ns system-message.state
 (:require
  [hoplon.core :as h]
  [javelin.core :as j]
  wheel.datascript.javelin
  wheel.system-message.data
  test.util
  [clojure.spec.alpha :as spec]
  [cljs.test :refer-macros [deftest is]]))

(defn messages-cell []
 (j/cell (sorted-map)))
(def messages (memoize messages-cell))

(defn +!
 ([messages body vibe]
  (+! messages {:system-message/body body :system-message/vibe vibe}))
 ([messages message]
  {:pre [(j/cell? messages)
         (spec/valid? :system-message/messages @messages)]
   :post [(spec/valid? :system-message/id %)]}
  (let [id (or
            (:system-message/id message)
            ; we want our messages to retain their insertion order
            (d/squuid))
        v (merge
           {:system-message/seen false
            :system-message/id id}
           message)]
   (assert (spec/valid? :system-message/message v))
   (swap! messages assoc id v)
   id)))

(defn not-seen
 [messages]
 {:post [(spec/valid? :system-message/messages %)]}
 (apply
  dissoc
  messages
  (map :system-message/id
   (filter :system-message/seen
    (vals messages)))))
(defn not-seen= [messages] (j/cell= (not-seen messages)))

(defn error!
 ([body] (error! (messages) body))
 ([messages body]
  {:pre [(j/cell? messages)
         (spec/valid? :system-message/body body)]}
  (+! messages {:system-message/body body
                :system-message/vibe :bad})))

(defn seen!
 [messages id]
 (swap! messages assoc-in [id :system-message/seen] true))

; TESTS

(deftest ??+!
 (let [ms (messages-cell)
       n (not-seen= ms)
       m (dissoc (wheel.test.util/fake :system-message/message) :system-message/seen)
       k (+! ms m)
       e (merge m {:system-message/seen false
                   :system-message/id k})]
  (is (= {k e} @ms @n))
  (seen! ms k)
  (is (= {k (merge e {:system-message/seen true})}
         @ms))
  (is (= {} @n))))
