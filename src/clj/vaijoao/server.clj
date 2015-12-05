(ns vaijoao.server
  (:require [vaijoao.handler :refer [app]]
            [environ.core :refer [env]]
            [org.httpkit.server :refer [run-server]])
  (:gen-class))

 (defn -main [& args]
   (let [port (Integer/parseInt (or (env :port) "3000"))]
     (println "Server is started on port " port)
     (run-server app {:port port :join? false})))
