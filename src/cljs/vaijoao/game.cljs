(ns vaijoao.game
  (:require [clojure.string :as string]))

(def ^:static alphabet
  ["a" "b" "c" "d" "e" "f" "g" "h" "i" "j" "k" "l" "m"
   "n" "o" "p" "q" "r" "s" "t" "u" "v" "x" "y" "w" "z"])

(defn letter-seq
  "Generate a sequence of letters of `n` x `m` length. If only `n` is informed,
  it will be `n` x `n`. This function is destined for creating boards"
  ([n] (letter-seq n n))
  ([n m] (repeatedly (* n m) #(rand-nth alphabet))))

(defn make-board
  "Create a board from a sequence of letters. The sequence have to be of a
   quadradic size.
   If a retangular board is desired, `n` could be specified to be the number of
   letters on each column. In that case the input sequence needs to have a length
   multiple of `n`."
  ([letters]
   (let [n (Math/sqrt (count letters))]
     (assert (integer? n)
             (str "board must have quadradic length, instead of " (count letters)))
     (make-board n letters)))
  ([n letters]
   (assert (zero? (mod (count letters) n))
           (str "board must have a length multiple of " n ", instead of " (count letters)))
   {:rows    (/ (count letters) n)
    :cols    n
    :letters (vec letters)
    :players {}}))

(defn add-player
  "Add new player to board, with associated meta-data"
  [board uuid name color]
  (assoc-in board [:players uuid] {:name     name
                                   :color    color
                                   :score    0
                                   :selected []
                                   :captured #{}}))

(comment
  (make-board ["a" "b" "c"])                                ;; should error!
  (make-board ["a" "b" "c" "d"])
  (-> (make-board ["a" "b" "c" "d"])
      (add-player "f" "foobar" "red")
      (add-player "b" "bazbar" "blue"))
  )

(defn ^:private index-of
  [{:keys [rows cols]} row col]
  {:pre [(and (<= 0 row (dec rows)) (<= 0 col (dec cols)))]}
  (+ (* row cols) col))

(defn ^:private position-of
  [{:keys [cols letters]} index]
  {:pre [(<= 0 index (dec (count letters)))]}
  [(int (/ index cols)) (mod index cols)])

(defn ^:private neighbors-of [{:keys [rows cols] :as board} row col]
  (letfn [(within-bounds? [[r c]] (and (<= 0 r (dec rows))
                                       (<= 0 c (dec cols))))]
    (->> [[row (inc col)] [row (dec col)] [(inc row) col] [(dec row) col]]
         (filter within-bounds?)
         (map (partial apply index-of board))
         (into #{}))))

(defn available?
  "Checks for availability of a letter to be selected. Only letters adjacents
   with the last selected one are available. Any letter is available for an
   empty board."
  [board player row col]
  {:pre [(contains? (:players board) player)]}
  (let [player-selection (get-in board [:players player :selected])
        not-yet-selected (complement (into #{} player-selection))
        current-position (index-of board row col)
        [l-row l-col]    (position-of board (last player-selection))]
    (or (empty? player-selection)
        (and (not-yet-selected current-position)
             (contains? (neighbors-of board l-row l-col) current-position)))))

(defn select
  "Selects a letter by it's row and column index (zero-based)"
  [board player row col]
  {:pre [(available? board player row col)]}
  (let [selected-index (index-of board row col)]
    (-> board
        (update-in [:players player :selected] conj selected-index)
        (update-in [:rindex selected-index] (fnil conj #{}) player))))

(defn clear-selection
  "Clears the current selected letters"
  [board player]
  {:pre [(contains? (:players board) player)]}
  (letfn [(clear-rindex [r] (reduce-kv #(assoc %1 %2 (disj %3 player)) {} r))]
    (-> board
        (update-in [:players player :selected] empty)
        (update-in [:rindex] clear-rindex))))

(defn current-word
  "Fetches the current word formed by the selected letters. Returns a string."
  [{:keys [letters] :as board} player]
  (let [player-selection (get-in board [:players player :selected])]
    (string/join (map letters player-selection))))

(comment
  (-> (make-board ["a" "b" "c" "d"])
      (add-player "f" "foobar" "red")
      (select "f" 0 0)
      (available? "f" 0 1))                                 ;; => true
  (-> (make-board ["a" "b" "c" "d"])
      (add-player "f" "foobar" "red")
      (select "f" 0 0)
      (available? "f" 1 1))                                 ;; => false
  (-> (make-board ["a" "b" "c" "d"])
      (add-player "f" "foobar" "red")
      (select "f" 0 0)
      (select "f" 0 1)
      (select "f" 1 1)
      (current-word "f"))                                   ;; => "abd"
  (-> (make-board ["a" "b" "c" "d"])
      (add-player "f" "foobar" "red")
      (select "f" 0 0)
      (select "f" 0 1)
      (clear-selection "f")
      (current-word "f"))                                   ;; => ""
  )

(defn board-seq
  "Returns the sequence of sequences representing each row of letters. Include
  information about the state of a given letter (who selected if any, link
  direction etc)"
  [{:keys [cols letters rindex players] :as board}]
  (letfn [(make-letter [idx letter] (let [[row col] (position-of board idx)]
                                      {:players (map players (get rindex idx))
                                       :letter  letter
                                       :row     row
                                       :col     col}))]
    (partition cols (map-indexed make-letter letters))))


(comment
  (-> (make-board ["a" "b" "c" "d"])
      (add-player "f" "foobar" "red")
      (add-player "b" "barbaz" "blue")
      (select "f" 0 0)
      (select "b" 0 1)
      (select "f" 0 1)
      (board-seq))
  )

(defn capture-selection
  "Blacklist and clear current selection."
  [board player]
  (if-let [word (not-empty (current-word board player))]
    (-> board
        (update-in [:players player :captured] conj word)
        (update-in [:players player :score] inc)
        (clear-selection player))
    board))

(defn match?
  "Check if the current selected word matches any one from given dictionary."
  [{:keys [players] :as board} player dictionary]
  (let [captured (into #{} (mapcat :captured (vals players)))
        word     (current-word board player)]
    (and (not (captured word)) (contains? dictionary word))))

(comment
  (def board (-> (make-board ["d" "o" "g"
                              "o" "x" "z"
                              "g" "y" "k"])
                 (add-player "f" "foobar" "red")
                 (add-player "b" "bazbar" "blue")))
  (-> board
      (select "f" 0 0)
      (select "f" 1 0)
      (select "f" 2 0)
      (match? "f" #{"dog"}))                                ;; => true
  (-> board
      (select "f" 0 0)
      (select "f" 0 1)
      (select "f" 0 2)
      (match? "f" #{"dog"}))                                ;; => true
  (-> board
      (select "f" 0 0)
      (select "f" 0 1)
      (select "f" 1 1)
      (match? "f" #{"dog"}))                                ;; => false
  (-> board
      (select "f" 0 0)
      (select "f" 1 0)
      (select "f" 2 0)
      (capture-selection "b")
      (capture-selection "f")
      (capture-selection "f"))
  (-> board
      (select "f" 0 0)
      (select "f" 1 0)
      (select "f" 2 0)
      (capture-selection "f")
      (select "b" 0 0)
      (select "b" 0 1)
      (select "b" 0 2)
      (match? "b" #{"dog"}))                                ;; => false
  )
