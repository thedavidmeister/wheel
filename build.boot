(def project 'thedavidmeister/wheel)
(def version "0.3.5")

(set-env!
 :source-paths #{"src"}
 :dependencies
 '[[org.clojure/clojure "1.9.0"]
   [org.clojure/clojurescript "1.9.946"]
   [adzerk/boot-test "RELEASE" :scope "test"]
   [adzerk/bootlaces "RELEASE" :scope "test"]
   [adzerk/boot-cljs "2.1.4"]
   [doo "0.1.8"]
   [crisptrutski/boot-cljs-test "0.3.5-SNAPSHOT" :scope "test"]
   [adzerk/boot-test "1.1.1" :scope "test"]
   [hoplon "7.1.0-SNAPSHOT"]
   [samestep/boot-refresh "0.1.0" :scope "test"]

   ; Other util libs
   [medley "1.0.0"]
   [binaryage/oops "0.5.6"]
   [com.taoensso/timbre "4.10.0"]
   [com.gfredericks/test.chuck "0.2.8"]

   ; Math
   [thedavidmeister/cljc-long "1.1"]
   [thedavidmeister/xoroshiro128 "1.1.3"]

   ; Strings
   [funcool/cuerdas "2.0.4"]
   [cheshire "5.8.0"]

   ; Data validation
   [prismatic/schema "1.1.6"]

   ; Routing
   [bidi "2.1.1"]

   ; Data
   [datascript "0.16.1"]

   ; CLJSJS
   [cljsjs/resize-observer-polyfill "1.4.2-0"]])

(task-options!
 pom {:project     project
      :version     version
      :description "Don't re-invent it! simple and basic components for clj(s)/hoplon web development."
      :url         "https://github.com/thedavidmeister/wheel"
      :scm         {:url "https://github.com/thedavidmeister/wheel"}})

(deftask build
  "Build and install the project locally."
  []
  (comp (pom) (jar) (install)))

(require
 '[crisptrutski.boot-cljs-test :refer [test-cljs]]
 '[adzerk.bootlaces :refer :all]
 '[adzerk.boot-test :refer [test]]
 '[samestep.boot-refresh :refer [refresh]])

(bootlaces! version)

(def cljs-compiler-options {})

(deftask deploy
 []
 (comp
  (build-jar)
  (push-release)))

(deftask repl-server
 []
 (comp
  (watch)
  (refresh)
  (repl :server true)))

(deftask repl-client
 []
 (repl :client true))

(deftask tests-cljs
  "Run all the CLJS tests"
  [w watch? bool "Watches the filesystem and reruns tests when changes are made."]
  ; Run the JS tests
  (comp
    (if watch?
        (comp
          (watch)
          (speak :theme "woodblock"))
        identity)
    (test-cljs :exit? (not watch?)
               ; :js-env :chrome
               :cljs-opts (-> cljs-compiler-options
                              (merge {:load-tests true
                                      :process-shim false}))
               :namespaces [#".*"])))

(defn test-filter-for-wip
 [wip?]
 (if wip?
  '(:wip (meta %))
  '(not (or (:broken (meta %))
            (:wip (meta %))))))

(deftask tests-clj
 "Run all the CLJ tests"
 [w watch? bool "Watches the filesystem and reruns tests when changes are made."
  W wip? bool "true to only run WIP tests. WIP tests will not run if false."]
 (comp
   (if watch?
       (comp
         (watch)
         (speak :theme "woodblock"))
       identity)
   (test
     :filters [(test-filter-for-wip wip?)])))
