(ns tryclojure.core
  (:use ring.adapter.jetty
	[hiccup core form-helpers page-helpers]
	[ring.middleware reload stacktrace params file]
	net.cgrand.moustache))

(defn fire-html [text]
  (html
   (:html5 doctype)
   (include-css "/resources/public/css/tryclojure.css")
   [:head
    [:title "TryClojure"]]
   [:body
    [:h1 "Welcome to TryClojure!"]
    [:div.scroll text]
    (form-to [:post "/"]
	     [:input {:name "code" :size 99}]
	     [:p]
	     (submit-button "Make Magic Happen"))]))

(defn handler [{fparams :form-params}]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (fire-html (fparams "code"))})

(def clojureroutes
     (app
      (wrap-reload '(tryclojure.core))
      (wrap-file (System/getProperty "user.dir"))
      (wrap-params)
      (wrap-stacktrace)
      [""] handler))

(defn tryclj [] (run-jetty #'clojureroutes {:port 8081}))