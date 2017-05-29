(ns wheel.math.random
 (:require
  xoroshiro128.core
  medley.core
  wheel.math.distribution.triangular
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

(defn rand-seq->0:1
  "Maps a sequence of uniformly distributed longs to uniformly distributed floats in the interval [0, 1]."
  [rands]
  {:pre [(sequential? rands)]
   :post [(sequential? %)]}
  (let [long->1 (fn [l] (Math/abs (/ (double l) (double Long/MAX_VALUE))))]
   (map long->1 rands)))

(defn rand-seq->triangular
 "Maps a sequence of uniformly distributed longs to triangularly distributed floats around min, max and mode."
 [rands min mode max]
 {:pre [(sequential? rands)]
  :post [(sequential? %)]}
 (let [inv-cdf-fn (wheel.math.distribution.triangular/inverse-cdf-fn min mode max)
       scaled (rand-seq->0:1 rands)]
  (map inv-cdf-fn scaled)))

; TESTS

(def sample-size 1000)

(deftest ??rand-seq--seed
 ; Using the same seed should produce the same sequence.
 ; Using a different seed should produce a different sequence.
 (let [seed-1 (medley.core/random-uuid)
       seed-2 (medley.core/random-uuid)]

  (is (= (take sample-size (rand-seq seed-1))
         (take sample-size (rand-seq seed-1))))

  (is (= (take sample-size (rand-seq seed-2))
         (take sample-size (rand-seq seed-2))))

  (is (not (= (take sample-size (rand-seq seed-1))
              (take sample-size (rand-seq seed-2)))))))

(deftest ??rand-seq->0:1--bounds
 (let [rands (rand-seq)
       scaled (rand-seq->0:1 rands)]
  (is
   (some
    #(< % -1)
    (take sample-size rands)))

  (is
   (some
    #(> % 1)
    (take sample-size rands)))

  (is
   (every?
    #(and (<= % 1) (>= % 0))
    (take sample-size scaled)))))

(deftest ??rand-seq->triangular--bounds
 (let [rands (rand-seq)
       min 1
       mode 10
       max 100
       scaled (rand-seq->triangular rands min mode max)]
  (is
   (some
    #(< % -1)
    (take sample-size rands)))

  (is
   (some
    #(> % 1)
    (take sample-size rands)))

  (is
   (every?
    #(and (<= % max) (>= % min))
    (take sample-size scaled)))))
