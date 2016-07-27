(ns clj-slackbot.handlers.app-version
  (:require [clj-slackbot.channels :refer [get-pub]]
            [clj-http.client :as client]
            [clojure.core.async :refer [<! chan go-loop sub]]
            [clojure.string :as s]))

(def version-chan (chan))
(sub (get-pub :message-pub) :version version-chan)

(def url-regex #"(?im)^<?(https?:\/\/)?([^\/:]+)(:\d+)?\/?")

(def shortcut
  {:bioidprod "https://login.bio/version"
   :bioidqa "http://bioid.qa.cloud.bionexo.com.br/version"
   :regeneprod "https://platform.bio/version"
   :regeneqa "http://qaregene.bionexo.com.br/version"})

(defn parse-url [url]
  (let [sanitized (s/replace url #"[<>]" "")
        [_ protocol domain port] (re-matches url-regex sanitized)
        proto (if (s/blank? protocol) "https://" protocol)]
    (get shortcut (keyword sanitized)
         (str proto domain port "/version"))))

(go-loop []
  (let [message (<! version-chan)
        body (:body message)
        url (parse-url body)
        writer (:out message)]
    (if (= body "shortcuts")
      (writer (println-str shortcut))
      (try
        (writer (str url " " (:body (client/get url))))
        (catch Exception e
          (writer (str "Error: no version information found. Is the URL well-formed? " url)))))
    (recur)))
