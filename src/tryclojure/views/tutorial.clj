(ns tryclojure.views.tutorial
  (:use [noir.core :only [defpage]]))

(defpage [:post "/tutorial"] {n :n}
  (slurp (str "resources/public/tutorial/page" n ".html")))