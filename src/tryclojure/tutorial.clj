(ns tryclojure.tutorial
  (:use hiccup.core))

(defn code [s] (html [:code.code s]))

(def tutorial0-text
     (html
      [:p.bottom 
       "This tutorial is intended for people who have never used Clojure before but have experience with "
       " other programming languages.  This is not meant to be a comprehensive tutorial"
       ", but instead intended to get you started with Clojure -- and hopefully interested enough to continue.  " 
       "Please bear in mind that this tutorial is evolving, so check back frequently for new additions." [:br] [:br]
       "Above, you have your REPL.  Please try the examples as we go along, and by all means experiment"
       " with concepts indpendently.  Should you refuse, you will promptly be IP banned from this website, and a teddy "
       "bear will eat your candies. ;-)"[:br] [:br] "Don't make teddy eat your candy."]
      [:p.bottom "Hit the pretty little 'Next' button to continue for the love of Pete!"]))

(def tutorial1-text
     (html
      [:p.bottom 
       "Let's jump right in there and write some Clojure. Let's start out with the familiar 'Hello, World!'  People "
       "seem to love it so much. Type the following into the REPL: "]
       (code "(println \"!dlrow ,olleH\")") 
       [:p.bottom
	"Something isn't right here! (I'm not talking about the fact that this website's lack of panache)  "
	"It seems that 'Hello World' has been mangled!  It's okay though, because we're smart "
	"and we can figure out how to remedy this situation.  If you're wondering why it printed our gibberish AND 'nil' "
	"it's because 'nil' (which essentially means 'nothing') is println's return value." 
	"Let's experiment until we figure out what went wrong.  "
	"It seems that the " (code "reverse") " should fix things -- so type the following into the REPL: "]
       (code "(reverse \"!dlrow ,olleH\")")
       [:p.bottom
	"Hmm. It did indeed set it right, but this isn't what we were looking for!  That doesn't even look like a string. "
	"Reverse took our string, reversed it, and now we have a sequence of characters.  You can tell this because each "
	"character has a  '\\' (backslash in layman's terms) preceeding it.  Clojure uses those to denote Character (big 'C' character) literals.  "
	"We need a string though! "
	"What do we do when we need a string, but we have something else that isn't stringy (a highly technical term)?  No, we don't go to the shop "
	"and buy string cheese (not to disparage string sheese mind you), but instead we use the lovely " (code "str") " function!  Try this: "]
       (code "(str (reverse \"!dlrow ,olleH\"))")
       [:p.bottom
	"Ooh! So close.  It appears that the str function is making the entire sequence a string!  Sequences can be made to "
	" look like strings.  str works on any number of arguments.  You can pass str any number of arguments and it will "
	"call Java's toString method on each of them to change them into a string, and then it will concatenate them "
	"all into one mammoth string.  Unfortunately, all of our characters are stuck in this sequence!  How can we get them out "
	"of the sequence, and pass them all to str?  Do not fear, young Clojurian, for I have the answer.  We use the "
	(code "apply") " function.  " (code "apply") " takes a function and a sequence, and 'unrolls' the sequence, "
	"passing all of its elements to the function as arguments.  Basically, it takes the sequence "
	"and dumps its elements into the argument list of the supplied function; which is exactly what we need.  "
	"Let's see if it works...  try this: "]
       (code "(apply str (reverse \"!dlrow ,olleH\"))")
       [:p.bottom
	"Ka-chow!  There we go.  Impressive.  I'm proud of you, I really am.  Now, for the final test.  Let's tell the world "
	"just how much they mean to us by making the world rotate in the other direction! Let's do this: "]
       (code "(println (apply str (reverse \"!dlrow ,olleH\")))")
      [:p.bottom
       "Congratulations, you have passed step one.  Press Next to continue."]))

