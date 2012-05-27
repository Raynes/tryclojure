(ns tryclojure.client.home
  (:use [jayq.core :only [$]]
        [jayq.util :only [clj->js]])
  (:use-macros [jayq.macros :only [ready]]))

(def page-num (atom -1))
(def page (atom nil))
(def controller (atom nil))

(def pages (concat
            [{:verify (fn [data] false)}]
            (map #(hash-map :verify %)
                 ["(+ 3 3)"
                  "(/ 10 3)"
                  "(/ 10 3.0)"
                  "(+ 1 2 3 4 5 6)"
                  "(defn square [x] (* x x))"
                  "(square 10)"
                  "((fn [x] (* x x)) 10)"
                  "(def square (fn [x] (* x x)))"
                  "(map inc [1 2 3 4])"])
            [{:verify (fn [data] false)}]))

(defn show-page [n]
  (when-let [res (nth @pages n)]
    (swap! page-num (constantly n))
    (swap! page (constantly res))
    (let [block ($ "#changer")]
      (.fadeOut block
                (fn [e]
                  (.load block
                         (clj->js {:n (+ 1 n)})
                         #(do (.fadeIn block)
                              (changer-updated))))))))

(defn setup-link [url]
  (fn [e]
    (.load ($ "#changer")
           url
           (fn [data]
             (.html ($ "#changer") data)))))

(defn setup-examples [controller]
  (.click ($ ".code")
          (fn [e]
            (.promptText controller (this-as this (.text ($ this)))))))

(defn get-step [n controller]
  (.load ($ "#tuttext")
         "tutorial"
         (clj->js {:step n})
         #(setup-examples controller)))

(defn eval-clojure [code]
  (let [data (atom nil)]
    (.ajax js/jQuery
           (clj->js {:url "eval.json"
                     :data {:expr code}
                     :async false
                     :success (fn [res] (swap! data (constantly res)))}))
    @data))

(defn do-command [input report]
  (case input
    "tutorial" (do (show-page 0)
                   (report)
                   true)
    "back" (if (> @page-num 0)
             (do (show-page (- @page-num 1))
                 (report)
                 true)
             false)
    "next" (if (and (>= @page-num 0)
                    (< @page-num (- (count @pages) 1)))
             (do (show-page (+ 1 @page-num))
                 (report)
                 true)
             false)
    "restart" (if (> @page-num 0)
                (do (show-page 0)
                    (report)
                    true)
                false)
    false))

(defn on-validate [input]
  (not= input ""))

(defn on-handle [line report]
  (let [input (.trim js/jQuery line)]
    (when-not (do-command input command)
      (let [data (eval-clojure input)]
        (if (.-error data)
          (clj->js [{:msg (.-message data)
                     :className "jquery-console-message-error"}])
          (do
            (when (and @page (.verify @page data))
              (show-page (+ 1 @page-num)))
            (clj->js [{:msg (.-result data)
                       :className "jquery-console-message-value"}])))))))

(defn changer-updated []
  (.each ($ "#changer code.expr")
        #(this-as this
                  (.css ($ this) "cursor" "pointer")
                  (.attr ($ this) "title" (str "Click to insert '" (.text ($ this)) "' into the console." ))
                  (.click ($ this)
                          (fn [e]
                            (.promptText @controller (.text ($ this)))
                            (.click (.-inner @controller)))))))



(ready (swap! controller
              #(.console ($ "#console")
                         (clj->js
                          {:welcomeMessage "Enter sme Clojure code to be evaluated."
                           :promptLabel "Clojure>"
                           :commandValidate on-validate
                           :commandHandle on-handle
                           :autofocus true
                           :animateScroll true
                           :promptHistory true})))
       (.click ($ "#home") (setup-link "home"))
       (.click ($ "#links") (setup-link "links"))
       (.click ($ "#about") (setup-link "about"))
       (changer-updated))