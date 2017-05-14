(ns wheel.math.geometry
 (:require
  wheel.math.number
  #?(:cljs [cljs.test :refer-macros [deftest is are]]
     :clj [clojure.test :refer [deftest is are]])))

(defn cos [radians]
 #?(:cljs (.cos js/Math radians)
    :clj (Math/cos radians)))

(defn sin [radians]
 #?(:cljs (.sin js/Math radians)
    :clj (Math/sin radians)))

(defn abs [n]
 #?(:cljs (.abs js/Math n)
    :clj (Math/abs n)))

(defn polar->cartesian
 "Given a radius (unitless) and rotation in radians, returns (unitless) [x y] co-ordinates"
 [radius radians]
 {:pre [(or (pos? radius) (zero? radius))]}
 ; https://www.wolframalpha.com/input/?i=polar+coordinates
 [(* radius (cos radians))
  (* radius (sin radians))])

(defn degrees->radians
 "Given a rotation in degrees, returns the same rotation in radians"
 [degrees]
 (/ (* degrees wheel.math.number/pi) 180))

(defn radians->degrees
 "Given a rotation in radians, returns the same rotation in degrees"
 [radians]
 (/ (* radians 180) wheel.math.number/pi))

; TESTS.

(deftest ??polar->cartesian
 ; oracle.
 (let [angle (rand (* 2 wheel.math.number/pi))
       radius (rand 10)]
  (is (= [(* radius (cos angle))
          (* radius (sin angle))]
         (polar->cartesian radius angle))))

 ; examples.
 ; Apparently this has some serious rounding issues so we "only" test the values
 ; to within a tolerance of 10^-15
 (let [pi wheel.math.number/pi
       within-tolerance? (fn [n] (< (abs n) 1E-15))
       ins [; Trivial case.
            [0 0] [0 0]
            [0 (rand (* 2 pi))] [0 0]

            [1 0] [1 0]
            [1 (* 0.25 pi)] [0.7071067811865476 0.7071067811865476]
            [1 (* 0.5 pi)] [0 1]
            [1 pi] [-1 0]
            [1 (* 1.5 pi)] [0 -1]

            [2 0] [2 0]
            [2 (* 0.25 pi)] [1.4142135623730951 1.4142135623730951]
            [2 (* 0.5 pi)] [0 2]
            [2 pi] [-2 0]
            [2 (* 1.5 pi)] [0 -2]]]
  (doseq [[i [xo yo]] (partition 2 ins)]
   (let [[x y] (apply polar->cartesian i)]
    (is (within-tolerance? (- xo x)) (str "xo and x not within tolerance. xo:" xo " x:" x " i:" i))
    (is (within-tolerance? (- yo y)))))))

(deftest ??degrees->radians
 ; oracle
 (let [degrees (rand 360)]
  (is (== (degrees->radians degrees)
          (/ (* degrees wheel.math.number/pi)
             180))))

 ; examples
 (let [pi wheel.math.number/pi]
  (are [i o] (== o (degrees->radians i))
   0 0
   45 (/ pi 4)
   90 (/ pi 2)
   180 pi
   360 (* pi 2))))

(deftest ??radians->degrees
 ; oracle
 (let [radians (rand (* 2 wheel.math.number/pi))]
  (is (== (radians->degrees radians))
      (/ (* radians 180)
         wheel.math.number/pi)))

 ; examples
 (let [pi wheel.math.number/pi]
  (are [i o] (== o (radians->degrees i))
   0 0
   (/ pi 4) 45
   (/ pi 2) 90
   pi 180
   (* 2 pi) 360)))
