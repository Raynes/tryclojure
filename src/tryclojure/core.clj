(ns tryclojure.core
  (:use ring.adapter.jetty
	net.licenser.sandbox
	[net.licenser.sandbox tester matcher]
	[hiccup core form-helpers page-helpers]
	[ring.middleware reload stacktrace params file session]
	net.cgrand.moustache
	[clojure.stacktrace :only [root-cause]])
  (:import java.io.StringWriter
	   org.apache.commons.lang.StringEscapeUtils
	   java.util.concurrent.TimeoutException
	   java.net.URLDecoder))

(def sandbox-tester
     (extend-tester secure-tester 
		    (whitelist 
		     (function-matcher 'println 'print 'pr 'prn 'var 'print-doc 'doc 'throw))))

(def sc (stringify-sandbox (new-sandbox-compiler :tester sandbox-tester 
						 :timeout 1000)))

(defn execute-text [txt]
  (let [writer (java.io.StringWriter.)
	result (try
		(pr-str ((sc txt) {'*out* writer}))
		(catch TimeoutException _ "Execution Timed Out!")
		(catch SecurityException _ "Disabled for security purposes.")
		(catch Exception e (str (root-cause e))))]
    (str writer result)))

(defn format-links [& links] (interpose [:br] links))

(def fire-html
     (html
      (:html4 doctype)
      [:head
       (include-css "/resources/public/css/tryclojure.css")
       (include-js "/resources/public/javascript/jquery-1.4.2.min.js"
		   "/resources/public/javascript/jquery.console.js"
		   "/resources/public/javascript/tryclojure.js")
       [:title "TryClojure"]]
      [:body
       [:div#container [:div#console.console]
	[:div.bottom
	 [:p.status 
	  "This site is still under construction. I can't promise everything will work correctly."
	  [:br] [:br]
	  "TryClojure is written in Clojure and JavaScript, powered by " 
	  (link-to "http://github.com/Licenser/clj-sandbox" "clj-sandbox")
	  "and Chris Done's "
	  (link-to "http://github.com/chrisdone/jquery-console" "jquery-console")
	  [:br] [:br]
	  "Huge thanks to " (link-to "http://www.bestinclass.dk/" "Lau Jensen")
	  " for lot's of help with everything ranging from Gimp, to straight up CSS and HTML design tips."]]]
       [:div.footer [:p.footer "Copyright 2010 Anthony Simpson. All Rights Reserved."]]]))

(defn handler [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    fire-html})

(defn div-handler [{qparams :query-params}]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (StringEscapeUtils/escapeHtml (execute-text (qparams "code")))})

(def clojureroutes
     (app
      (wrap-reload '(tryclojure.core))
      (wrap-file (System/getProperty "user.dir"))
      (wrap-params)
      (wrap-stacktrace)
      ["magics"] div-handler
      [""] handler))

(defn tryclj [] (run-jetty #'clojureroutes {:port 8801}))