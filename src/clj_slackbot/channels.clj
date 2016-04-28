(ns clj-slackbot.channels
  (:require [clojure.core.async :refer [chan pub]]))

(def global-chans
  (memoize (fn []
             {:slack-message-in (chan)
              :parsed-message-in (chan)})))

(defn get-chan [key]
  (get (global-chans) key))

(def global-pubs
  (memoize (fn []
             {:message-pub (pub (get-chan :parsed-message-in) :command)})))

(defn get-pub [key]
  (get (global-pubs) key))
