(ns wheel.math.random
 (:require
  xoroshiro128.core
  medley.core
  [clojure.test :refer [deftest is are]]))

(defn rand-seq
 "Generate a lazy, seedable sequence of uniformly distributed longs."
 ([] (rand-seq (xoroshiro128.core/rand)))
 ([seed]
  {:post [(sequential? %)]}
  (map
   xoroshiro128.core/value
   (iterate
    xoroshiro128.core/next
    (xoroshiro128.core/xoroshiro128+ seed)))))

; TESTS

(deftest ??rand-seq--seed
 ; Using the same seed should produce the same sequence.
 ; Using a different seed should produce a different sequence.
 (let [seed-1 (medley.core/random-uuid)
       seed-2 (medley.core/random-uuid)
       sample-size 1000]

  (is (= (take sample-size (rand-seq seed-1))
         (take sample-size (rand-seq seed-1))))

  (is (= (take sample-size (rand-seq seed-2))
         (take sample-size (rand-seq seed-2))))

  (is (not (= (take sample-size (rand-seq seed-1))
              (take sample-size (rand-seq seed-2)))))))
