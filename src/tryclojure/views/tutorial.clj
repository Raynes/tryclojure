(ns tryclojure.views.tutorial
  (:require [tryclojure.models.tutorial :as m]
            [clojure.string :as str]
            [net.cgrand.enlive-html :as html]
            [noir.response :as response]))

(defn- try-parse-int
  [n]
  (try
    (Integer/parseInt n)
    (catch NumberFormatException _)))

(defn count-leading-whitespace
  [s]
  (count (take-while #(Character/isWhitespace %) s)))

(defn trim-leading-whitespace
  [n]
  (fn [s]
    (subs s (min n (count-leading-whitespace s)))))

(defn clean-whitespace
  [s]
  (let [lines (remove str/blank? (str/split-lines s))
        n (count-leading-whitespace (first lines))]
    (str/join "\n" (map (trim-leading-whitespace n) lines))))

(defn strip-blank-lines
  [s]
  (if (and (string? s) (str/blank? s))
    nil
    s))

(defn format-code-blocks
  [node]
  (first
   (html/at node
            [:pre.codeblock] (html/transform-content strip-blank-lines)
            [:pre.codeblock :code] (html/transform-content clean-whitespace))))

(defn tutorial-html [page]
  (when-let [n (Integer/parseInt page)]
    (when-let [content (m/get-page n)]
      (apply str (html/emit* (html/unwrap (format-code-blocks content)))))))

(defn tutorial-meta []
  (when-let [metadata (m/get-metadata)]
    (response/json metadata)))
