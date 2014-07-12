(ns tryclojure.server
  (:use compojure.core)
  (:require [compojure.route :as route]
            [noir.util.middleware :as nm]
            [noir.session :as session]
            [clojure.tools.logging :as log]
            [ring.adapter.jetty :as jetty]
            [tryclojure.views.home :as home]
            [tryclojure.views.tutorial :as tutorial]
            [tryclojure.views.eval :as eval])
  (:import java.util.UUID))

(defn wrap-session-id
  "Middleware to assign a UUID for tracking page views. The main app
   does not create a cookie until the first attempt to evaluate Clojure
   code in the REPL. This middleware assures a UUID and session cookie
   are assigned on the first page view."
  [handler]
  (fn [request]
    (session/swap! (fn [sess]
                     (if (get sess :uuid)
                       sess
                       (assoc sess :uuid (.toString (UUID/randomUUID))))))
    (handler request)))

(defn wrap-log-request
  [handler]
  (fn [request]
    (log/debug request)
    (handler request)))

(defn wrap-log-pageview
  [handler]
  (fn [request]
    (when (= (:uri request) "/tutorial")
      (log/info "PAGEVIEW" (session/get :uuid) (get-in request [:params :page])))
    (handler request)))

(def app-routes
  [(GET "/" [] (home/root-html))
   (GET "/about" [] (home/about-html))
   (GET "/links" [] (home/links-html))
   (GET "/metadata.json" [] (tutorial/tutorial-meta))
   (POST "/tutorial" [:as {args :params}] (tutorial/tutorial-html (args :page)))
   (POST "/eval.json" [:as {args :params}] (eval/eval-json (args :expr) (args :jsonp)))
   (GET "/eval.json" [:as {args :params}] (eval/eval-json (args :expr) (args :jsonp)))
   (route/resources "/")
   (route/not-found "Not Found")])

(def app (nm/app-handler app-routes :middleware [wrap-session-id wrap-log-request wrap-log-pageview]))

(defn -main [port]
  (jetty/run-jetty app {:port (Long. port) :join? false}))
