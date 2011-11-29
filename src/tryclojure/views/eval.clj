(ns tryclojure.views.eval
  (:use [noir.core :only [defpage]]
        [noir.response :only [json]]
        [tryclojure.models.eval :only [eval-request]]))

(defpage "/eval.json" {:keys [expr jsonp]}
  (update-in
   (json
    (let [{:keys [expr result error message] :as res} (eval-request expr)]
      (if error
        res
        (let [[out res] result]
          {:expr (pr-str expr)
           :result (str out (pr-str res))}))))
   [:body]
   #(if jsonp (str jsonp "(" % ")") %)))