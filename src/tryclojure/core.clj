(ns tryclojure.core
  (:use ring.adapter.jetty
	[hiccup form-helpers page-helpers]
	[ring.middleware reload stacktrace params file session]
        noir.core
        [noir.response :only [json]]
	[clojure.stacktrace :only [root-cause]]
        [clojail.core :only [sandbox]]
        [clojail.testers :only [secure-tester-without-def]])
  (:require [noir.server :as server])
  (:import java.io.StringWriter
	   java.util.concurrent.TimeoutException))

(defpartial links []
  (unordered-list 
   [(link-to "http://clojure.org" "The official Clojure website")
    (link-to "http://dev.clojure.org/display/doc/Getting+Started" "Getting started with Clojure")
    (link-to "http://groups.google.com/group/clojure" "Clojure mailing list")
    (link-to "http://java.ociweb.com/mark/clojure/article.html" "A comprehensive Clojure tutorial")
    (link-to "http://joyofclojure.com/" "The Joy of Clojure: a book by Michael Fogus and Chris Houser")
    (link-to "http://disclojure.org" "Disclojure")
    (link-to "http://planet.clojure.in" "Planet Clojure")]))

(defpartial bottom-html []
  [:p.bottom
   "This site is still under construction. I can't promise everything will work correctly."
   " You can find the site's source and such on its " (link-to "http://github.com/Raynes/tryclojure" "github")
   " page."]
  [:p.bottom
   "Please note that this REPL is sandboxed, so you wont be able to do everything in it "
   "that you would in a local unsandboxed REPL. Keep in mind that this site is designed for "
   "beginners to try out Clojure and not necessarily as a general-purpose server-side REPL."]
  [:p.bottom
   "TryClojure is written in Clojure and JavaScript (JQuery), powered by " 
   (link-to "https://github.com/flatland/clojail" "clojail")
   " and Chris Done's "
   (link-to "https://github.com/chrisdone/jquery-console" "jquery-console")]
  [:p.bottom "Design by " (link-to "http://apgwoz.com" "Andrew Gwozdziewyc")])

(defpartial home-text []
  [:p.bottom 
   "Welcome to Try Clojure. Above, you have a Clojure REPL. You can type expressions and see "
   "their results right here in your browser. We also have a brief tutorial to give you a "
   "taste of Clojure. Try it out by typing <code class=\"expr\">tutorial</code> in the console!"])

(defpartial fire-html []  
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
      [:div#changer (home-text)]]
     [:div.footer
      [:p.bottom "©2011 Anthony Grimes (Raynes) and contributors"]
      [:p.bottom "Domain kindly paid for by "
       (link-to "http://blog.licenser.net" "Heinz N. Gies")]]]]])

(defpage "/" []
  (fire-html))

(defpage "/about" []
  (bottom-html))

(defpage "/links" []
  (links))

(defn eval-form [form sbox]
  (with-open [out (java.io.StringWriter.)]
    (let [result (sbox form {#'*out* out})]
      {:expr form
       :result [out result]})))

(defn eval-string [expr sbox]
  (let [form (binding [*read-eval* false] (read-string expr))]
    (eval-form form sbox)))

(defn eval-request [expr]
  (let [sbox (sandbox secure-tester-without-def :timeout 3000)]
    (try
      (eval-string expr sbox)
      (catch TimeoutException _
        {:error true :message "Execution Timed Out!"})
      (catch Exception e
        {:error true :message (.getMessage (root-cause e))}))))

(defpage "/eval.json" {:keys [expr]}
  (prn "expr")
  (json
   (let [{:keys [expr result error message] :as res} (eval-request expr)]
     (if error
       res
       (let [[out res] result]
         {:expr (pr-str expr)
          :result (str out (pr-str res))})))))

(server/add-middleware wrap-session)
(server/add-middleware wrap-file (System/getProperty "user.dir"))
(server/add-middleware wrap-params)
(server/add-middleware wrap-stacktrace)

(defn tryclj [] (server/start 8801))