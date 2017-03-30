(ns wordstat.fetcher
  (:require
   [clojure.core.async :as async]
   [qbits.jet.client.http :as http])
  (:import
   [java.net URLEncoder]))

(defn- get-rss [client word]
  (let [url (str "http://blogs.yandex.ru/search.rss?numdoc=10&text="
                 (URLEncoder/encode word))]
    (async/go
      (let [resp (async/<! (http/get client url))
            body (:body resp)]
        ;; reject request exeptions
        (when body
          (async/<! body))))))

(defn fetcher [client words]
  (let [c-get-rss (partial get-rss client)]
    (async/merge (map c-get-rss words))))
