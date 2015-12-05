(ns vaijoao.board
  (:require [reagent.core :as r]
            [vaijoao.game :as game]))

(def ^:static selection-border-width
  "Width for selection boarders, keep out of CSS because require calculations."
  5)

(defonce board-state (r/atom (-> (game/make-board (flatten (repeatedly (* 10 10)
                                 (partial rand-nth
                                                  ["a" "b" "c" "d"
                                                   "e" "f" "g" "h"
                                                   "i" "j" "k" "l"
                                                   "m" "n" "o" "p"
                                                   "q" "r" "s" "t"
                                                   "u" "v" "x" "y"
                                                   "w" "z"]))))
                                 (game/add-player "foobar")
                                 (game/add-player "barbaz"))))
                              

(defonce board-players (r/atom {"foobar" {:color "red"}
                                "barbaz" {:color "blue"}}))

(comment
  (swap! board-state game/select "barbaz" 0 2)
  (swap! board-state game/capture-selection "foobar"))

(defn ^:private wrap-with-selection [body player]
  [:span {:class "selection"
          :style {:border-width selection-border-width
                  :border-color (get-in @board-players [player :color])}} body])

(defn letter-box [letter]
  [:span {:class "letter-box"} letter])

(defn ^:private fill-letter-box-with-player-gap [players]
  (* selection-border-width (- (count @board-players) (count players))))

(defn board-col [{:keys [letter players]}]
  [:td {:style {:padding (fill-letter-box-with-player-gap players)}}
   (reduce wrap-with-selection [letter-box letter] players)])

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