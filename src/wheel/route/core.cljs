(ns wheel.route.core
 (:require
  goog.events
  goog.History
  bidi.bidi
  medley.core
  cuerdas.core
  [javelin.core :as j]
  [cljs.test :refer-macros [deftest is async]])
 (:import [goog History]))

(defn history-cell
 "A cell analagous to hoplon's route cell, based on Google Closure History API."
 []
 (let [c (j/cell nil)
       history (History.)]
  (j/with-let [_ (j/cell= c #(.setToken history %))]
   (goog.events/listen history goog.History/EventType.NAVIGATE
    (fn [e]
     (reset! c (.-token e))))
   (.setEnabled history true))))

(defn current-hash
 "Fetches the current hash from the window location, sans left # character"
 []
 (-> js/window .-location .-hash (cuerdas.core/ltrim "#")))

(defn set-path!
 "Sets the path for a history cell"
 [c s]
 {:pre [(j/cell? c) (string? s)]}
 (reset! c s))

(defn history=->location=
 "Given cells for history, routes and fallback, returns a bidi location cell."
 [history routes fallback]
 (j/cell= (or (bidi.bidi/match-route routes history)
              {:handler fallback})))

(defn bidi->path
 ([routes handler] (bidi->path routes handler {}))
 ([routes handler params]
  {:pre [(keyword? handler) (map? params)]}
  (let [with-handler (partial bidi.bidi/path-for routes handler)
        param-list (->> params (into []) flatten)]
   (apply with-handler param-list))))

; TESTS

(deftest ??history-cell--set-hash
 ; Can we set c by modifying the window hash directly?
 ; https://github.com/google/closure-library/issues/825
 (async done
  (let [c (history-cell)
        p (str (random-uuid))]
   (-> js/window .-location .-hash (set! p))
   (j/cell=
    (when (= p c (current-hash))
     (done))))))

(deftest ??history-cell
 (let [c (history-cell)]
  ; Is c correctly initialized to some hash?
  (is (not (nil? @c)))
  (is (string? @c))

  ; Can we set c and the window hash by resetting c?
  (reset! c "bar")
  (is (= "bar" @c (current-hash)))

  (set-path! c "baz")
  (is (= "baz" @c (current-hash)))))

(deftest ??history=->location=
 (let [history (history-cell)
       routes (j/cell ["fooo" :foo])
       fallback (j/cell :bar)
       location (history=->location= history routes fallback)]
  (is (= @location
         {:handler :bar}))

  (set-path! history "fooo")
  (is (= @location
         {:handler :foo}))

  (set-path! history (str (random-uuid)))
  (is (= @location
         {:handler :bar}))))

(deftest ??bidi->path
 ; Local route defs.
 (is (= "/foo" (bidi->path ["/foo" :foo] :foo {})))
 ; Params.
 (is (= "/foo/123" (bidi->path ["/foo/" [[["" :bar] :foo]]] :foo {:bar 123}))))
