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


(defn connect-game [room]
  (let [host (-> js/window .-location .-host)
        ws (js/WebSocket. (str "ws://" host "/game/" room))]
    (set! (.-onmessage ws) (fn [e]
                             (let [json (-> (.-data e) 
                                            (js/JSON.parse) 
                                            js->clj)]
                               (.log js/console (get json "action"))
                               (when (= (get json "action") "INITBOARD")
                                 (vaijoao.core/show-board (str->keyword (get-in json ["data"])))))))
    ws))
