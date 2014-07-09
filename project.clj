(defproject tryclojure "0.1.0-SNAPSHOT"
  :description "A simple web-based Clojure REPL for trying out Clojure without having to install it."
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [enlive "1.1.5"]
                 [lib-noir "0.8.4"]
                 [compojure "1.1.8"]
                 [ring-server "0.3.1"]
                 [commons-lang/commons-lang "2.5"]
                 [clojail "1.0.6"]]
  :jvm-opts ["-Djava.security.policy=example.policy" "-Xmx80M"]
  :min-lein-version "2.0.0"
  :uberjar-name "tryclojure-standalone.jar"
  :plugins [[lein-ring "0.8.11"]]
  :ring {:handler tryclojure.server/app :port 8801}
  :profiles {:dev {:ring {:nrepl? true}}})
