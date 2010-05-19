(defproject tryclojure "0.1.0-SNAPSHOT"
  :description "A simple web-based Clojure REPL for trying out Clojure without having to install it."
  :dependencies [[org.clojure/clojure "1.1.0"]
                 [org.clojure/clojure-contrib "1.1.0"]
		 [net.cgrand/moustache "1.0.0-SNAPSHOT"]
                 [ring/ring-jetty-adapter "0.2.0"]
		 [commons-lang/commons-lang "2.5"]
		 [clj-sandbox "0.3.7"]
		 [hiccup "0.2.3"]
		 [clj-gist "1.0.0-SNAPSHOT"]]
  :dev-dependencies [[swank-clojure "1.2.1"]
		     [lein-search "0.3.0-SNAPSHOT"]
		     [ring/ring-devel "0.2.0"]])
