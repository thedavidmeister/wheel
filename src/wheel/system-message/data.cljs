(ns system-message.data
 (:require
  [clojure.spec.alpha :as spec]
  wheel.dom.data))

(spec/def :system-message/id uuid?)
(spec/def :system-message/seen boolean?)
(spec/def :system-message/vibe #{:bad :neutral :good})
(spec/def :system-message/body
 (spec/or
  :wheel.dom/element :wheel.dom/element
  :wheel.dom/string string?))
(spec/def :system-message/message
 (spec/keys
  :req [:system-message/seen :system-message/vibe :system-message/body]))
(spec/def :system-message/messages
 (spec/map-of :system-message/id :system-message/message))
