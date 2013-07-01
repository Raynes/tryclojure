(defproject tryclojure "0.1.0-SNAPSHOT"
  :description "A simple web-based Clojure REPL for trying out Clojure without having to install it."
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [noir "1.3.0-beta10"]
                 [commons-lang/commons-lang "2.5"]
                 [clojail "1.0.6"]]
  :jvm-opts ["-Djava.security.policy=example.policy" "-Xmx80M"]
  :min-lein-version "2.0.0"
  :uberjar-name "tryclojure-standalone.jar"
  :main tryclojure.server)
