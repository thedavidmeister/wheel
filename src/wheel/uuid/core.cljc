(ns wheel.uuid.core
 #?(:cljs (:refer-clojure :exclude [random-uuid])))

(defn random-uuid []
 #?(:cljs (clojure.core/random-uuid)
    :clj (java.util.UUID/randomUUID)))
