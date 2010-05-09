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
		     (function-matcher 'println 'print 'pr 'prn 'var 'print-doc 'doc 'throw
				       'def 'defn 'defmacro))))

(def sc (stringify-sandbox (new-sandbox-compiler :tester sandbox-tester 
						 :timeout 1000)))

(defn execute-text [txt]
  (let [writer (java.io.StringWriter.)]
    (try
     (str writer ((sc txt) {'*out* writer}))
     (catch TimeoutException _ "Execution Timed Out!")
     (catch SecurityException e "Disabled for security purposes.")
     (catch Exception e (str (root-cause e))))))

(defn format-links [& links] (interpose [:br] links))

(defn fire-html [text]
  (let [result (.replaceAll (if (seq text) text "") "\n" "<br />")
	ftext (if (seq result) (html [:p result]) result)]
    (html
     (:html5 doctype)
     (include-css "/resources/public/css/tryclojure.css")
     [:head
      [:title "TryClojure"]]
     [:body
      [:tr]
      [:h1 "Welcome to TryClojure!"]
      [:table {:border "0" :width "100%" :cellpadding "10"}
       [:tr]
       [:td.sides {:width "15%" :valign "top"}
	"Useful links:" [:br] [:br]
	(format-links
	 (link-to "http://clojure.org" "clojure.org")
	 (link-to "http://java.ociweb.com/mark/clojure/article.html" "Clojure Tutorial")
	 (link-to "http://joyofclojure.com/" "The Joy of Clojure")
	 (link-to "http://groups.google.com/group/clojure" "Clojure Mailing List")
	 (link-to "http://www.reddit.com/r/clojure" "Clojure Reddit"))
	[:br] [:br]
	"Personal links:"
	[:br] [:br]
	(format-links
	 (link-to "http://github.com/Raynes/tryclojure" "This site's source code"))]
       [:td {:width "60%"}
	[:div#code.scroll ftext [:a#bottom_div]]
	[:script {:type "text/javascript"} "var objDiv = document.getElementById(\"bottom_div\");
objDiv.scrollIntoView(false);
"]
	(form-to [:post "/"]
		 [:input {:name "code" :size 99}]
		 [:p]
		 (submit-button "Make Magic Happen"))]
       [:td {:width "15%"}]]])))
  
(defn handler [{fparams :form-params session :session}]
  (let [code (StringEscapeUtils/escapeHtml (if (seq (fparams "code")) (fparams "code") ""))
	result (StringEscapeUtils/escapeHtml (if (seq code) (execute-text (fparams "code")) ""))
	sess-history (:history session)
	history (if (seq result) 
		  (str sess-history "=> " code "<br />" result "<br />") 
		  (when (and (seq sess-history) ()) (str sess-history "<br />")))]
    {:status  200
     :headers {"Content-Type" "text/html"}
     :body    (fire-html history)
     :session {:history history}}))

(def clojureroutes
     (app
      (wrap-reload '(tryclojure.core))
      (wrap-session)
      (wrap-file (System/getProperty "user.dir"))
      (wrap-params)
      (wrap-stacktrace)
      [""] handler))

(defn tryclj [] (run-jetty #'clojureroutes {:port 8801}))