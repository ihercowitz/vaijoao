(ns vaijoao.websocket
  (:require [org.httpkit.server :refer [send! with-channel on-receive on-close]]))

(def games (atom {}))

(defn game-play [room msg] 
  (doseq [g (room @games)]
    (send! g msg) ) ) 

(defn join-room [room cli]
  (->> (conj (room @games) cli)
       (swap! games assoc room)))

(defn leave-room []
  (doseq [r (keys @games)]
    (doseq [g (r @games)]
      (when (:closed (bean g))
        (->> (remove #{g} (r @games))
             (swap! games assoc r))))))

(defn game-room-handler [request room]
  (println "Game room handler")
  (with-channel request channel
    (println "Channel: " (bean channel))
    (when-not (:closed (bean channel))
      (join-room room channel)
      (println games)
      (when (= 2 (count (room @games)))
        (game-play room "/BEGINCARNAGE")))
    (on-close channel (fn [status] (leave-room)))
    (on-receive channel (fn [msg] (game-play room msg)))))


