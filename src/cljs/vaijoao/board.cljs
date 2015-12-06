(ns vaijoao.board
  (:require [reagent.core :as r]
            [vaijoao.game :as game]))

(def ^:static selection-border-width
  "Width for selection boarders, keep out of CSS because require calculations."
  5)

(def board-state (r/atom {}))

(def current-player (r/atom "f"))
(def players ["f" "b"])

(defn set-board [board]
  (swap! board-state merge (-> board
                               (game/add-player @current-player "You" "red")
                               (game/add-player (first (remove #{@current-player} players)) 
                                                  "Opponent" "blue"))))

(comment
  (swap! board-state game/select "b" 0 2)
  (swap! board-state game/capture-selection "f"))

(defn ^:private wrap-with-selection [body {:keys [color]}]
  [:span {:class "selection"
          :style {:border-width selection-border-width
                  :border-color color}} body])

(defn ^:private select-letter [state player row col]
  (if (game/available? state player row col)
    (game/select state player row col)
    state))

(defn letter-box [letter row col]
  [:span {:class    "letter-box"
          :on-click #(swap! board-state select-letter @current-player row col)}
   letter])

(defn ^:private fill-letter-box-with-player-gap [all-players players]
  (* selection-border-width (- (count all-players) (count players))))

(defn board-col [{all-players :players} {:keys [letter players row col]}]
  [:td {:style {:padding (fill-letter-box-with-player-gap all-players players)}}
   (reduce wrap-with-selection [letter-box letter row col] players)])

(defn board-row [board-state row]
  [:tr
   (for [{r :row c :col :as col} row]
     ^{:key [r c]} [board-col board-state col])])

(defn game-board [board-state]
  [:table {:class "board"}
   [:tbody
    (for [[r row] (map-indexed list (game/board-seq board-state))]
      ^{:key r} [board-row board-state row])]])

(defn player-card [{:keys [name color score]}]
  [:li {:class "player"}
   [:span {:class "name"
           :style {:color color}}
    name]
   [:span {:class "score"}
    score]])

(defn ^:private captured-words [players]
  (letfn [(make-words [{:keys [color captured]}]
            (map (partial list color) captured))]
    (sort-by last (mapcat make-words (vals players)))))

(defn score-board [{:keys [players]}]
  [:div {:class "score-board"}
   [:ul {:class "players"}
    (for [[uuid player] players]
      ^{:key uuid} [player-card player])]
   [:ul {:class "captured-words"}
    (for [[color word] (captured-words players)]
      ^{:key word} [:li {:class "word" :style {:color color}} word])]])

(defn board-page []
  [:div {:class "game"}
   [score-board @board-state]
   [game-board @board-state]])

(defn new-board-page [board]
  (set-board board)
  (reset! current-player (:current-player board))
  (board-page))
