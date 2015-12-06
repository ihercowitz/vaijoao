(ns vaijoao.utils)

(defn generate-uuid []
  (letfn [(f [] (.toString (rand-int 16) 16))
          (g [] (.toString  (bit-or 0x8 (bit-and 0x3 (rand-int 15))) 16))]
    (clojure.string/join (concat 
                          (repeatedly 8 f) "-"
                          (repeatedly 4 f) "-4"
                          (repeatedly 3 f) "-"
                          (g) (repeatedly 3 f) "-"
                          (repeatedly 12 f)))))

(defn str->keyword [data]
  (into {} 
        (for [[k v] data] 
          [(keyword k) v])))


(defn connect-game [room player]
  (let [host (-> js/window .-location .-host)
        ws (js/WebSocket. (str "ws://" host "/game/" room))]
    (set! (.-onmessage ws) (fn [e]
                             (.log js/console e)
                             (let [str->js (fn [e] (-> e js/JSON.parse js->clj))
                                   json (str->js (.-data e))
                                   action  (get json "action")
                                   data    (get json "data")]
                               (cond 
                                 (= action "INITBOARD") (vaijoao.core/show-board (assoc 
                                                                                  (str->keyword data)
                                                                                  :current-player player))
                                 (= action "USERPLAY")  
                                 (do
                                   (println "Received data: " data)

                                   (vaijoao.board/update-board (str->keyword (str->js data))))  
                                 :else json)))) ws))
