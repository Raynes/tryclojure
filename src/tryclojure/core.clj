(ns tryclojure.core
  (:use ring.adapter.jetty
	net.licenser.sandbox
	[net.licenser.sandbox tester matcher]
	[hiccup core form-helpers page-helpers]
	[ring.middleware reload stacktrace params file session]
	net.cgrand.moustache
	[clojure.stacktrace :only [root-cause]]
	#_clj-gist.core)
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

;(defn gist-history [text]
;  (post-gist "history.clj" (.replaceAll text "<br />" "\n")))

(defn omg-html [text]
  (let [result (.replaceAll (if (seq text) text "") "\n" "<br />")
	ftext (html [:p.primary result])]
    (html
     [:html4 doctype]
     [:head
      (include-css "/resources/public/css/tryclojure.css")]
     [:script {:type "text/javascript;"} "function scrollDown() {
var objDiv = document.getElementById(\"code\");
objDiv.scrollTop = objDiv.scrollHeight;
}"]
     [:body {:onload "scrollDown()"}
      [:div#code.scroll ftext]
      (form-to [:post "/magics"]
	       [:input#code_input {:name "code" :size 99}]
	       [:br]
	       (submit-button "Make Magic Happen"))
      (form-to [:post "/magics?clear=true"]
	       (submit-button "Clear REPL"))
      [:script {:type "text/javascript"} "document.getElementById(\"code_input\").focus();"]])))

(def fire-html
     (html
      (:html4 doctype)
      [:head
       (include-css "/resources/public/css/tryclojure.css")
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
	[:td.prime 
	 [:iframe.prime {:src "/magics" :frameborder "0"} ""]]
	[:td.right {:width "15%" :align "left"}
	 [:p (str "This is a largely HTML based web application for executing Clojure code and seeing the result. "
		  "Enter your code and press enter (or Make Magic Happen) and your code will be executed. "
		  "It works just like a normal REPL.")]
	 [:p "Written by Anthony Simpson (Raynes)."] 
	 [:p "Powered by " (link-to "http://github.com/Licenser/clj-sandbox" "clj-sandbox.")]
	 [:p "This website isn't finished. There are still some important bugs that need fixed. Here is a link "
	  "to a list currently known issues, and the progress on fixing them: " 
	  (link-to "http://github.com/Raynes/tryclojure/issues" "Issues")]]]
       [:br] [:br]
       [:div.footer [:p.footer "Copyright 2010 Anthony Simpson. All Rights Reserved."]]]))

(defn handler [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    fire-html})

(defn div-handler [{fparams :form-params qparams :query-params session :session}]
    (let [code (StringEscapeUtils/escapeHtml (if (seq (fparams "code")) (fparams "code") ""))
	  result (StringEscapeUtils/escapeHtml (if (seq code) (execute-text (fparams "code")) ""))
	  sess-history (:history session)
	  history (when-not (= "true" (qparams "clear"))
		    (if (seq result) 
		      (str sess-history "=> " code "<br />" result "<br />") 
		      (when (seq sess-history) (str sess-history "<br />"))))]
      {:status  200
       :headers {"Content-Type" "text/html"}
       :body    (omg-html history)
       :session {:history history}}))

(def clojureroutes
     (app
      ;(wrap-reload '(tryclojure.core))
      (wrap-session)
      (wrap-file (System/getProperty "user.dir"))
      (wrap-params)
      (wrap-stacktrace)
      ["magics"] div-handler
      [""] handler))

(defn tryclj [] (run-jetty #'clojureroutes {:port 8801}))