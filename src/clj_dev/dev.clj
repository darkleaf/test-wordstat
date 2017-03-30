(ns dev
  (:require
   [mount.core :as mount]
   [wordstat.http]))

(defn start []
  (mount/start))

(defn stop []
  (mount/stop))

(defn restart []
  (stop)
  (start))
