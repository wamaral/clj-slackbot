(defproject clj-slackbot "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.2.385"]
                 [slack-rtm "0.1.3"]
                 [compojure "1.5.0"]
                 [clojail "1.0.6"]
                 [clj-http "3.1.0"]
                 [cheshire "5.6.1"]
                 [environ "1.0.2"]]
  :uberjar-name "clj-slackbot.jar"
  :main clj-slackbot.core
  :jvm-opts ["-Djava.security.manager" "-Djava.security.policy=.java.policy"]
  :profiles
  {:dev {:repl-options {:init-ns clj-slackbot.core}
         :dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}
   :uberjar {:aot :all}})
