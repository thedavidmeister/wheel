; Datascript/Javelin interop.
(ns wheel.datascript.javelin
 (:require
  [datascript.core :as d]
  [javelin.core :as j]
  [cljs.test :refer-macros [deftest is]]))

(defn- conn-cell-from-db
  "Mimics datascript conn-from-db but builds a compatible javelin cell"
  [db]
  {:pre   [(d/db? db)]
   :post  [(d/conn? %) (j/cell? %)]}
  (j/cell db :meta { :listeners (atom {})}))

(defn conn-cell
  "Builds a fresh conn cell wrapping an empty db"
  ([]
   (conn-cell {}))
  ([schema]
   {:pre [(map? schema)]
    :post [(d/conn? %) (j/cell? %) (= {} (-> % meta :listeners deref))]}
   (conn-cell-from-db (d/empty-db schema))))

(defn conn-cell-with
  [conn tx]
  {:pre   [(d/conn? conn) (coll? tx)]
   :post  [(d/conn? %) (j/cell? %)]}
  (conn-cell-from-db
    (d/db-with @conn tx)))

(defn conn-from-datoms
  ([datoms] (conn-from-datoms datoms {}))
  ([datoms schema]
   (conn-cell-with (conn-cell schema) (or datoms []))))
