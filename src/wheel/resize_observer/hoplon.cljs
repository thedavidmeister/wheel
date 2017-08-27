(ns wheel.resize-observer.hoplon
 (:require
  [hoplon.core :as h]
  [hoplon.svg :as svg]
  [javelin.core :as j]
  wheel.clearfix.hoplon
  cljsjs.resize-observer-polyfill
  wheel.dom.traversal
  wheel.dom.manipulation
  [cljs.test :refer-macros [deftest is are async]]))

(h/defelem el
 [{:keys [width height f] :as attributes} children]
 (let [f (or f h/div)
       el (f
           (dissoc attributes :width :height)
           children
           (wheel.clearfix.hoplon/clearfix))
       width (or width (j/cell 0))
       height (or height (j/cell 0))
       ; https://stackoverflow.com/questions/220188/how-can-i-determine-if-a-dynamically-created-dom-element-has-been-added-to-the-d
       el-attached? #(-> % .-ownerDocument .-body (.contains %))
       cb (fn [es] (let [rect (-> es first .-contentRect)]
                    ; Don't trigger a resize when the el is detached from the
                    ; DOM as it will always be 0 and lead to a FOUC.
                    ; There is another resize when the el is re-attached anyway.
                    (when (el-attached? el)
                     (j/dosync
                      (reset! width (.-width rect))
                      (reset! height (.-height rect))))))]
  (.observe (js/ResizeObserver. cb) el)
  el))

(def div (partial el :f h/div))

; TESTS

(deftest ??el
 (is (wheel.dom.traversal/is? (el) "div"))
 (is (wheel.dom.traversal/is? (el :f h/span) "span"))
 (is (wheel.dom.traversal/is? (el :f svg/svg) "svg"))
 (is (wheel.dom.traversal/is? (el :f svg/g) "g")))

(deftest ??div
 (async done
  (let [height (j/cell 0)
        width (j/cell 0)
        floated (h/div :css {
                             :float "left"
                             :height "2px"
                             :width "1px"
                             :outline "1px solid blue"})
        el (h/div :css {:width "3px"}
            (div
             :height height
             :width width
             :css {:outline "1px solid red"}
             floated))]
   (is (wheel.dom.traversal/contains? el floated))

   ; These should still be the initial values as el is not attached to the DOM.
   (is (= 0 @height))
   (is (= 0 @width))

   (wheel.dom.manipulation/document-append! el)

   (let [correct-height? (j/cell= (= 2 height))
         correct-width? (j/cell= (= 3 width))]
    (j/cell= (when correct-height? (prn "ResizeObserver saw correct height.")))
    (j/cell= (when correct-width? (prn "ResizeObserver saw correct width.")))
    ; Shortcut the longer timeout once the dimensions are correct.
    (j/cell= (when (and correct-height? correct-width?) (done)))

    ; Short-circuit a failing test so it doesn't hang.
    (h/with-timeout 100
     (when-not (and @correct-height? @correct-width?)
      (is false (str "ResizeObserver did not see correct height and width. width: " @width ", height: " @height))
      (done)))))))
