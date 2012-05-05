(ns tryclojure.views.tutorial
  (:require [noir.core :refer [defpage]]))

(defpage [:post "/tutorial"] {n :n}
  (slurp (str "resources/public/tutorial/page" n ".html")))