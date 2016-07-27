(ns clj-slackbot.core
  (:require [slack-rtm.core :as slack]
            [clojure.string :as s]
            [environ.core :refer [env]]
            [clj-slackbot.channels :refer [get-chan]]
            [clj-slackbot.handlers.connection :as conn]
            [clj-slackbot.load-handlers]
            [clj-http.client :as http]
            [clojure.core.async :as async :refer [>! <! go go-loop]])
  (:import java.lang.Thread)
  (:gen-class))

(def cmd-prefix (or (:bot-prefix env) "!"))
(def cmd-regex (re-pattern (str "^(?ius)(" cmd-prefix ")(\\w+)(?:\\s+(.*))?")))
(def bot-name (or (:bot-name env) "ClojureBot"))
(def bot-icon (or (:bot-icon env) "http://lorempixel.com/48/48"))

(defn message-writer [original out-chan]
  (fn [text]
    (let [payload {:channel (:channel original)
                   :type "message"
                   :text text
                   :as_user false
                   :username bot-name
                   :icon_url bot-icon}]
      (slack/send-event out-chan payload))))

(defn message-writer-http [original out-chan]
  (fn [text]
    (let [payload {:token (:slack-api-token env)
                   :channel (:channel original)
                   :text text
                   :as_user false
                   :unfurl_links true
                   :unfurl_media true
                   :username bot-name
                   :icon_url bot-icon}]
      (http/get "https://slack.com/api/chat.postMessage" {:query-params payload}))))

(defn parse-message [message]
  (let [contents (:text message)
        [_ prefix command body] (re-matches cmd-regex contents)]
    (if-not (s/blank? command)
      {:original-message message
       :prefix prefix
       :command (keyword command)
       :body body})))

(defn handle-message [message out-chan]
  (if-let [parsed-message (parse-message message)]
    (go (>! (get-chan :parsed-message-in)
            (merge parsed-message
                   {:out (message-writer-http message out-chan)})))))

(defn -main [& args]
  (let [conn (slack/connect (:slack-api-token env)
                            :hello conn/established
                            :error conn/error
                            :on-close conn/closed)
        events (:events-publication conn)
        dispatcher (:dispatcher conn)]
    (slack/sub-to-event events :message (get-chan :slack-message-in))

    (go-loop []
      (if-let [message (<! (get-chan :slack-message-in))]
        (handle-message message dispatcher))
      (recur))

    #_(.join (Thread/currentThread))))
