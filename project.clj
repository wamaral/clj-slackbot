(defproject clj-slackbot "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.4.0"]
                 [clojail "1.0.6"]
                 [clj-http "2.0.1"]
                 [cheshire "5.5.0"]
                 [environ "1.0.2"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [ring/ring-defaults "0.1.5"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler clj-slackbot.core.handler/app}
  :uberjar-name "clj-slackbot.jar"
  :main clj-slackbot.core.handler
  :jvm-opts ["-Djava.security.manager" "-Djava.security.policy=.java.policy"]
  :profiles
  {:dev {:repl-options {:init-ns clj-slackbot.core.handler}
         :dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}
   :uberjar {:aot :all}})
