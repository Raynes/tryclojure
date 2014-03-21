(ns tryclojure.views.tutorial)

(defn tutorial-html [page] 
  (slurp (str "resources/public/tutorial/" page ".html")))