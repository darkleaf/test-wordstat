(ns wordstat.entry
  (:require
   [mount.core :as mount]
   [wordstat.http])
  (:gen-class))

(defn -main [& args]
  (mount/start))
