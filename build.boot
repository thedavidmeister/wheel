(def project 'wheel)
(def version "0.1.0-SNAPSHOT")

(set-env! :source-paths   #{"src"}
          :dependencies   '[[org.clojure/clojure "RELEASE"]
                            [adzerk/boot-test "RELEASE" :scope "test"]
                            [adzerk/bootlaces "RELEASE" :scope "test"]])

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

(require '[adzerk.boot-test :refer [test]]
         '[adzerk.bootlaces :refer :all])

(bootlaces! version)
