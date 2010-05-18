(ns tryclojure.tutorial
  (:use hiccup.core))

(def tutorial1-text
     (html
      [:div#tuttext
       [:p.bottom 
	"This tutorial is intended for people who have never used Clojure before but are familiar"
	" with at least one other programming language. It's not intended to be a comprehensive tutorial"
	", but to get you started with Clojure, and interested enough to continue." [:br] [:br]
	"Above, you have your REPL. I expect you to try out the examples as we go along, and to experiment"
	" with stuff on your own. If you don't, you will promptly be IP banned from this website, and I will"
	" murder you in your sleep." [:br] [:br] "Please try the examples. Don't make me kill you."]]))

(def tutorial
     (html
      tutorial1-text
      [:div.continue [:input#continue {:type "button" :value "Next"}] [:input#back {:type "button" :value "Back"}]]))

(defn get-tutorial [step]
  (condp = step
    "0" tutorial
    "1" tutorial1-text
    "2" "wtf"))