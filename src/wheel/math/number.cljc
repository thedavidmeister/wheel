(ns wheel.math.number)

(def pi #?(:clj Math/PI
           :cljs (.-PI js/Math)))
