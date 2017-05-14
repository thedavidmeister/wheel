(ns wheel.abn.hoplon
 (:require
  wheel.abn.core
  wheel.link.hoplon
  [cljs.test :refer-macros [deftest is]]))

(defn abn [n]
 (wheel.link.hoplon/external
  (wheel.abn.core/abr-search-url n)
  (wheel.abn.core/normalize n)))

; TESTS

(deftest ??abn
 (let [n "12345678910"
       el (abn n)]
  (wheel.link.hoplon/external?
   el
   "https://abr.business.gov.au/SearchByAbn.aspx?SearchText=12345678910"
   "12 345 678 910")))
