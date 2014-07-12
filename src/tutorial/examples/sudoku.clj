(ns tutorial.examples.sudoku)

(def candidates (set (range 1 10)))

(defn cell-index
  [cell]
  (let [[row col] (map str cell)
        row-index (.indexOf "ABCDEFGHI" row)
        col-index (.indexOf "123456789" col)]
    (+ col-index (* 9 row-index))))

(def puzzle "530070000
             600195000
             098000060
             800060003
             400803001
             700020006
             060000280
             000419005
             000080079")

(defn parse-int [s] (Integer/parseInt s))

(defn candidates-for [d]
  (if (zero? d) candidates (hash-set d)))

(defn parse-puzzle [puzzle]
  (mapv (comp candidates-for parse-int)
        (re-seq #"\d" puzzle)))

(def grid (parse-puzzle puzzle))

(defn render-cell [x]
  (cond
   (number? x)     x
   (= (count x) 1) (first x)
   :else           "."))

(defn row-render-values [grid row]
  (for [col (range 1 10) :let [cell (str row col)]]
    (render-cell (get grid (cell-index cell)))))

(defn render-grid-row [grid row]
  (apply str
         (flatten
          ["|" (interpose "|" (partition 3 (row-render-values grid row))) "|"])))

(defn render-grid [grid]
  (let [hsep "+---+---+---+"
        rows (map (partial render-grid-row grid) "ABCDEFGHI")]
    (doseq [line (flatten [hsep (interpose hsep (partition 3 rows)) hsep])]
      (println line))))

(defn row-peers [cell]
  (let [[row _] (map str cell)]
    (for [col "123456789"] (str row col))))

(defn col-peers [cell]
  (let [[_ col] (map str cell)]
    (for [row "ABCDEFGHI"] (str row col))))

(defn square-peers [cell]
  (let [[row col] (map str cell)
        rows (case row
               ("A" "B" "C") "ABC"
               ("D" "E" "F") "DEF"
               ("G" "H" "I") "GHI")
        cols (case col
               ("1" "2" "3") "123"
               ("4" "5" "6") "456"
               ("7" "8" "9") "789")]
    (for [row rows col cols] (str row col))))

(defn peers [cell]
  (remove #{cell} (distinct (concat (square-peers cell)
                                    (row-peers cell)
                                    (col-peers cell)))))

(defn eliminate-one [grid cell value-to-eliminate]
  (let [ix         (cell-index cell)
        cell-value (grid ix)]
    (if (number? cell-value)
      (if (= cell-value value-to-eliminate)
        nil
        grid)
      (let [new-candidates (disj cell-value value-to-eliminate)]
        (if (empty? new-candidates)
          nil
          (assoc grid ix new-candidates))))))

(defn eliminate [grid cells value-to-eliminate]
  (reduce (fn [accum cell]
            (let [new-accum (eliminate-one accum cell value-to-eliminate)]
              (if (nil? new-accum)
                (reduced new-accum)
                new-accum)))
          grid
          cells))

(defn assign [grid cell value]
  (let [ix (cell-index cell)]
    (when (contains? (grid ix) value)
      (when-let [new-grid (eliminate grid (peers cell) value)]
        (assoc new-grid ix value)))))

(defn solved? [grid]
  (every? number? grid))

(defn singleton? [cell-value]
  (and (set? cell-value)
       (= (count cell-value) 1)))

(defn find-singleton [grid]
  (first
   (for [row "ABCDEFGHI"
         col "123456789"
         :let [cell (str row col)]
         :when (singleton? (grid (cell-index cell)))]
     cell)))

(defn solve [grid]
  (cond
   (nil? grid) nil
   (solved? grid) grid
   :else (if-let [singleton-cell (find-singleton grid)]
           (let [value (first (grid (cell-index singleton-cell)))
                 next-grid (assign grid singleton-cell value)]
             (recur next-grid))
           grid)))

(defn find-unsolved [grid]
  (first
   (for [row "ABCDEFGHI"
         col "123456789"
         :let [cell (str row col)
               cell-value (grid (cell-index cell))]
         :when (and (set? cell-value) (not (singleton? cell-value)))]
     cell)))

(defn solve [grid]
  (cond
   (nil? grid) nil
   (solved? grid) grid
   :else (if-let [singleton-cell (find-singleton grid)]
           (let [value (first (grid (cell-index singleton-cell)))
                 next-grid (assign grid singleton-cell value)]
             (recur next-grid))
           (let [cell (find-unsolved grid)
                 candidates (grid (cell-index cell))]
             (first (filter identity
                            (map (fn [candidate]
                                   (solve (assign grid cell candidate)))
                                 candidates)))))))
