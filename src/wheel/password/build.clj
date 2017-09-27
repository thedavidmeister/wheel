(ns wheel.password.build)

(defn with-cljs-compiler-options
 ([] (with-cljs-compiler-options {}))
 ([options]
  (-> options
   (update
    :foreign-libs
    conj
    {:file "https://raw.githubusercontent.com/dropbox/zxcvbn/master/dist/zxcvbn.js"
     :provides ["zxcvbn.lib"]}))))
