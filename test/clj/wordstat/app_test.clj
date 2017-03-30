(ns wordstat.app-test
  (:require
   [clojure.test :refer [deftest is]]
   [ring.mock.request :as mock.requeest]
   [clojure.core.async :as async]
   [clojure.data.xml :as xml]
   [cheshire.core :as json]
   [wordstat.handler :refer [handler]]))

(defn- test-rss [& urls]
  (xml/emit-str
   (xml/sexp-as-element
    [:rss
     [:channel
      (map (fn [url] [:item [:link url]]) urls)]])))

(deftest main
  (let [fetcher (fn [_words]
                  (async/to-chan [(test-rss "http://foo.domain.com/some"
                                            "http://bar.domain.com/another")
                                  (test-rss "http://bar.com/1"
                                            "http://bar.com/1")]))
        req (-> (mock.requeest/request :get "/search?query=foo")
                (assoc :wordstat/fetcher fetcher))
        resp (async/<!! (handler req))]
    (is (= 200 (:status resp)))
    (is (= {"domain.com" 2, "bar.com" 1}
           (json/parse-string (:body resp))))))
