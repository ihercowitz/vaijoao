(ns vaijoao.board
  (:require [vaijoao.game :as game]))

(defonce board-state (-> (game/make-board ["d" "o" "g" "e"
                                           "o" "a" "b" "k"
                                           "g" "e" "m" "p"
                                           "e" "w" "j" "l"])
                         (game/add-player "foobar")
                         (game/add-player "barbaz")))

(defn board-col [col]
  [:td col])

(defn board-row [row]
  [:tr
   (for [col row]
     [board-col col])])

(defn board [board-state]
  [:table {:class "board"}
   [:tbody
    (for [row (game/board-seq board-state)]
      [board-row row])]])

(defn board-page []
  [:div [board board-state]])