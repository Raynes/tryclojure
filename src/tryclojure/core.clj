(ns tryclojure.core
  (:use ring.adapter.jetty
	net.licenser.sandbox
	[net.licenser.sandbox tester matcher]
	[hiccup core form-helpers page-helpers]
	[ring.middleware reload stacktrace params file session]
	net.cgrand.moustache
	[clojure.contrib.json.write]
	[clojure.stacktrace :only [root-cause]])
  (:import java.io.StringWriter
	   java.util.concurrent.TimeoutException))

(def sandbox-tester
     (extend-tester secure-tester 
		    (whitelist 
		     (function-matcher 'println 'print 'pr 'prn 'var 'print-doc 'doc 'throw))))

(def sc (stringify-sandbox (new-sandbox-compiler :tester sandbox-tester 
						 :timeout 1000)))

(defn execute-text [txt]  
    (try
     (let [writer (java.io.StringWriter.)
	   r ((sc txt) {'*out* writer})]
       {:result (str writer (pr-str r))
	:type (if (nil? r) "nil" (str (type r)))
	:expr txt})
     (catch TimeoutException _ 
       {:exception "Execution Timed Out!"})
     (catch SecurityException e 
       {:exception (str e)})
     (catch Exception e
       {:exception (str (.getMessage (root-cause e)))})))

(defn str-join [stuff] (apply str (interpose "\n" stuff)))

(def main-html (html
   (:html5 doctype)
   [:head
    [:meta {:http-equiv "Content-Type" :content "text/html; charset=UTF-8"}]
    [:title "TryClojure"]
    (include-css "/resources/public/css/tryclojure.css")
    (include-js "/resources/public/js/jquery.js")    
    (include-js "/resources/public/js/jquery.console.js")
    (include-js "/resources/public/js/tryclojure.js")
    ]
   [:body
    [:div#header 
     [:div#logo 
      [:img {:src "/resources/public/images/clojure-icon.gif" :alt "Clojure icon"}]]
     [:div#title [:h1 "Welcome to TryClojure!"]]
     ]
    [:div#console {:class "console"}]
    [:p#note
     "Many thanks to "
     [:a {:href "http://tryhaskel.org"} "tryhaskel"] 
     " their javascript for the repl console is great and we are using it as the base for try-clojure.org." [:br]
     "Also many thanks to Raynes, " [:a {:href "http://tryclj.licenser.net/"} "his code"] " is what this version of try clojure is based on."]
    [:script {:type "text/javascript"}
	      "var gaJsHost = (('https:' == document.location.protocol) ? 'https://ssl.' : 'http://www.');
document.write(unescape(\"%3Cscript src='\" + gaJsHost + \"google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E\"));"
    ]
   [:script {:type "text/javascript"}
    "try {
  var pageTracker = _gat._getTracker('UA-552543-3');
  pageTracker._trackPageview();
} catch(err) {}"]]))

(defn fire-html []
  main-html)

(defn handler [_]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body  (fire-html)})

(defn repl-handler [{params :query-params session :session uri :uri :as request}]
  (let [expr (params "expr")
	result (when (seq expr) (execute-text expr))]
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (json-str result)}))

(def clojureroutes
     (app
      ;(wrap-reload '(tryclojure.core))
      (wrap-session)
      (wrap-file (System/getProperty "user.dir"))
      (wrap-params)
      (wrap-stacktrace)
      [""] handler
      ["clojure.json"] repl-handler))

(defn tryclj [] (run-jetty #'clojureroutes {:port 8081 :encoding "utf-8"}))

(def *server-thread* (Thread. (fn [] (tryclj))))

(.start *server-thread*)