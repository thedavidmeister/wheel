; Datascript/Javelin interop.
(ns wheel.datascript.javelin
 (:require
  wheel.datascript.spec
  [datascript.core :as d]
  [javelin.core :as j]
  [clojure.spec.alpha :as spec]
  [cljs.test :refer-macros [deftest is]]))

(defn conn-cell? [c]
 (and
  (d/conn? c)
  (j/cell? c)))

(defn- conn-cell-from-db
 "Mimics datascript conn-from-db but builds a compatible javelin cell"
 [db]
 {:pre   [(d/db? db)]
  :post  [(conn-cell? %)]}
 (j/cell db :meta { :listeners (atom {})}))

(defn conn-cell
 "Builds a fresh conn cell wrapping an empty db"
 ([]
  (conn-cell {}))
 ([schema]
  {:pre [(map? schema)]
   :post [(conn-cell? %) (= {} (-> % meta :listeners deref))]}
  (conn-cell-from-db (d/empty-db schema))))

(defn conn-cell-with
  [conn tx]
  {:pre   [(d/conn? conn) (coll? tx)]
   :post  [(conn-cell? %)]}
  (conn-cell-from-db
    (d/db-with @conn tx)))

(defn conn-from-datoms
  ([datoms] (conn-from-datoms datoms {}))
  ([datoms schema]
   (conn-cell-with (conn-cell schema) (or datoms []))))

; TESTS

(deftest ??conn-cell?
 (is (not (conn-cell? (j/cell nil))))
 (is (not (conn-cell? (d/empty-db))))
 (is (not (conn-cell? (d/create-conn {}))))

 (is (conn-cell? (j/cell (d/empty-db {}))))
 (is (conn-cell? (conn-cell))))

(deftest ??conn-cell
 ; create a conn as a javelin cell
 (let [conn (conn-cell)]
  ; Smoke test a very basic query for exceptions.
  (is (= #{} (d/q '[:find ?id :where [?id :data _]] @conn))))

 ; add a listener
 (let [c (j/cell nil)
       conn (conn-cell)]
  (d/listen! conn ::foo (partial reset! c))
  (d/transact! conn [{:foo :bar}])
  (is (spec/valid? :wheel.datascript/tx-report @c))
  (is (= @conn (:db-after @c)))))

(deftest ??conn-cell-with
 "Test that we can return a conn cell with tx applied"
 (let [conn (conn-cell)
       tx [{:foo :bar}]
       conn-with (conn-cell-with conn tx)
       q (fn [conn] (d/q '[:find ?foo :where [?id :foo ?foo]] @conn))]
  (is (= #{} (q conn)))
  (is (= #{[:bar]} (q conn-with)))))
