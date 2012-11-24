(ns tryclojure.views.home
  (:require [noir.core :refer [defpartial defpage]]
            [hiccup.element :refer [javascript-tag link-to unordered-list]]
            [hiccup.page :refer [include-css include-js html5]]))

(defpartial links []
  (unordered-list
   [(link-to "http://clojure.org" "The official Clojure website")
    (link-to "http://dev.clojure.org/display/doc/Getting+Started" "Getting started with Clojure")
    (link-to "http://groups.google.com/group/clojure" "Clojure mailing list")
    (link-to "http://java.ociweb.com/mark/clojure/article.html" "A comprehensive Clojure tutorial")
    (link-to "http://joyofclojure.com/" "The Joy of Clojure: a book by Michael Fogus and Chris Houser")
    (link-to "http://disclojure.org" "Disclojure")
    (link-to "http://planet.clojure.in" "Planet Clojure")]))

(defpartial about-html []
  [:p.bottom
	 "Welcome to Try Clojure - a quick tour of Clojure for absolute beginners."
	]
  [:p.bottom
	 "Here is our only disclaimer: this site is an introduction to Clojure, not a generic Clojure REPL. "
	 "You won't be able to do everything in it that you could do in your local interpreter. "
	 "Also, the interpreter deletes the data that you enter if you define too many things, or after 15 minutes."]
  [:p.bottom
   "You can find the site's source and such on its "
   (link-to "http://github.com/Raynes/tryclojure" "github")
   " page."]
  [:p.bottom
   "TryClojure is written in Clojure and JavaScript with "
   (link-to "http://webnoir.org" "Noir") ", "
   (link-to "https://github.com/flatland/clojail" "clojail") ", and Chris Done's "
   (link-to "https://github.com/chrisdone/jquery-console" "jquery-console") ". "
   " The design is by " (link-to "http://apgwoz.com" "Andrew Gwozdziewycz") "."
  ])

(defpartial home-html []
  [:p.bottom
    "Welcome to Clojure! "
		"You can see a Clojure interpreter above - we call it a <emp>REPL</emp>."
	]
	[:p.bottom
	"Type \"tutorial\" in the REPL to begin."
	])

(defn root-html []
  (html5
   [:head
    (include-css "/resources/public/css/tryclojure.css"
								 "/resources/public/css/gh-fork-ribbon.css")
    (include-js "http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"
                "/resources/public/javascript/jquery-console/jquery.console.js"
                "/resources/public/javascript/tryclojure.js")
    [:title "Try Clojure"]]
   [:body
    [:div#wrapper
			[:div.github-fork-ribbon-wrapper.right
	     [:div.github-fork-ribbon
	       (link-to "https://github.com/Raynes/tryclojure" "Fork me on GitHub")
				]
			]
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
       [:p.bottom "©2011-2012 Anthony Grimes and numerous contributors."]]
      (javascript-tag
       "var _gaq = _gaq || [];
        _gaq.push(['_setAccount', 'UA-27340918-1']);
        _gaq.push(['_trackPageview']);

        (function() {
          var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
          ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
          var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
        })();")]]]))

(defpage "/" []
  (root-html))

(defpage "/home" []
  (home-html))

(defpage "/about" []
  (about-html))

(defpage "/links" []
  (links))
