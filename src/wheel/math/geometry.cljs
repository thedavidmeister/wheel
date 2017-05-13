(ns wheel.math.geometry
 (:require
  [cljs.test :refer-macros [deftest is]]))

(defn polar->cartesian
 "Given a radius (unitless) and rotation in radians, returns (unitless) [x y] co-ordinates"
 [radius radians]
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
 (is (= true false)))
