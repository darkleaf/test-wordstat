(ns wordstat.handler
  (:require
   [ring.middleware.params :refer [wrap-params]]
   [ring.util.response :as resp]
   [clojure.core.async :as async]
   [cheshire.core :as json]
   [wordstat.processor :as processor]))

(defn- get-words [req]
  (let [words (get-in req [:params "query"])]
    (cond
      (nil? words) []
      (string? words) [words]
      :else words)))

(defn action [req]
  (let [words (get-words req)
        fetcher (:wordstat/fetcher req)
        rss-ch (fetcher words)
        result-ch (async/transduce processor/xf conj {} rss-ch)]
    (async/go
      (let [result (async/<! result-ch)
            json-str (json/generate-string result
                                           {:pretty true})]
        (resp/response json-str)))))

(def handler
  (-> action
      (wrap-params)))
