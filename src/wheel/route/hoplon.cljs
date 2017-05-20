(ns wheel.route.hoplon
 (:require
  wheel.route.core
  wheel.dom.traversal
  wheel.dom.events
  [hoplon.core :as h]
  [javelin.core :as j]
  [cljs.test :refer-macros [deftest is]]))

(h/defelem link
 "Returns a routing link. Pass :wheel.route/preserve-params to params to preserve them on navigation"
 [{:keys [history routes handler params fallback] :as attributes} children]
 (assert (j/cell? history))
 (assert routes)
 (assert handler)
 (let [params (j/cell= (case params
                        ; Nil params is to overwrite params with navigate!
                        nil {}
                        ; Pass wheel.route/preserve-params to trigger handler!
                        :wheel.route/preserve-params nil
                        ; Default is value of params.
                        params))

       bidi= (j/cell= (wheel.route.core/path->bidi history routes fallback))
       current-handler? (j/cell= (= handler (:handler bidi=)))
       current-params? (j/cell= (= params (:route-params bidi=)))]
  (h/a
   :class "route-link"
   :click #(if params
            (wheel.route.core/navigate! history routes handler params)
            (wheel.route.core/handler! history routes handler))
   :data-current (j/cell= (remove nil? [(when current-handler? "handler")
                                        (when current-params? "params")]))
   (dissoc attributes :history :routes :handler :params :fallback)
   children)))

; (defn a
;   "Returns a routing link. Pass nil explicitly to params to have them preserved."
;  ([history routes body handler] (a history body handler {}))
;  ([history routes body handler params]
;   {:pre [(j/cell? history)]}
;   (let [bidi= (j/cell= (wheel.route.core/path->bidi history routes))
;         handler?= (route.state/handler?= handler)]
;    (h/a
;     :class #{"route-link"}
;     :click #(if params (route.state/navigate! handler params)
;                        (route.state/handler! handler))
;     :data-current (j/cell= (when handler?= "handler"))
;     body))))

; TESTS

(deftest ??link
 (let [child (h/div)
       h (j/cell :landing)
       el (link child h)]
  (route.state/navigate! :landing)

  (is (wheel.dom.traversal/contains? el child))
  (is (wheel.dom.traversal/is? el "[data-current]"))

  (reset! h :privacy)
  (is (not (wheel.dom.traversal/is? el "[data-current]")))

  (route.state/navigate! :privacy)
  (is (wheel.dom.traversal/is? el "[data-current]"))))

(deftest ??a-click
 (let [p (a "privacy" :privacy)
       l (a "landing" :landing)
       current-hash #(-> js/window .-location .-hash)
       current? #(wheel.dom.traversal/is? % "[data-current=\"handler\"]")]
  (route.state/navigate! :landing)
  (is (current? l))
  (is (not (current? p)))

  (wheel.dom.events/trigger-native! p "click")
  (is (= "/privacy" @route.state/path=))
  (is (not (current? l)))
  (is (current? p))

  (wheel.dom.events/trigger-native! l "click")
  (is (= "/" @route.state/path=))
  (is (current? l))
  (is (not (current? p)))))
