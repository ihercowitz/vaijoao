(ns vaijoao.board
  (:require [reagent.core :as r]
            [vaijoao.game :as game]))

(def ^:static selection-border-width
  "Width for selection boarders, keep out of CSS because require calculations."
  5)

(defonce board-state (r/atom (-> (game/make-board (game/letter-seq 10))
                                 (game/add-player "foobar")
                                 (game/add-player "barbaz"))))

(defonce board-players (r/atom {"foobar" {:color "red"}
                                "barbaz" {:color "blue"}}))

(defonce current-player (r/atom "foobar"))

(comment
  (swap! board-state game/select "barbaz" 0 2)
  (swap! board-state game/capture-selection "foobar"))

(defn ^:private wrap-with-selection [body player]
  [:span {:class "selection"
          :style {:border-width selection-border-width
                  :border-color (get-in @board-players [player :color])}} body])

(defn ^:private select-letter [state player row col]
  (if (game/available? state player row col)
    (game/select state player row col)
    state))

(defn letter-box [letter row col]
  [:span {:class    "letter-box"
          :on-click #(swap! board-state select-letter @current-player row col)}
   letter])

(defn ^:private fill-letter-box-with-player-gap [players]
  (* selection-border-width (- (count @board-players) (count players))))

(defn board-col [{:keys [letter players row col]}]
  [:td {:style {:padding (fill-letter-box-with-player-gap players)}}
   (reduce wrap-with-selection [letter-box letter row col] players)])

(defn board-row [row]
  [:tr
   (for [{r :row c :col :as col} row]
     ^{:key [r c]} [board-col col])])

(defn board [board-state]
  [:table {:class "board"}
   [:tbody
    (for [[r row] (map-indexed list (game/board-seq board-state))]
      ^{:key r} [board-row row])]])

(defn board-page []
  [:div [board @board-state]])