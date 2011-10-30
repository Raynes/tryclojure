(defproject tryclojure "0.1.0-SNAPSHOT"
  :description "A simple web-based Clojure REPL for trying out Clojure without having to install it."
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [noir "1.2.0"]
		 [commons-lang/commons-lang "2.5"]
                 [clojail "0.4.6-beta2"]]
  :main tryclojure.core)