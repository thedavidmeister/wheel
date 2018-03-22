(ns time.hoplon
 (:refer-clojure :exclude [time])
 (:require
  [hoplon.core :as h]
  [javelin.core :as j]
  i18n.datetime
  [cljs.test :refer-macros [deftest is are]]))

(defn time
 [& {:keys [date stamp pattern locale tz]}]
 (let [d (j/cell=
          (or
           date
           (js/Date. stamp)
           (js/Date.)))]
  (h/span
   :data-time true
   (i18n.datetime/format-cell d
    :pattern pattern
    :locale locale
    :tz tz))))

; TESTS

(deftest ??time
 (are [e s l] (= e (.-innerHTML (time :stamp s :locale l :tz 0)))
  "1 January 1970" 0 "en-AU"
  "2 January 1970" 100000000 "en-AU"
  "12 January 1970" 1000000000 "en-AU"
  "26 April 1970" 10000000000 "en-AU"
  "3 March 1973" 100000000000 "en-AU"
  "9 September 2001" 1000000000000 "en-AU"
  "January 1, 1970" 0 "en-US"
  "January 2, 1970" 100000000 "en-US"
  "January 12, 1970" 1000000000 "en-US"
  "April 26, 1970" 10000000000 "en-US"
  "March 3, 1973" 100000000000 "en-US"
  "September 9, 2001" 1000000000000 "en-US"))

(deftest ??time-formats
 (let [p (j/cell :short-date)
       l (j/cell "en-AU")
       t (time :stamp 100000000000 :pattern p)]
  (is (= "3/3/73" (.-innerHTML t)))

  (reset! p :medium-date)
  (is (= "3 Mar. 1973" (.-innerHTML t)))))
