(ns tryclojure.core
  (:use ring.adapter.jetty
	[hiccup form-helpers page-helpers]
	[ring.middleware reload stacktrace params file session]
        noir.core
        [noir.response :only [json]]
	[clojure.stacktrace :only [root-cause]]
        [clojail.core :only [sandbox eagerly-consume]]
        [clojail.testers :only [secure-tester-without-def]])
  (:require [noir.server :as server]
            [noir.session :as session])
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

(def github-link (link-to "http://github.com/Raynes/tryclojure" "github"))

(defpartial about-html []
  [:p.bottom
   "Please note that this REPL is sandboxed, so you wont be able to do everything in it "
   "that you would in a local unsandboxed REPL. Keep in mind that this site is designed for "
   "beginners to try out Clojure and not necessarily as a general-purpose server-side REPL."]
  [:p.bottom
   "One quirk you might run into is that things you bind with def can sometimes disappear. "
   "The sandbox wipes defs if you def too many things, so don't be surprised. Furthermore, "
   "The sandbox will automatically be wiped after 15 minutes and if you evaluate more after that,"
   "It'll be in an entirely new namespace/sandbox."]
  [:p.bottom
   "You can find the site's source and such on its "
   github-link
   " page."]
  [:p.bottom
   "TryClojure is written in Clojure and JavaScript (JQuery), powered by "
   (link-to "https://github.com/flatland/clojail" "clojail")
   " and Chris Done's "
   (link-to "https://github.com/chrisdone/jquery-console" "jquery-console")]
  [:p.bottom "Design by " (link-to "http://apgwoz.com" "Andrew Gwozdziewycz")])

(defpartial home-html []
  [:p.bottom
   "Welcome to Try Clojure. See that little box up there? That's a Clojure repl. You can type "
   "expressions and see their results right here in your browser. We also have a brief tutorial to "
   "give you a taste of Clojure. Try it out by typing " [:code.expr "tutorial"] " in the console!"]
  [:p.bottom
   "Check out the site's source on "
   (link-to "http://github.com/Raynes/tryclojure" "github")
   "!"])

(defpartial root-html []
  (html4
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
        [:a#home.buttons "home"]
        [:a#links.buttons "links"]
        [:a#about.buttons.last "about"]]
       [:div#changer (home-html)]]
      [:div.footer
       [:p.bottom "©2011 Anthony Grimes and numerous contributors"]
       [:p.bottom
        "Built with "
        (link-to "http://webnoir.org" "Noir")
        "."]]]]]))

(defpage "/" []
  (root-html))

(defpage "/home" []
  (home-html))

(defpage "/about" []
  (about-html))

(defpage "/links" []
  (links))

(defpage [:post "/tutorial"] {n :n}
  (slurp (str "resources/public/tutorial/page" n ".html")))

(defn eval-form [form sbox]
  (with-open [out (java.io.StringWriter.)]
    (let [result (sbox form {#'*out* out})]
      {:expr form
       :result [out result]})))

(defn eval-string [expr sbox]
  (let [form (binding [*read-eval* false] (read-string expr))]
    (eval-form form sbox)))

(def sandboxes (atom {:counter 0}))

(def try-clojure-tester
  (into secure-tester-without-def
        #{'tryclojure.core}))

(defn add-user [old]
  (let [count (inc (:counter old))]
    (assoc old
      :counter count
      count (sandbox try-clojure-tester
                     :timeout 2000
                     :namespace (symbol (str "sandbox" (rand-int Integer/MAX_VALUE)))))))

(defn eval-request [expr]
  (try
    (eval-string
     expr
     (do
       (if-let [sb (@sandboxes (session/get :sb))]
         sb
         (let [{count :counter} (swap! sandboxes add-user)]
           (session/put! :sb count)
           (future
             (Thread/sleep 600000)
             (-> ((get @sandboxes count) '*ns*) str symbol remove-ns)
             (swap! sandboxes dissoc count))
           (get @sandboxes count)))))
    (catch TimeoutException _
      {:error true :message "Execution Timed Out!"})
    (catch Exception e
      {:error true :message (str (root-cause e))})))

(defpage "/eval.json" {:keys [expr jsonp]}
  (update-in
   (json
    (let [{:keys [expr result error message] :as res} (eval-request expr)]
      (if error
        res
        (let [[out res] result]
          {:expr (pr-str expr)
           :result (str out (pr-str res))}))))
   [:body]
   #(if jsonp (str jsonp "(" % ")") %)))

(server/add-middleware wrap-file (System/getProperty "user.dir"))

(defn to-port [s]
  (when-let [port s] (Long. port)))

(defn tryclj [& [port]]
  (server/start
   (or (to-port port)
       (to-port (System/getenv "PORT")) ;; For deploying to Heroku
       8801)))

(defn -main [& args] (tryclj (first args)))