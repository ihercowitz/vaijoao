(ns vaijoao.util)

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
   (let [n (int (Math/sqrt (count letters)))]
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
