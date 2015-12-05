(ns vaijoao.board
  (:require [clojure.string :as string]))

(defn make-board
  "Create a board from a sequence of letters. The sequence have to be of a
   quadradic size"
  [letters]
  (let [n (Math/sqrt (count letters))]
    (assert (integer? n)
            (str "board must have quadradic letters, instead of " (count letters)))
    {:rows     n
     :cols     n
     :letters  letters
     :selected []}))

(comment
  (make-board ["a" "b" "c"])                                ;; should error!
  (make-board ["a" "b" "c" "d"])
  )

(defn ^:private index-of
  [{:keys [rows cols]} row col]
  {:pre [(and (<= 0 row (dec rows)) (<= 0 col (dec cols)))]}
  (+ (* row cols) col))

(defn ^:private neighbors-of
  [{:keys [rows cols] :as board} row col]
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
  [{:keys [selected] :as board} row col]
  (or (empty? selected)
      (contains? (neighbors-of board row col) (last selected))))

(defn select
  "Selects a letter by it's row and column index (zero-based)"
  [board row col]
  {:pre [(available? board row col)]}
  (update board :selected #(conj % (index-of board row col))))

(defn clear-selection
  "Clears the current selected letters"
  [board]
  (assoc board :selected []))

(defn current-word
  "Fetches the current word formed by the selected letters. Returns a string."
  [{:keys [selected letters]}]
  (string/join (map (partial get letters) selected)))

(comment
  (-> (make-board ["a" "b" "c" "d"])
      (select 0 0)
      (available? 0 1))                                     ;; => true
  (-> (make-board ["a" "b" "c" "d"])
      (select 0 0)
      (available? 1 1))                                     ;; => false
  (-> (make-board ["a" "b" "c" "d"])
      (select 0 0)
      (select 0 1)
      (select 1 1)
      (current-word))
  (-> (make-board ["a" "b" "c" "d"])
      (select 0 0)
      (select 0 1)
      (clear-selection)
      (current-word))
  )

(defn board-seq
  "Returns the sequence of sequences representing each row of letters."
  [{:keys [cols letters]}]
  (partition cols letters))

(comment
  (-> (make-board ["a" "b" "c" "d"])
      (board-seq))
  )

(defn match?
  "Check if the current selected word matches any one from given dictionary."
  [board dictionary]
  (contains? dictionary (current-word board)))

(comment
  (def board (make-board ["d" "o" "g"
                          "o" "x" "z"
                          "g" "y" "k"]))
  (-> board (select 0 0) (select 1 0) (select 2 0) (match? #{"dog"})) ;; => true
  (-> board (select 0 0) (select 0 1) (select 0 2) (match? #{"dog"})) ;; => true
  (-> board (select 0 0) (select 0 1) (select 1 1) (match? #{"dog"})) ;; => false
  )
