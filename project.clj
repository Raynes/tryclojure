(defproject tryclojure "0.1.0-SNAPSHOT"
  :description "A simple web-based Clojure REPL for trying out Clojure without having to install it."
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
		 [net.cgrand/moustache "1.0.0-SNAPSHOT"]
                 [ring "0.3.3"]
		 [commons-lang/commons-lang "2.5"]
                 [clojail "0.2.2-SNAPSHOT"]
		 [org.clojars.rayne/clj-sandbox "0.4.2-SNAPSHOT"]
                 [org.clojars.rayne/clj-highlight "1.1.1-SNAPSHOT"]
		 [hiccup "0.2.7"]]
  :dev-dependencies [[swank-clojure "1.2.1"]
		     [ring/ring-devel "0.3.3"]])
