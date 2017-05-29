(ns wheel.math.distribution.triangular
 (:require wheel.math.number))
#?(:clj (use 'clojure.test))

; From the wikipedia calculations
; a = min
; b = max
; c = mode

; https://en.wikipedia.org/wiki/Triangular_distribution#Generating_Triangular-distributed_random_variates
(defn -inverse-cdf-fn
 [min mode max]
 (if-not (and min mode max (<= min mode max))
  (fn [U] wheel.math.number/nan)
  (if-not (< min max)
   ; Avoid a divide by zero in the case that max = min.
   (fn [U] min)

   ; Precalculate a few constants for speed.
   (let [Fc (/ (- mode min) (- max min))
         +-multiplier (* (- max min) (- mode min))
         --multiplier (* (- max min) (- max mode))]
    (fn
     [U]
     (if (< U Fc)
      (+ min (Math/sqrt (* U +-multiplier)))
      (- max (Math/sqrt (* (- 1 U) --multiplier)))))))))

(defn inverse-cdf-fn
 [min mode max]
 (let [[min mode max] (map wheel.math.number/safe-bigdec [min mode max])]
  #?(:clj (with-precision 10 (-inverse-cdf-fn min mode max))
     :cljs (-inverse-cdf-fn min mode max))))

(defn mean
 [min mode max]
 (if (<= min mode max)
  (/ (+ min mode max)
     3)
  wheel.math.number/nan))

(defn median
 [min mode max]
 (if (<= min mode max)
  (if (<= (/ (+ min max) 2) mode)
   (+ min (Math/sqrt (* 0.5 (- max min) (- mode min))))
   (- max (Math/sqrt (* 0.5 (- max min) (- max mode)))))
  wheel.math.number/nan))

(defn variance
 [min mode max]
 (if (<= min mode max)
  (/ (+ (* min min) (* max max) (* mode mode) (* -1 min max) (* -1 min mode) (* -1 max mode))
     18)
  wheel.math.number/nan))

(defn std-dev
 [min mode max]
 (Math/sqrt (variance min mode max)))

(defn estimate
 "Calculate a recommended estimate of mean + 2 std-dev"
 [min mode max]
 (+ (mean min mode max)
    (* 2 (std-dev min mode max))))

; All values checked in Mathematica.
#?(:clj
   ; InverseCDF[TriangularDistribution[{"min", "max"}, "mode"], "U"]
   (deftest ??inverse-cdf-fn
    (are [min mode max] (wheel.math.number/nan? ((inverse-cdf-fn min mode max) (rand)))
     nil nil nil
     nil nil 1
     nil 1 1
     1 nil nil
     1 1 nil
     wheel.math.number/nan wheel.math.number/nan wheel.math.number/nan
     wheel.math.number/nan wheel.math.number/nan 1
     wheel.math.number/nan 1 1
     1 wheel.math.number/nan wheel.math.number/nan
     1 1 wheel.math.number/nan)

    (are [e U min mode max] (== e ((inverse-cdf-fn min mode max) U))
     1 0 1 2 3
     1.7071067811865475 0.25 1 2 3
     2 0.5 1 2 3
     2.2928932188134525 0.75 1 2 3
     3 1 1 2 3

     2 0 2 6 12
     5.16227766016838 0.25 2 6 12
     6.522774424948339 0.5 2 6 12
     8.127016653792584 0.75 2 6 12
     12 1 2 6 12

     0 0 0 0 0
     0 1 0 0 0

     1 0 1 1 1
     1 1 1 1 1

     ; Do some big stuff that regular floats and ints can't handle.
     10000000000 0 10000000000 20000000000 30000000000
     20000000000 0.5 10000000000 20000000000 30000000000
     30000000000 1 10000000000 20000000000 30000000000

     1 0 1 2 30000000000
     8.786796564403576E9 0.5 1 2 30000000000
     30000000000 1 1 2 30000000000

     ; Some big dec stuff.
     10000000000.0 0 10000000000.0 20000000000.0 30000000000.0
     20000000000.0 0.5 10000000000.0 20000000000.0 30000000000.0
     30000000000.0 1 10000000000.0 20000000000.0 30000000000.0)))


#?(:clj
   (deftest ??mean
    (are [e min mode max] (== e (mean min mode max))
     2 1 2 3
     10 5 10 15
     0 0 0 0
     1 1 1 1
     20 10 20 30
     (/ 211 3) 1 100 110)))

#?(:clj
   (deftest ??median
    (are [e min mode max] (== e (median min mode max))
     2 1 2 3
     20 10 20 30
     1 1 1 1
     0 0 0 0
     10 5 10 15
     (- 12 (Math/sqrt 30)) 2 6 12)))

#?(:clj
   (deftest ??variance
    (are [e min mode max] (== e (variance min mode max))
     0 0 0 0
     (/ 38 9) 2 6 12
     (/ 1 6) 1 2 3
     0 1 1 1)))

#?(:clj
   (deftest ??std-dev
    (are [e min mode max] (== e (std-dev min mode max))
     0 0 0 0
     0 1 1 1
     (Math/sqrt (/ 38 9)) 2 6 12
     (Math/sqrt (/ 1 6)) 1 2 3)))

#?(:clj
   (deftest ??non-monotonic-nans
    (let [examples [[3 2 1]
                    [2 1 1]
                    [2 2 1]
                    [1 1 0]
                    [0 1 0]]
          fns [mean median variance std-dev]]
     (doseq [e examples f fns]
      (is (wheel.math.number/nan? (apply f e))))

     ; Inverse CDF has slightly different params so we have to test separately.
     (doseq [e examples u [0 0.5 1]]
      (is (wheel.math.number/nan? ((apply inverse-cdf-fn e) u)))))))
