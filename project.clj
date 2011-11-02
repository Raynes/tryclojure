(defproject tryclojure "0.1.0-SNAPSHOT"
  :description "A simple web-based Clojure REPL for trying out Clojure without having to install it."
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [noir "1.2.0"]
		 [commons-lang/commons-lang "2.5"]
                 [clojail "0.5.0-beta3"]]
  ;; For lein and Heroku compatibility. If you're using cake, add
  ;; this line to .cake/config: jvm.opts = -Djava.security.policy=example.policy
  :jvm-opts ["-Djava.security.policy=example.policy"]
  :main tryclojure.core)