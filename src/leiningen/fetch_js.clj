(ns leiningen.fetch-js
  (:use [leiningen.core :only [prepend-tasks]]
        [leiningen.deps :only [deps]])
  (:require [clojure.java.io :as io]))

(defn fetch-js [project]
  (let [js (io/file "resources" "public" "javascript")
        jquery (io/file js "jquery-1.4.2.min.js")
        console (io/file js "jquery.console.js")]
    (when-not (.exists jquery)
      (println "Downloading jquery.")
      (io/copy (io/reader "http://code.jquery.com/jquery-1.4.2.min.js") jquery))
    (when-not (.exists console)
      (println "Downloading jquery-console.")
      (io/copy (io/reader "https://raw.github.com/chrisdone/jquery-console/055c2c212944349ddbb045e8536ebfb2192acb80/jquery.console.js")
            console))))

(prepend-tasks #'deps fetch-js)