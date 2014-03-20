(defproject tryclojure "0.1.0-SNAPSHOT"
  :description "A simple web-based Clojure REPL for trying out Clojure without having to install it."
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [lib-noir "0.8.1"]
                 [compojure "1.1.6"]
                 [ring-server "0.3.1"]
                 [commons-lang/commons-lang "2.5"]
                 [clojail "1.0.6"]]
  :jvm-opts ["-Djava.security.policy=example.policy" "-Xmx80M"]
  :min-lein-version "2.0.0"
  :uberjar-name "tryclojure-standalone.jar"
  :plugins [[lein-ring "0.8.10"]]
  :ring {:handler tryclojure.server/app :port 8801})
