(ns vaijoao.server
  (:require [vaijoao.handler :refer [app]]
            [environ.core :refer [env]]
            [org.httpkit.server :refer [run-server]])
  (:gen-class))

(def server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)
    (println "Server has been stoped")))

(defn start-server [port]
  (reset! server (run-server #'app {:port port})))


(defn -main [& args]
  (let [port (Integer/parseInt (or (env :port) "3000"))]
    (println "Server is started on port " port)
    (start-server port)))
