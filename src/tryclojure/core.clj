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
	   java.util.concurrent.TimeoutException))

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
       [:tr]
       [:h1 "Welcome to TryClojure!"]
       [:table {:border "0" :width "100%" :height "300" :cellpadding "10"}
	[:tr]
	[:td.left {:align "left"}
	 "Useful links:" [:br] [:br]
	 (unordered-list 
	  [(link-to "http://clojure.org" "clojure.org")
	   (link-to "http://java.ociweb.com/mark/clojure/article.html" "Clojure Tutorial")
	   (link-to "http://joyofclojure.com/" "The Joy of Clojure")
	   (link-to "http://groups.google.com/group/clojure" "Clojure Mailing List")
	   (link-to "http://www.reddit.com/r/clojure" "Clojure Reddit")])
	 [:br] [:br]
	 "Personal links:"
	 [:br] [:br]
	 (format-links
	  (link-to "http://github.com/Raynes/tryclojure" "This site's source code"))]
	[:td.primary [:div#console.console]]
	[:td.right {:width "15%" :align "left"}
	 [:p (str "This is an online Clojure REPL. Just enter your code and press enter and it will be executed.")]
	 [:p "Written by Anthony Simpson (Raynes)."] 
	 [:p "Powered by " (link-to "http://github.com/Licenser/clj-sandbox" "clj-sandbox") " and "
	  (link-to "http://github.com/chrisdone/jquery-console" "jquery-console") " written by Chris Done."]
	 [:p "This website isn't finished. There may still be issues. To find a list of current issues, visit: " 
	  (link-to "http://github.com/Raynes/tryclojure/issues" "Issues")]]]
       [:br] [:br]
       [:div.footer [:p.footer "Copyright 2010 Anthony Simpson. All Rights Reserved."]]]))

(defn handler [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    fire-html})

(defn div-handler [{qparams :query-params}]
    (let [code (StringEscapeUtils/escapeHtml (if (seq (qparams "code")) (qparams "code") ""))
	  result (StringEscapeUtils/escapeHtml (if (seq code) (execute-text (qparams "code")) ""))]
      {:status  200
       :headers {"Content-Type" "text/html"}
       :body    result}))

(def clojureroutes
     (app
      (wrap-reload '(tryclojure.core))
      (wrap-file (System/getProperty "user.dir"))
      (wrap-params)
      (wrap-stacktrace)
      ["magics"] div-handler
      [""] handler))

(defn tryclj [] (run-jetty #'clojureroutes {:port 8801}))