(ns tryclojure.core
  (:use ring.adapter.jetty
	net.licenser.sandbox
	[net.licenser.sandbox tester matcher]
	[hiccup core form-helpers page-helpers]
	[ring.middleware reload stacktrace params file session]
	net.cgrand.moustache
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
  (let [writer (java.io.StringWriter.)]
    (try
     (str writer ((sc txt) {'*out* writer}))
     (catch TimeoutException _ "Execution Timed Out!")
     (catch SecurityException e e)
     (catch Exception e (.getMessage (root-cause e))))))

(defn str-join [stuff] (apply str (interpose "\n" stuff)))

(defn fire-html [text]
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
     [:td.sides {:width "10%" :valign "top"}
      [:div "Ohai"]]
     [:td {:width "80%"}
      [:div#code.scroll text]
      [:script {:type "text/javascript"} "var objDiv = document.getElementById(\"code\");
window.scrollTo(0,objDiv.offsetTop+objDiv.offsetHeight);;
"]
      (form-to [:post "/"]
	       [:input {:name "code" :size 99}]
	       [:p]
	       (submit-button "Make Magic Happen"))]
     [:td {:width "10%"}]]]))
  
(defn handler [{fparams :form-params session :session}]
  (let [result (execute-text (fparams "code"))
	history (apply str (concat (:history session) result))]
    {:status  200
     :headers {"Content-Type" "text/html"}
     :body    (fire-html history)
     :session {:history history}}))

(def clojureroutes
     (app
      ;(wrap-reload '(tryclojure.core))
      (wrap-session)
      (wrap-file (System/getProperty "user.dir"))
      (wrap-params)
      (wrap-stacktrace)
      [""] handler))

(defn tryclj [] (run-jetty #'clojureroutes {:port 8081}))