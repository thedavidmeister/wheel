(ns wheel.math.geometry
 (:require
  [cljs.test :refer-macros [deftest is are]]))

(defn polar->cartesian
 "Given a radius (unitless) and rotation in radians, returns (unitless) [x y] co-ordinates"
 [radius radians]
 {:pre [(or (pos? radius) (zero? radius))]}
 ; https://www.wolframalpha.com/input/?i=polar+coordinates
 [(* radius (.cos js/Math radians))
  (* radius (.sin js/Math radians))])

(defn degrees->radians
 "Given a rotation in degrees, returns the same rotation in radians"
 [degrees]
 (/ (* degrees (.-PI js/Math)) 180))

(defn radians->degrees
 "Given a rotation in radians, returns the same rotation in degrees"
 [radians]
 (/ (* radians 180) (.-PI js/Math)))

; TESTS.

(deftest ??polar->cartesian
 ; First, an oracle.
 (let [angle (rand (* 2 (.-PI js/Math)))
       radius (rand 10)]
  (is (= [(* radius (.cos js/Math angle))
          (* radius (.sin js/Math angle))]
         (polar->cartesian radius angle))))

 ; Second a bunch of hand-picked examples.
 ; Apparently this has some serious rounding issues so we "only" test the values
 ; to within a tolerance of 10^-15
 (let [pi (.-PI js/Math)
       within-tolerance? (fn [n] (< (.abs js/Math n) 1E-15))
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
