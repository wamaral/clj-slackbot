(ns clj-slackbot.handlers.ping
  (:require [clj-slackbot.channels :refer [get-pub]]
            [clojure.core.async :refer [<! chan go-loop sub]]))

(def ping-chan (chan))
(sub (get-pub :message-pub) :ping ping-chan)

(go-loop []
  (let [message (<! ping-chan)
        writer (:out message)]
    (writer "Pong!")
    (recur)))
