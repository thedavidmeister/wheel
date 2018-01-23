(ns wheel.error.core
 (:require
  [hoplon.core :as h]
  taoensso.timbre
  #?(:cljs wheel.system-message.state)
  [clojure.test :refer [deftest is]]))

(defn throw-error
 [e]
 #?(:cljs (throw (js/Error. e))
    :clj (throw (Exception. e))))

(defn error
 "Wrapper for all the different ways we might want to handle an \"error\"."
 [& {:keys [error log user-message messages hard-error]}]
 ; Logs a message with logging level set to error.
 (when log
  (taoensso.timbre/error log))

 ; Displays a message to the user with vibe set to bad.
 ; Falls back to a regular error on JVM.
 #?(:cljs
    (when user-message
     (let [messages (or messages (wheel.system-message.state/messages))]
      (wheel.system-message.state/error! messages user-message))))

 ; Wrapping the thrown error in a timeout *should* isolate it from everything
 ; else, preventing WSOD, etc. We still do want to actually throw the error so
 ; that Sentry.io intercepts and logs it for us.
 ; Only supported in JS environment.
 #?(:cljs
    (when error
     (h/with-timeout 0
      (throw-error error))))

 ; Error immediately. This can very easily cause WSOD or break things if
 ; triggered while Hoplon/Javelin is trying to do something important.
 ; Use with caution.
 ; identical to error and user-message on JVM.
 (let [hard-error #?(:cljs hard-error :clj (or error hard-error user-message))]
  (when hard-error
   (throw-error hard-error))))

; TESTS

#?(:cljs
   (deftest ??error--system-message
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
           :system-message/id))))))
