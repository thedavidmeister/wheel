(ns wheel.dom.manipulation)

(defn document-append! [el]
 (-> js/document .-body (.appendChild el)))

(defn document-remove! [el]
 (-> js/document .-body (.removeChild el)))
