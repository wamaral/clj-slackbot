(ns clj-slackbot.handlers.clj-eval
  (:require [clojail.core :refer [sandbox]]
            [clojail.testers :refer [secure-tester-without-def blanket]]
            [environ.core :refer [env]]
            [clj-slackbot.channels :refer [get-pub]]
            [clojure.core.async :refer [<! chan go-loop sub]]
            [clj-http.client :as client])
  (:import java.io.StringWriter
           java.util.concurrent.TimeoutException)
  (:gen-class))

(def clj-chan (chan))
(sub (get-pub :message-pub) :clj clj-chan)

(def clj-slackbot-tester
  (conj secure-tester-without-def (blanket "clj-slackbot")))

(def sb (sandbox clj-slackbot-tester))

(defn eval-expr
  "Evaluate the given string"
  [code]
  (try
    (with-open [out (StringWriter.)]
      (let [form (binding [*read-eval* false] (read-string code))
            result (sb form {#'*out* out})]
        {:status true
         :input code
         :form form
         :result result
         :output (.toString out)}))
    (catch Exception e
      {:status false
       :input code
       :result (.getMessage e)})))

(defn format-result [r]
  (if (:status r)
    (str "```"
         "=> " (:form r) "\n"
         (when-let [o (:output r)]
           o)
         (if (nil? (:result r))
           "nil"
           (prn-str (:result r)))
         "```")
    (str "```"
         "=> " (or (:form r) (:input r)) "\n"
         (or (:result r) "Unknown Error")
         "```")))

(go-loop []
  (let [message (<! clj-chan)
        body (:body message)
        writer (:out message)]
    (-> body
        eval-expr
        format-result
        writer)
    (recur)))
