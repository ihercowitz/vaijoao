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

(defn select
  "Selects a letter by it's row and column index (zero-based)"
  [board row col]
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
      (select 0 1)
      (select 1 1)
      (current-word))
  (-> (make-board ["a" "b" "c" "d"])
      (select 0 0)
      (select 1 1)
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
