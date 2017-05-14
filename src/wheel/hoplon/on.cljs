(ns wheel.hoplon.on
 (:require
  hoplon.core))

; https://github.com/hoplon/hoplon/pull/154
(defmethod hoplon.core/on! :hoplon.core/default
 [elem event callback]
 (.on (js/jQuery elem) (name event) callback))

(defmethod hoplon.core/on! :html/*
 [elem event callback]
 (.on (js/jQuery elem) (name event) callback))
