(ns vaijoao.board
  (:require [reagent.core :as r]
            [vaijoao.game :as game]))

(defonce board-state (r/atom (-> (game/make-board ["d" "o" "g" "e"
                                                   "o" "a" "b" "k"
                                                   "g" "e" "m" "p"
                                                   "e" "w" "j" "l"])
                                 (game/add-player "foobar")
                                 (game/add-player "barbaz"))))

(defonce board-players (r/atom {"foobar" {:color "red"}
                                "barbaz" {:color "blue"}}))

(comment
  (swap! board-state game/select "foobar" 0 0)
  (swap! board-state game/capture-selection "foobar"))

(defn ^:private wrap-with-selection [body player]
  [:span {:class "player-selection"
          :style {:display "inline-block"
                  :border-width 3
                  :border-style "solid"
                  :border-color (get-in @board-players [player :color])}} body])

(defn letter-box [letter]
  [:span {:style {:display     "inline-block"
                  :width       "2em"
                  :height      "2em"
                  :text-align  "center"
                  :line-height "2em"}} letter])

(defn board-col [{:keys [letter players]}]
  (letfn [(fill-letter-box-with-player-gap [players]
            (* 3 (- (count @board-players) (count players))))]
    [:td {:style {:padding (fill-letter-box-with-player-gap players)}}
     (reduce wrap-with-selection [letter-box letter] players)]))

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
  [:div [board @board-state]])