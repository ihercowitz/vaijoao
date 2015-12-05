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


(defn connect-game [room]
  (let [host (-> js/window .-location .-host)
        ws (js/WebSocket. (str "ws://" host "/game/" room))]
    (set! (.-onmessage ws) (fn [e] 
                             (if (= (.-data e) "/BEGINCARNAGE")
                               (vaijoao.core/show-board)
                               
                               (.log js/console (.-data e)))))
    ws))
