(ns wheel.error.core
 (:require
  [hoplon.core :as h]
  taoensso.timbre
  wheel.system-message.state
  [cljs.test :refer-macros [deftest is async]]))

(defn error
 "Wrapper for all the different ways we might want to handle an \"error\"."
 [& {:keys [error log user-message messages hard-error]}]
 ; Logs a message with logging level set to error.
 (when log
  (taoensso.timbre/error log))

 ; Displays a message to the user with vibe set to bad.
 (when user-message
  (let [messages (or messages (wheel.system-message.state/messages))]
   (wheel.system-message.state/error! messages user-message)))

 ; Wrapping the thrown error in a timeout *should* isolate it from everything
 ; else, preventing WSOD, etc. We still do want to actually throw the error so
 ; that Sentry.io intercepts and logs it for us.
 (when error
  (h/with-timeout 0
   (throw (js/Error. error))))

 ; Error immediately. This can very easily cause WSOD or break things if
 ; triggered while Hoplon/Javelin is trying to do something important.
 ; Use with caution.
 (when hard-error
  (throw (js/Error. hard-error))))

(deftest ??error
 (let [messages (wheel.system-message.state/messages-cell)
       not-seen= (wheel.system-message.state/not-seen= messages)]

  ; We should be able to show the user something.
  (is (= {} @not-seen=))
  (error :messages messages :user-message "Bar!")
  (is (= {:system-message/body "Bar!"
          :system-message/vibe :bad
          :system-message/seen false}
       (dissoc
        (@not-seen= (first (keys @not-seen=)))
        :system-message/id)))))
