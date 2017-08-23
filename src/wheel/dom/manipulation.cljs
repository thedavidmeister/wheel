(ns wheel.dom.manipulation
 (:require
  oops.core))

(defn document-append! [el]
 (when-not (.-body js/document)
  (oops.core/oset! (.-body js/document) (.createElement js/document "body")))
 (-> js/document .-body (.appendChild el)))

(defn document-remove! [el]
 (when (.-body js/document)
  (-> js/document .-body (.removeChild el))))
