(ns vaijoao.websocket
  (:require [org.httpkit.server :refer [send! with-channel on-receive on-close]]
            [clojure.data.json :as json]
            [vaijoao.util :as utils]))

(def games (atom {}))

(defn game-play [room msg] 
  (doseq [g (room @games)]
    (send! g (json/write-str msg)) ) ) 

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
  (with-channel request channel
    (when-not (:closed (bean channel))
      (join-room room channel)
      (when (= 2 (count (room @games)))
        (game-play room  {:action "INITBOARD"
                          :data (utils/make-board (utils/letter-seq 10 10))}))) 
    (on-close channel (fn [status] (leave-room)))
    (on-receive channel (fn [msg] (game-play room {:action "USERPLAY" :data msg})))))


