(ns tryclojure.core
  (:use ring.adapter.jetty
	[net.licenser.sandbox tester matcher]
	[hiccup core form-helpers page-helpers]
	[ring.middleware reload stacktrace params file session]
	net.cgrand.moustache
	[clojure.stacktrace :only [root-cause]]
        [clojail core testers]
	tryclojure.tutorial
        [clojure.set :only [difference]])
  (:import java.io.StringWriter
	   java.util.concurrent.TimeoutException))

(def sb-tester (difference secure-tester #{'def}))

(def state-tester #{'def 'def* 'ensure 'ref-set 'alter 'commute 'swap! 'compare-and-set!})

(defn has-state? [form]
  (check-form form state-tester))

(defn execute-text [txt history]
  (let [sb (sandbox sb-tester :timeout 3000)
        history (if (= 5 (count history)) (drop 1 history) history)
	result (try
                 (loop [history history]
                   (when (not (empty? history))
                     (do
                       (sb (first history))
                       (recur (next history)))))
                 (let [form (binding [*read-eval* false] (read-string txt))]
                   (with-open [writer (java.io.StringWriter.)]
                     (let [r (pr-str (sb form {#'*out* writer}))]
                       [(str (.replace (escape-html writer) "\n" "<br/>") (code (str r)))
                        (if (has-state? form) (conj history form) history)])))
                 (catch OutOfMemoryError _ ["Out of memory error was thrown. Cleaning up all defs." nil])
                 (catch TimeoutException _ ["Execution Timed Out!" history])
                 (catch Exception e [(str (root-cause e)) history]))]
    result))

(def links
     (html (unordered-list 
	    [(link-to "http://clojure.org" "The official Clojure website")
	     (link-to "http://www.assembla.com/wiki/show/clojure/Getting_Started" "Getting started with Clojure")
	     (link-to "http://groups.google.com/group/clojure" "Clojure mailing list")
	     (link-to "http://java.ociweb.com/mark/clojure/article.html" "A comprehensive Clojure tutorial")
	     (link-to "http://joyofclojure.com/" "The Joy of Clojure: a book by Michael Fogus and Chris Houser")
	     (link-to "http://www.pragprog.com/titles/shcloj/programming-clojure" "Programming Clojure, a book by Stuart Halloway")
	     (link-to "http://disclojure.org" "Disclojure")
	     (link-to "http://planet.clojure.in" "Planet Clojure")])))

(def bottom-html
     (html [:p.bottom
	    "This site is still under construction. I can't promise everything will work correctly."
	    " You can find the site's source and such on it's " (link-to "http://github.com/Raynes/tryclojure" "github")
	    " page."]
           [:p.bottom
            "Please note that this REPL is sandboxed, so you wont be able to do everything in it "
            "that you would in a local unsandboxed REPL. Keep in mind that this site is designed for "
            "beginners to try out Clojure and not necessarily as a general-purpose server-side REPL."]
	   [:p.bottom
	    "TryClojure is written in Clojure and JavaScript, powered by " 
	    (link-to "http://github.com/Raynes/clojail" "clojail")
	    " and Chris Done's "
	    (link-to "http://github.com/chrisdone/jquery-console" "jquery-console")]
	   [:p.bottom
	    "Huge thanks to " (link-to "http://www.bestinclass.dk/" "Lau Jensen")
	    " for lots of help with everything ranging from Gimp, to straight up CSS and HTML design tips."]
	   [:p.bottom
	    "Also thanks to my buddy Heinz (Licenser) for all the help with this site. This site is hosted on"
	    " his server, with his domain name. He completely funds this project and it wouldn't be possible without him."]))

(def fire-html
     (html
      (:html4 doctype)
      [:head
       (include-css "/resources/public/css/tryclojure.css")
       (include-js "/resources/public/javascript/jquery-1.4.2.min.js"
		   "/resources/public/javascript/jquery.console.js"
		   "/resources/public/javascript/tryclojure.js")
       [:title "Try Clojure"]]
      [:body
       [:div#wrapper
        [:div#content
         [:div#header
          [:h1 
           [:span.logo-try "Try"] " "
           [:span.logo-clojure "Clo" [:em "j"] "ure"]]]
         [:div#container
          [:div#console.console]
          [:div#buttons
           [:a#tutorial.buttons "tutorial"]
           [:a#links.buttons "links"]
           [:a#about.buttons.last "about"]]
          [:div#changer
           [:p.bottom 
            "Welcome to Try Clojure. Above, you have a Clojure REPL. You can type expressions and see "
            "their results right here in your browser. We also have a brief tutorial to give you a "
            "taste of Clojure. Try it out!"]]]
         [:div.footer
          [:p.bottom "©2010 Anthony Simpson (Raynes) and contributors"]
          [:p.bottom "Domain and hosting kindly provided by "
           (link-to "http://blog.licenser.net" "Heinz N. Gies") "."]]]
        [:script {:type "text/javascript"}
         "var _gaq = _gaq || []; _gaq.push(['_setAccount', 'UA-552543-3']); _gaq.push(['_trackPageview']);
  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();
"]
]]))

(defn handler [{session :session}]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :session session
   :body    fire-html})

(defn div-handler [{qparams :query-params session :session}]
 (let [[result history] (execute-text (qparams "code") (or (:history session) []))]
   {:status  200
    :headers {"Content-Type" "text/txt"}
    :session {:history history}
    :body    result}))

(defn about-handler [{session :session}]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :session session
   :body bottom-html})

(defn link-handler [{session :session}]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :session session
   :body links})

(defn tutorial-handler [{formps :form-params session :session :as req}]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :session session
   :body (get-tutorial (formps "step"))})

(def clojureroutes
     (app
      (wrap-session)
      ;(wrap-reload '(tryclojure.core tryclojure.tutorial))
      (wrap-file (System/getProperty "user.dir"))
      (wrap-params)
      (wrap-stacktrace)
      ["tutorial"] tutorial-handler
      ["links"] link-handler
      ["about"] about-handler
      ["magics"] div-handler
      [""] handler))

(defn tryclj [] (run-jetty #'clojureroutes {:port 8801}))
