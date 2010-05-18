(ns tryclojure.tutorial
  (:use hiccup.core))

(defn code [s] (html [:code.code s]))

(def tutorial0-text
     (html
      [:p.bottom 
       "This tutorial is intended for people who have never used Clojure before but are familiar"
       " with at least one other programming language. It's not intended to be a comprehensive tutorial"
       ", but to get you started with Clojure, and interested enough to continue. It's a work in progress, and " 
       "not much of it has been written yet. Check back frequently for new additions." [:br] [:br]
       "Above, you have your REPL. I expect you to try out the examples as we go along, and to experiment"
       " with stuff on your own. If you don't, you will promptly be IP banned from this website, and a teddy "
       "bear will eat your candies."[:br] [:br] "Please try the examples. Don't make teddy eat your candy."]
      [:p.bottom "Hit the pretty little 'Next' button to continue. If you want your candy."]))

(def tutorial1-text
     (html
      [:p.bottom 
       "Let's jump right in there and write some Clojure. Let's start out with the usual 'Hello, World!' that people "
       "seem to love so much. Type the following into the REPL: "]
       (code "(println \"!dlrow ,olleH\")") 
       [:p.bottom
	"Whoa! Something isn't right here, and I'm not talking about the fact that this website's black and white colors"
	" are gloomier than a horror movie either! Hello World has been mangled! It's okay though, because we're smart "
	"and we can figure out how to right this situation. If you're wondering why it printed the message AND 'nil' "
	"it's because 'nil' (which essentially means 'nothing') is println's return value." 
	"Let's try some stuff out until we figure out how to fix it. "
	"Let's try the " (code "reverse") " function, and see if that will fix things. Type this into the REPL: "]
       (code "(reverse \"!dlrow ,olleH\")")
       [:p.bottom
	"Hmm. It did indeed set it right, but this isn't what we were looking for! That doesn't even look like a string. "
	"Reverse took our string, reversed it, and now we have a sequence of characters. You can tell this because each "
	"character has a  '\\' beside it. Those are what Character literals look like in Clojure. "
	"We need a string though! "
	"What do we do when we need a string, but we have something else that isn't stringy? No, we don't go to the shop "
	"and buy string cheese, but instead we use the lovely " (code "str") " function! Try this: "]
       (code "(str (reverse \"!dlrow ,olleH\"))")
       [:p.bottom
	"Ooh! So close. It appears that str is making the entire sequence a string! Indeed. Sequences can be made to "
	" look like strings. str works on any number of arguments. You can pass str any number of arguments and it will "
	"call Java's toString method on each of them to change them into a string, and then it will concatenate them "
	"all into one big juicy string. But all of our characters are stuck in this sequence! How can we get them out "
	"of the sequence, and pass them all to str? Do not fear, young Clojurian, for I have the answer. We use the "
	(code "apply") " function. " (code "apply") " takes a function and a sequence, and it 'unrolls the sequence "
	"and passes all of the elements of the sequence to the function individually. It basically takes the sequence "
	"and dumps out all of it's elements into the argument list of the supplied function. This is exactly what we need. "
	"Let's see if it works. Try this: "]
       (code "(apply str (reverse \"!dlrow ,olleH\"))")
       [:p.bottom
	"Bang! There we go. Impressive. I'm proud of you, I really am. Now, for the final test. Let's tell the world "
	"just how much they mean to us by making the world rotate in the other direction! Let's do this: "]
       (code "(println (apply str (reverse \"!dlrow ,olleH\")))")
      [:p.bottom
       "Congratulations, you have passed step one. Press Next to continue."]))

