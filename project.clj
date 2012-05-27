(defproject tryclojure "0.1.0-SNAPSHOT"
  :description "A simple web-based Clojure REPL for trying out Clojure without having to install it."
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [noir "1.3.0-beta3"]
                 [jayq "0.1.0-alpha4"]
		 [commons-lang/commons-lang "2.5"]
                 [clojail "0.5.1"]]
  :jvm-opts ["-Djava.security.policy=example.policy""-Xmx80M"]
  :plugins [[lein-cljsbuild "0.2.0"]]
  :cljsbuild {
    :builds [{
        :source-path "src"
        :compiler {
          :output-to "resources/public/javascript/tryclojure.js"
          :optimizations :whitespace
          :pretty-print true}}]}
  :main tryclojure.server)


