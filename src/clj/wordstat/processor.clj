(ns wordstat.processor
  (:require
   [clojure.string :as str]
   [net.cgrand.xforms :as xforms]
   [clojure.zip :as zip]
   [clojure.data.xml :as xml]
   [clojure.data.zip.xml :as zip-xml])
  (:import [java.net URI]))

(defn- get-links [content]
  (zip-xml/xml-> (zip/xml-zip (xml/parse-str content))
                 (zip-xml/tag= :rss)
                 (zip-xml/tag= :channel)
                 (zip-xml/tag= :item)
                 (zip-xml/tag= :link)
                 zip-xml/text))

(defn- get-host [link]
  (let [host (.getHost (URI. link))
        segments (str/split host #"\.")
        result-segments (take-last 2 segments)]
     (str/join "." result-segments)))

(def xf
  (comp
   (map get-links)
   cat
   (distinct)
   (map get-host)
   (xforms/by-key identity xforms/count)))
