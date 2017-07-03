(ns wheel.json.core)

(defn parse
 [s]
 {:pre [(string? s)]}
 (.parse js/JSON s))
