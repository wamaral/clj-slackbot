(ns clj-slackbot.channels
  (:require [clojure.core.async :refer [chan pub sliding-buffer]]))

(def global-chans
  (memoize (fn []
             {:slack-message-in (chan (sliding-buffer 1))
              :parsed-message-in (chan (sliding-buffer 1))})))

(defn get-chan [key]
  (get (global-chans) key))

(def global-pubs
  (memoize (fn []
             {:message-pub (pub (get-chan :parsed-message-in) :command)})))

(defn get-pub [key]
  (get (global-pubs) key))
