(ns tryclojure.tutorial
  (:use hiccup.core))

(defn code [s] (html [:code.code s]))

(def tutorial1-text
     (html
      [:p.bottom 
       "This tutorial is intended for people who have never used Clojure before but are familiar"
       " with at least one other programming language. It's not intended to be a comprehensive tutorial"
       ", but to get you started with Clojure, and interested enough to continue." [:br] [:br]
       "Above, you have your REPL. I expect you to try out the examples as we go along, and to experiment"
       " with stuff on your own. If you don't, you will promptly be IP banned from this website, and I will"
       " murder you in your sleep." [:br] [:br] "Please try the examples. Don't make me kill you."]
      [:br] [:br]
      "Hit the pretty little 'Next' button to continue. If you want to live."))

(def tutorial2-text
     (html
      [:p.bottom 
       "Let's jump right in there and write some Clojure. Let's start out with the usual 'Hello, World!' that people "
       "seem to love so much. Type the following into the REPL: " 
       [:br] [:br] 
       (code "(println \"!dlrow ,olleH\")") 
       [:br] [:br]
       "Whoa! Something isn't right here, and I'm not talking about the fact that this website's black and white colors"
       " are gloomier than a horror movie either! Hello World has been mangled! It's okay though, because I know how "
       "to right this situation. Let's try some stuff out until we figure out how to fix it. Let's try the "
       (code "reverse") " function, and see if that fixes things. Type this into the REPL: "
       [:br] [:br]
       (code "(reverse \"!dlrow ,olleH\")")
       [:br] [:br]
       "Hmm. It did indeed set it right, but this isn't what we were looking for! That doesn't even look like a string. "
       "Reverse took our string, reversed it, and now we have a sequence of characters. We need a string though! "
       "What do we do when we need a string, but we have something else that isn't stringy? No, we don't go to the shop "
       "and buy string cheese, but instead we use the lovely " (code "str") " function! Try this: "
       [:br] [:br]
       (code "(str (reverse \"!dlrow ,olleH\"))")
       [:br] [:br]
       "Ooh! So close. It appears that str is making the entire sequence a string! Indeed. Sequences can be made to "
       " look like strings. str works on any number of arguments. You can pass str any number of arguments and it will "
       "call Java's toString method on each of them to change them into a string, and then it will concatenate them "
       "all into one big juicy string. But all of our characters are stuck in this sequence! How can we get them out "
       "of the sequence, and pass them all to str? Do not fear, young Clojurian, for I have the answer. We use the "
       (code "apply") " function. " (code "apply") " takes a function and a sequence, and it 'unrolls the sequence "
       "and passes all of the elements of the sequence to the function individually. It basically takes the sequence "
       "dumps out all of it's elements into the argument list of the supplied function. This is exactly what we need. "
       "Let's see if it works. Try this: "
       [:br] [:br]
       (code "(apply str (reverse \"!dlrow ,olleH\"))")
       [:br] [:br]
       "Bang! There we go. Impressive. I'm proud of you, I really am. Now, for the final test. Let's tell the world "
       "just how much they mean to us by making the world rotate in the other direction! Let's do this: "
       [:br] [:br]
       (code "(println (apply str (reverse \"!dlrow ,olleH\")))")
       [:br] [:br]
       "Congratulations, you have passed step two. Press Next to continue."]))

(def tutorial
     (html
      [:div#tuttext
       tutorial1-text]
      [:div.continue
       [:input#back {:type "button" :value "Back"}]
       [:input#continue {:type "button" :value "Next"}]]))

(defn get-tutorial [step]
  (condp = step
    "0" tutorial
    "1" tutorial1-text
    "2" tutorial2-text))