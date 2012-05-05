(ns tryclojure.server
  (:require [noir.server :as server]
            [ring.middleware.file :refer [wrap-file]]))

(server/add-middleware wrap-file (System/getProperty "user.dir"))
(server/load-views "src/tryclojure/views")

(defn to-port [s]
  (when-let [port s] (Long. port)))

(defn tryclj [& [port]]
  (server/start
   (or (to-port port)
       (to-port (System/getenv "PORT")) ;; For deploying to Heroku
       8801)
   {:session-cookie-attrs {:max-age 600}}))

(defn -main [& args] (tryclj (first args)))