(ns wordstat.http
  (:require
   [mount.core :refer [defstate]]
   [qbits.jet.server :as server]
   [qbits.jet.client.http :as http]
   [wordstat.fetcher :refer [fetcher]]
   [wordstat.handler :refer [handler]]))

(def port (or (System/getenv "HTTP_PORT")
              3000))

(def max-connections-per-destination
  (or (System/getenv "MAX_CONNECTIONS_PER_DESTINATION")
      10))

(defstate http-client
  :start (http/client {:max-connections-per-destination max-connections-per-destination})
  :stop (http/stop-client! http-client))

(defn wrap-add-fetcher [handler]
  (let [c-fetcher (partial fetcher http-client)]
    (fn [req]
      (-> req
          (assoc :wordstat/fetcher c-fetcher)
          (handler)))))

(defstate web-server
  :start (server/run-jetty {:ring-handler (wrap-add-fetcher handler)
                            :port port
                            :join? false})
  :stop (.stop web-server))
