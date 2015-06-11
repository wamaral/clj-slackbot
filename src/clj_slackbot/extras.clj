(ns clj-slackbot.extras
  (:refer-clojure :exclude [inc dec]))

(def ^:private users (atom {}))

(defn inc [x]
  (cond
    (instance? clojure.lang.Keyword x)
    (swap! users update-in [x :karma] #(if (nil? %) 1 (clojure.core/inc %)))

    :else
    (clojure.core/inc x)))

(defn dec [x]
  (cond
    (instance? clojure.lang.Keyword x)
    (swap! users update-in [x :karma] #(if (nil? %) -1 (clojure.core/dec %)))

    :else
    (clojure.core/dec x)))

(defn karma-for [k]
  (prn (get-in @users [k :karma])))
