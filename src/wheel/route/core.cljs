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

(defn path->bidi
 "Given a path, routes and fallback handler, returns a bidi location."
 ([path routes] (bidi.bidi/match-route routes path))
 ([path routes fallback]
  (or (path->bidi path routes)
      {:handler fallback})))

(defn bidi->path
 "Given a bidi handler, and optionally bidi params, returns a path"
 ([routes handler] (bidi->path routes handler {}))
 ([routes handler params]
  {:pre [(keyword? handler) (map? params)]}
  (let [with-handler (partial bidi.bidi/path-for routes handler)
        param-list (->> params (into []) flatten)]
   (apply with-handler param-list))))

(defn navigate!
 "Set the history cell to the given handler and params."
 ([history routes handler] (navigate! history routes handler {}))
 ([history routes handler params]
  (reset! history (str (bidi->path routes handler params)))))

(defn handler!
 "Set the history cell to the given handler without changing the params."
 [history routes handler]
 {:pre [(j/cell? history) (sequential? routes) (keyword? handler)]}
 (navigate! history routes handler (-> @history (path->bidi routes) :route-params)))

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
  (is (= "bar" @c (current-hash)))))

(deftest ??path->bidi
 (let [path (str (random-uuid))
       handler :foo
       routes [path handler]
       fallback :bar]
  (is (= {:handler handler}
         (path->bidi path routes fallback)))

  (is (= {:handler fallback}
         (path->bidi (str (random-uuid)) routes fallback)))))

(deftest ??bidi->path
 ; Local route defs.
 (is (= "/foo" (bidi->path ["/foo" :foo] :foo {})))
 ; Params.
 (is (= "/foo/123" (bidi->path ["/foo/" [[["" :bar] :foo]]] :foo {:bar 123}))))

(deftest ??navigate!
 ; no params
 (let [history (history-cell)
       path (str (random-uuid))
       handler :foo
       routes [path handler]]
  (navigate! history routes handler)
  (is (= path (current-hash))))

 (let [history (history-cell)
       path (str (random-uuid))
       path2 (str (random-uuid))
       param-key :bar
       param-value (str (random-uuid))
       handler :foo
       handler2 :baz
       routes [path {["/" param-key] handler}
               path2 {["/" param-key] handler2}]]
  (navigate! history routes handler {param-key param-value})
  (is (= (str path "/" param-value)
         (current-hash)))

  ; Test handler!
  (handler! history routes handler2)
  (is (= (str path2 "/" param-value)))))
