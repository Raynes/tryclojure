(ns tryclojure.models.tutorial
  (:require [clojure.java.io :as io]
            [net.cgrand.enlive-html :as html]))

(def tutorial-resource (io/resource "public/tutorial.html"))

(defn get-metadata
  []
  (let [tutorial (html/html-resource tutorial-resource)]
    (map (fn [{:keys [attrs]}]
           (into {} (for [k (keys attrs) :when (.startsWith (name k) "data-")]
                      [(keyword (subs (name k) 5)) (attrs k)])))
         (html/select tutorial [:div.page]))))

(defn get-page
  [n]
  (let [tutorial (html/html-resource tutorial-resource)]
    (nth (html/select tutorial [:div.page]) n)))
