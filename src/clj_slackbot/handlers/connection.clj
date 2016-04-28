(ns clj-slackbot.handlers.connection)

(defn established [_]
  (prn "Connection established"))

(defn closed [{:keys [status-code reason]}]
  (prn "Connection closed" status-code ":" reason))

(defn error [{:keys [code msg]}]
  (prn "Error" code ":" msg))
