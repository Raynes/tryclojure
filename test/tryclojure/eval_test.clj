(ns tryclojure.eval-test
  (:use tryclojure.models.eval
        clojure.test)
  (:require noir.session))

(def sb (make-sandbox))

(deftest eval-form-test
  (let [form "(do (println 10) (+ 3 3))"
        result (eval-string form sb)]
    (is (= "10\n" (-> result :result first str)))
    (is (= "6" (-> result :result second str)))
    (is (= (read-string form) (-> result :expr)))))

(alter-var-root #'noir.session/*noir-session* (constantly (atom {})))

(deftest eval-request-test
  (is (= "Execution Timed Out!" (:message (eval-request "(while true)")))))