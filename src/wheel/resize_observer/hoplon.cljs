(ns wheel.resize-observer.hoplon
 (:require
  [hoplon.core :as h]
  [javelin.core :as j]
  wheel.clearfix.hoplon
  polyfill.ResizeObserver))

(h/defelem div
 [{:keys [width height]} children]
 (let [el (h/div
           children
           (wheel.clearfix.hoplon/clearfix))
       ; https://stackoverflow.com/questions/220188/how-can-i-determine-if-a-dynamically-created-dom-element-has-been-added-to-the-d
       el-attached? #(-> % .-ownerDocument .-body (.contains %))
       cb (fn [es] (let [rect (-> es first .-contentRect)]
                    ; Don't trigger a resize when the el is detached from the
                    ; DOM as it will always be 0 and lead to a FOUC.
                    ; There is another resize when the el is re-attached anyway.
                    (when (el-attached? el)
                     (j/dosync
                      (when width (reset! width (.-width rect)))
                      (when height (reset! height (.-height rect)))))))]
  (.observe (js/ResizeObserver. cb) el)
  el))
