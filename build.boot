(merge-env!
 :source-paths #{"src/clj"}
 :dependencies '[[org.clojure/clojure "1.9.0-alpha14"]
                 [ring/ring-core "1.5.0"]
                 [cc.qbits/jet "0.7.11"]
                 [org.clojure/core.async "0.2.395"]
                 [mount "0.1.10"]
                 [net.cgrand/xforms "0.9.2"]
                 [org.clojure/data.xml "0.0.8"]
                 [org.clojure/data.zip "0.1.2"]
                 [cheshire "5.7.0"]
                 [ring/ring-mock "0.3.0" :scope "test"]
                 [adzerk/boot-test "1.2.0" :scope "test"]])

(require '[adzerk.boot-test :refer [test]])

(replace-task!
 [t test] (fn [& xs]
            (merge-env! :source-paths #{"test/clj"})
            (apply t xs))
 [r repl] (fn [& xs]
            (merge-env! :source-paths #{"src/clj_dev" "test/clj"})
            (require 'dev)
            (apply r xs)))

(deftask build []
  (comp
   (uber)
   (aot :namespace #{'wordstat.entry})
   (jar :file "wordstat.jar" :main 'wordstat.entry)
   (sift :include #{#"wordstat.jar"})
   (target)))
