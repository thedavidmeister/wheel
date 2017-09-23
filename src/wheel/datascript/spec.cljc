(ns datascript.spec
 (:require
  datascript.db
  [clojure.spec.alpha :as spec]))

(spec/def :wheel.datascript/tx-report
 #(instance? datascript.db/TxReport %))
