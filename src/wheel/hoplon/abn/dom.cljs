(ns wheel.hoplon.abn.dom
 (:require
  wheel.hoplon.abn.api
  wheel.hoplon.link.dom
  [cljs.test :refer-macros [deftest is]]))

(defn abn [n]
 (wheel.hoplon.link.dom/external
  (wheel.hoplon.abn.api/abr-search-url n)
  (wheel.hoplon.abn.api/normalize n)))

; TESTS

(deftest ??abn
 (let [n "12345678910"
       el (abn n)]
  (wheel.hoplon.link.dom/external?
   el
   "https://abr.business.gov.au/SearchByAbn.aspx?SearchText=12345678910"
   "12 345 678 910")))
