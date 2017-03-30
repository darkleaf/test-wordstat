(ns wordstat.http
  (:require
   [mount.core :refer [defstate]]
   [clojure.core.async :as async]
   [qbits.jet.server :as server]
   [qbits.jet.client.http :as http]
   [wordstat.handler :refer [handler]])
  (:import
   [java.net URLEncoder]))

(defstate http-client
  :start (http/client {:max-connections-per-destination 10})
  :stop (.stop http-client))

(defn get-rss [client word]
  (let [url (str "http://blogs.yandex.ru/search.rss?numdoc=10&text="
                 (URLEncoder/encode word))]
    (async/go
      (-> (http/get client url)
          (async/<!)
          :body
          (async/<!)))))

(defn make-fetcher [client]
  (fn [words]
    (async/merge (map #(get-rss client %) words))))

(defn wrap-add-fetcher [handler]
  (let [fetcher (make-fetcher http-client)]
    (fn [req]
      (-> req
          (assoc :wordstat/fetcher fetcher)
          (handler)))))

(defstate web-server
  :start (server/run-jetty {:ring-handler (wrap-add-fetcher handler)
                            :port 3000
                            :join? false})
  :stop (.stop web-server))
