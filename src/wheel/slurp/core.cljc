; https://gist.github.com/noprompt/9086232
(ns wheel.slurp.core
 (:refer-clojure :exclude [slurp]))

#?(:clj
   (defmacro slurp [file]
    (clojure.core/slurp file)))
