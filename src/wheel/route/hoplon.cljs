(ns wheel.route.hoplon
 (:require
  wheel.route.core
  wheel.dom.traversal
  wheel.dom.events
  [hoplon.core :as h]
  hoplon.jquery
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
       current-params? (j/cell= (= params (or (:route-params bidi=) {})))]
  (h/a
   :class "route-link"
   :click #(if params
            (wheel.route.core/navigate! history routes handler params)
            (wheel.route.core/handler! history routes handler))
   :data-current (j/cell=
                  (seq
                   (remove nil? [(when current-handler? "handler")
                                 (when current-params? "params")])))
   (dissoc attributes :history :routes :handler :params :fallback)
   children)))

; TESTS

(deftest ??link
 (let [history (wheel.route.core/history-cell)
       routes ["" {["foo/" :x] :foo
                   "bar" :bar
                   ["baz/" :x] :baz}]
       child (h/div)
       h (j/cell :landing)
       el (link
           :history history
           :routes routes
           :handler :foo
           :params {:x "123"}
           child)]

  (is (wheel.dom.traversal/contains? el child))

  (wheel.route.core/navigate! history routes :foo {:x "123"})

  (is (wheel.dom.traversal/is? el "[data-current*=handler]"))
  (is (wheel.dom.traversal/is? el "[data-current*=params]"))

  (wheel.route.core/navigate! history routes :foo {:x "456"})

  (is (wheel.dom.traversal/is? el "[data-current*=handler]"))
  (is (not (wheel.dom.traversal/is? el "[data-current*=params]")))

  ; Handler = :baz, Params = {:x 123}
  (reset! history "baz/123")
  (is (not (wheel.dom.traversal/is? el "[data-current*=handler]")))
  (is (wheel.dom.traversal/is? el "[data-current*=params]"))

  ; Handler = :baz, Params = {:x 456}
  (reset! history "baz/456")
  (is (not (wheel.dom.traversal/is? el "[data-current*=handler]")))
  (is (not (wheel.dom.traversal/is? el "[data-current*=params]")))

  ; Handler = :bar, Params = {}
  (reset! history "bar")
  (is (not (wheel.dom.traversal/is? el "[data-current*=handler]")))
  (is (not (wheel.dom.traversal/is? el "[data-current*=params]")))))

; (deftest ??a-click
;  (let [p (a "privacy" :privacy)
;        l (a "landing" :landing)
;        current-hash #(-> js/window .-location .-hash)
;        current? #(wheel.dom.traversal/is? % "[data-current=\"handler\"]")]
;   (route.state/navigate! :landing)
;   (is (current? l))
;   (is (not (current? p)))
;
;   (wheel.dom.events/trigger-native! p "click")
;   (is (= "/privacy" @route.state/path=))
;   (is (not (current? l)))
;   (is (current? p))
;
;   (wheel.dom.events/trigger-native! l "click")
;   (is (= "/" @route.state/path=))
;   (is (current? l))
;   (is (not (current? p)))))