(def tutorial2-text
     (html
      [:p.bottom
       "Alright. We have greeted the world. Now what? Math. Clojure is great at math. Thanks to the wonderfulness of "
       "S-Expressions, we don't have to worry about precedence rules. In Clojure, operators are Clojure functions or "
       "macros. They work just like any other function. The typical ones are +, -, *, and /. Let's try them out: "]
      (code "(+ 2 2)\n(- 3 2)\n(* 5 5)\n(/ 4 3)")
      [:p.bottom
       "So, that was great. But that last one doesn't quite look right. The problem is that Clojure has a built in "
       "Ratio type. You can confirm this by doing this: " (code "(class (/ 4 3))") "."]
      [:p.bottom 
       "So how do we get actual divison? If you want decimal division, use a floating-point literal for the "
       "dividend: " (code "(/ 4.0 3)") ". If you want to stick to integers, you can use the " (code "quot") " function "
       "for integer divison, and the " (code "rem") " function to get the remainder."]
      [:p.bottom
       "All of the math functions take an arbitrary number of arguments. This means you can do stuff like this: "
       (code "(+ 1 2 3 4 5 6)") ". Go ahead, try it out. You know you want to. Play around with the math functions for "
       "a while. I'll wait. Go ahead."]
      [:p.bottom "That's the end of step two. Press Next to continue to step three. We'll play with sequences."]))

(def tutorial3-text
     (html
      [:p.bottom
       "We have become great Clojury mathematicians together. Now we'll play with sequences."]
      [:p.bottom
       "All of Clojure's data structures are sequences. 'sequence' is an abstraction that lots of data structures "
       "implement. This abstraction allows any function that works on a 'seq' (pronounced seek) to work on any data "
       "structure that is seqable. This means all Clojure and Java collections, strings, and even I/O streams."]
      [:p.bottom
       "Let's play with vectors. A literal vector looks like this: " (code "[1 2 3 \"four\" 5 6.0]") ". Vectors are the "
       "most commonly used collections in Clojure, along with maps. Let's see if we can square all of the elements "
       "of a vector: " (code "(map (fn [x] (* x x)) [1 2 3 4 5])") ". Try that out. The " (code "map") " function takes "
       "a function that takes a single argument and returns a value, and it also takes a sequence. It applies the "
       "function to each element of the sequence in turn, replacing the old value with the new value from the function "
       "and returns the new sequence."]
      [:p.bottom
       "We have introduced something new here: the anonymous function. They are used quite heavily in Clojure code. "
       "An anonymous function is created using " (code "fn") ". After fn, you supply a vector of arguments that the "
       "function takes, and then the body of the function that uses those arguments to computer a value. Clojure has "
       "short hand for anonymous functions as well. The above anonymous function can be rewritten as: "
       (code "#(+ % %)") ". When you use this shorthand, the function arguments are accessed like so: % or %1 for the "
       "first argument, %2 for the second argument, %3 for the third and so on. Our code now looks like this: "
       (code "(map #(* % %) [1 2 3 4 5])")]
      [:p.bottom
       "Another highly important collection is the hashmap. A literal hashmap looks like this: "
       (code "{:key \"value\" :key2 3 :key4 [3 4 2 1]}") ". The keys in this map are something called 'keywords'. "
       "You can tell that they are keywords because they start with a colon ':' character. They're like symbols that "
       "resolve to themselves. Because of this, they're often used as map keys. You can use any object as a map key, "
       "including integers and strings."]
      [:p.bottom
       "To get a value at a key in a map, we can do this: " (code "({:key1 :val} :key1)") ". I bet you're all lolwut "
       "right now, aren't you? In Clojure, hashmaps are functions that take a single argument, which should be a key "
       "and they lookup the key within themselves and return the value at the key or nil if the key doesn't exist. "
       "Another neat trick for maps with keywords for keys is this: " (code "(:key1 {:key1 :val})") ". Keywords are "
       "also functions. They just look themselves up in the map."]
      [:p.bottom
       "Clojure has all sorts of other data structures as well. Sets, lists, zippers, etc. We'll talk about some of "
       "those later on. For now, lets move on to step four. Press Next to continue."]))

(def tutorial
     (html
      [:div#tuttext
       tutorial0-text]
      [:div.continue
       [:input#back {:type "button" :value "Back"}]
       [:input#continue {:type "button" :value "Next"}]]))

(defn get-tutorial [step]
  (condp = step
    "0" tutorial
    "1" tutorial0-text
    "2" tutorial1-text
    "3" tutorial2-text
    "4" tutorial3-text
    "5" "TODO!"))