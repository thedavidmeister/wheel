(ns wheel.dom.manipulation)

(defn document-append! [el]
 (when-not (.-body js/document)
  ; https://developer.mozilla.org/en-US/docs/Web/API/Document/body
  (set! (.-body js/document) (.createElement js/document "body")))
 (-> js/document .-body (.appendChild el)))

(defn document-remove! [el]
 (when (.-body js/document)
  (-> js/document .-body (.removeChild el))))