(def tutorial2-text
     (html
      [:p.bottom
       "Alright.  We have greeted the world.  Now what?  Math?  Clojure is great at math.  Thanks to the uniformity of "
       "prefix notation, we don't have to worry about precedence rules; which we kinda loathe anyway.  This goes before that, and that goes before that other thing... blah!  In Clojure, mathematical operators work like any other function."
       "  The common operators are +, -, *, and /.  Let's try them out: "]
      (code "(+ 2 2)\n(- 3 2)\n(* 5 5)\n(/ 4 3)")
      [:p.bottom
       "So, that was great.  But that last one doesn't quite look right.  The problem is that Clojure has a built in "
       "Ratio type.  You can confirm this by doing this: " (code "(class (/ 4 3))") "."]
      [:p.bottom 
       "So how do we perform divison that we are familiar with?  If you want decimal division, use a floating-point literal for either "
       "the numerator or denominator: " (code "(/ 4.0 3)") ".  If you want to stick to integer math, you can use the " (code "quot") " function, "
       "or the " (code "rem") " function to get the remainder."]
      [:p.bottom
       "All of the math functions take an arbitrary number of arguments.  This means you can do stuff like this: "
       (code "(+ 1 2 3 4 5 6)") ".  Go ahead, try it out.  You know you want to.  Play around with the math functions for "
       "a while.  I'll wait.  Go ahead."]
      [:p.bottom "That's the end of step two.  Press Next to continue to step three, where we'll play with sequences."]))

(def tutorial3-text
     (html
      [:p.bottom
       "Now that we've covered the basics of prefix math, let's take some time to play with sequences."]
      [:p.bottom
       "In Clojure, the term 'sequence' is an abstraction that many data structures "
       "implement.  This abstraction allows any function that works on a 'seq' (pronounced seek) to work on any data "
       "structure that is seqable.  This means all Clojure and Java collections, strings, and even I/O streams."]
      [:p.bottom
       "In isolation this doesn't make sense, so let's play with vectors.  A literal vector looks like this: " (code "[1 2 3 \"four\" 5 6.0]") ".  Vectors are the "
       "most commonly used collection in Clojure, along with maps.  Let's see if we can square all of the elements "
       "of a vector: " (code "(map (fn [x] (* x x)) [1 2 3 4 5])") ".  Try that out.  The " (code "map") " function takes "
       "a function that takes a single argument and returns a value, which it 'applies' to each element of its second argument, a sequence.  "
       "The resulting sequence is then returned."]
      [:p.bottom
       "We have introduced something new here -- the anonymous function.  Anonymous functions are used quite heavily in Clojure code.  "
       "The are created using " (code "fn") ".  After fn, you supply a vector of arguments that the "
       "function takes, and then the body of the function that uses those arguments to computer a value.  Clojure has "
       "short hand for anonymous functions as well.  The above anonymous function can be rewritten using a shorthand form: "
       (code "#(+ % %)") ".  When you use this shorthand, the function arguments are accessed like so: % or %1 for the "
       "first argument, %2 for the second argument, %3 for the third and so on.  Our code now looks like this: "
       (code "(map #(* % %) [1 2 3 4 5])")]
      [:p.bottom
       "Another highly important collection is the hashmap.  A literal hashmap looks like this: "
       (code "{:key \"value\" :key2 3 :key4 [3 4 2 1]}") ".  The keys in this map are something called 'keywords'.  "
       "You can tell that they are keywords because they start with a colon ':' character.  They're like symbols that "
       "resolve to themselves.  In idiomatic Clojure code, keywords are often used as map keys.  You can use many objects as map keys, "
       "including integers, vectors, strings, and other maps."]
      [:p.bottom
       "To get a value at a key in a map, we can do this: " (code "({:key1 :val} :key1)") ".  I bet you're all lolwut "
       "right now, aren't you?  In Clojure, hashmaps are functions that take a single argument, which should be a key "
       "and they lookup the key within themselves and return the value at the key or nil if the key doesn't exist.  "
       "Another neat trick for maps with keywords for keys is this: " (code "(:key1 {:key1 :val})") ".  Keywords are "
       "also functions.  They just look themselves up in the supplied map."]
      [:p.bottom
       "Clojure has all sorts of other data structures as well.  Sets, lists, queues, zippers, etc.  We'll talk about some of "
       "those later on.  For now, lets move on to step four.  Press Next to continue."]))

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