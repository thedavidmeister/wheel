(ns wheel.dom.events
 (:require
  wheel.dom.manipulation
  wheel.hoplon.on
  hoplon.jquery
  wheel.dom.data))

(defn ensure-original-object!
 [e]
 (if-not (aget e "originalEvent")
  (aset e "originalEvent" (js-obj))
  e))

(defn set-data!
 [e k v]
 (ensure-original-object! e)
 (-> e
  (aget "originalEvent")
  (aset k v))
 ; It is common to bind set-data! as an event handler. Return the event as it
 ; is truthy and won't cause the event handler to return false (which v might).
 e)

(defn get-data
 [e k]
 (if-let [original-event (aget e "originalEvent")]
  (aget original-event k)))

(defn stop-propagation
 "compable stopPropagation event handler"
 [event]
 (.stopPropagation event)
 event)

(defn prevent-default
 "compable preventDefault event handler"
 [event]
 (.preventDefault event)
 event)

(defn make-bubblable! [el]
 (wheel.dom.manipulation/document-append! el))

; https://www.w3.org/TR/DOM-Level-3-Events/#events-Events-DocumentEvent-createEvent
(defn trigger-native!
 [el name]
 {:pre [(wheel.dom.data/el? el)]}
 (let [e (.createEvent js/document "UIEvents")]
  (.initEvent e name true true)
  (.dispatchEvent el e)))

(defn trigger-jq!
 ([el name] (trigger-jq! el name nil))
 ([el name properties]
  (let [e (js/jQuery.Event. name (clj->js properties))]
   (-> el js/jQuery (.trigger e)))))
