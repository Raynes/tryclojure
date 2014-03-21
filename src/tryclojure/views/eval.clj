(ns tryclojure.views.eval
  (:require [tryclojure.models.eval :refer [eval-request]]
            [noir.response :as resp]))

(defn eval-json [expr jsonp]
  (let [{:keys [expr result error message] :as res} (eval-request expr)
        data (if error
               res
               (let [[out res] result]
                 {:expr (pr-str expr)
                  :result (str out (pr-str res))}))]
    
    (if jsonp
      (resp/jsonp jsonp data)
      (resp/json data))))