(ns tryclojure.core
  (:use ring.adapter.jetty
	[hiccup core form-helpers page-helpers]
	[ring.middleware reload stacktrace params file session]
	net.cgrand.moustache
	[clojure.stacktrace :only [root-cause]]
        [clojail core testers]
  [clojure.set :only [difference]])
  (:require [clojure.contrib.json :as json])
  (:import java.io.StringWriter
	   java.util.concurrent.TimeoutException))

(def sb-tester (difference secure-tester #{'def}))

(def state-tester #{'def 'def* 'ensure 'ref-set 'alter 'commute 'swap! 'compare-and-set!})

(defn has-state? [form]
  (check-form form state-tester))

(defn eval-form [form sbox]
  (with-open [out (java.io.StringWriter.)]
    (let [result (sbox form {#'*out* out})]
      {:expr form
       :result [out result]})))

(defn eval-string [expr sbox]
  (let [form (binding [*read-eval* false] (read-string expr))]
    (eval-form form sbox)))

(defn eval-request [{params :params {history :history} :session}]
  (let [sbox (sandbox sb-tester :timeout 3000)]
    (try
      ;; re-eval history forms
      (doseq [form history] (eval-form form sbox))
      ;; eval request parameter
      (eval-string (params "expr") sbox)
      (catch OutOfMemoryError _
        {:error true :message "Out of memory error was thrown. Cleaning up all defs."})
      (catch TimeoutException _
        {:error true :message "Execution Timed Out!"})
      (catch Exception e
        {:error true :message (.getMessage (root-cause e))}))))

(def links
     (html (unordered-list 
	    [(link-to "http://clojure.org" "The official Clojure website")
	     (link-to "http://dev.clojure.org/display/doc/Getting+Started" "Getting started with Clojure")
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
           [:a#links.buttons "links"]
           [:a#about.buttons.last "about"]]
          [:div#changer
           [:p.bottom 
            "Welcome to Try Clojure. Above, you have a Clojure REPL. You can type expressions and see "
            "their results right here in your browser. We also have a brief tutorial to give you a "
            "taste of Clojure. Try it out by typing <code>tutorial</code> in the console!"]]]
         [:div.footer
          [:p.bottom "©2011 Anthony Grimes (Raynes) and contributors"]
          [:p.bottom "Domain kindly paid for by "
           (link-to "http://blog.licenser.net" "Heinz N. Gies")]]]]]))

(defn handler [{session :session}]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :session session
   :body    fire-html})

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

(def ^{:private true}
  eval-response-defaults
  {:status 200
   :headers {"Content-Type" "application/json"}})

(defn eval-handler [request]
  (let [{:keys [expr result error message] :as res} (eval-request request)
        history (get-in request [:session :history] [])]
    (if error
      (merge eval-response-defaults
             {:session (:session request)
              :body (json/json-str res)})
      (let [[out res] result]
        (merge eval-response-defaults
               {:session {:history (conj history expr)}
                :body (json/json-str {:expr (pr-str expr)
                                      :result (str out (pr-str res))})})))))

(defn- max-history [max history]
  (if (> (count history) max)
    (drop 1 history)
    history))

(defn wrap-post-history [handler]
  (fn [request]
    (let [response (handler request)]
      (->> (get-in response [:session :history] [])
           (max-history 5)
           (filter has-state?)
           vec
           (assoc-in response [:session :history])))))

(def clojureroutes
     (app
      (wrap-session)
      (wrap-post-history)
      ;(wrap-reload '(tryclojure.core tryclojure.tutorial))
      (wrap-file (System/getProperty "user.dir"))
      (wrap-params)
      (wrap-stacktrace)
      ["links"] link-handler
      ["about"] about-handler
      ["eval.json"] eval-handler
      [""] handler))

(defn tryclj [] (run-jetty #'clojureroutes {:port 8801}))
