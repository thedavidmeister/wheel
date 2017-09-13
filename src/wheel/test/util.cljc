(ns wheel.test.util
 (:require
  clojure.test.check.generators
  [clojure.spec.alpha :as spec]))

(def seen (atom #{}))

(defn fake
 "Generates a single value based on passed spec and options.
  Allowed options:
  :merge will be merged into the generated data, overriding if needed.
  :unique? set to true to ensure that the generated return value is unique. Can
  result in an infinite or very long loop if finding a unique value becomes too
  difficult based on the spec. Set a large size to increase chances of finding
  a unique value.
  :size works as per clojure.test.check.generators/generate"
 [k & {:keys [merge unique? size]}]
 ; https://github.com/thedavidmeister/estimate-work/issues/2342
 ; {:post [(or (spec/valid? k %) (prn %) (spec/explain k %))]}
 (let [size (or size 50)
       v (clojure.test.check.generators/generate
          (if (clojure.test.check.generators/generator? k)
           k
           (clojure.spec.alpha/gen k))
          size)
       ret (if merge (clojure.core/merge v merge) v)
       unique? (if (nil? unique?) true unique?)
       seen? (@seen ret)]
  (swap! seen conj ret)
  (if (and seen? unique?)
   ; try again to find a unique return.
   (fake k :merge merge :unique? unique?)
   ret)))
