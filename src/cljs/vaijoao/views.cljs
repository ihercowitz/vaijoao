(ns vaijoao.views)


(defn new-game []
  (let [uuid (vaijoao.utils/get-uuid)]
    [:div {:class "container"}
     [:div {:class "content"}
      [:h2 "New game"]
      [:br]
      [:strong (str "Room id: " uuid)]]]))
