(ns tryclojure.views.tutorial
  (:require [tryclojure.models.tutorial :as m]
            [net.cgrand.enlive-html :as html]
            [noir.response :as response]))

(defn- try-parse-int
  [n]
  (try
    (Integer/parseInt n)
    (catch NumberFormatException _)))

(defn tutorial-html [page]
  (when-let [n (Integer/parseInt page)]
    (when-let [content (m/get-page n)]
      (apply str (html/emit* (html/unwrap content))))))

(defn tutorial-meta []
  (when-let [metadata (m/get-metadata)]
    (response/json metadata)))
