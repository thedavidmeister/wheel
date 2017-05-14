(def project 'thedavidmeister/wheel)
(def version "0.1.0-SNAPSHOT")

(set-env! :source-paths   #{"src"}
          :dependencies   '[[org.clojure/clojure "RELEASE"]
                            [org.clojure/clojurescript "1.9.542"]
                            [adzerk/boot-test "RELEASE" :scope "test"]
                            [adzerk/bootlaces "RELEASE" :scope "test"]
                            [crisptrutski/boot-cljs-test "0.2.2-SNAPSHOT" :scope "test"]
                            [hoplon "7.0.1"]])

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

(require '[crisptrutski.boot-cljs-test :refer [test-cljs]]
         '[adzerk.bootlaces :refer :all])

(bootlaces! version)

(def cljs-compiler-options {})

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
                              (merge {:load-tests true}))
               :namespaces [#".*"])))
