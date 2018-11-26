(ns lcmap.gaia.nemo
  (:require [org.httpkit.client    :as http]
            [cheshire.core         :as json]
            [clojure.tools.logging :as log]
            [lcmap.gaia.config     :refer [config]]))

(defn results_url
  ([x y host path]
   (str host path "?chipx=" x "&chipy=" y))
  ([x y path]
   (results_url x y (:nemo_host config) path)))

(defn parse_body
  [http_response]
  (json/parse-string (:body http_response)))

(defn results
  [x y]
  (let [segments_response    @(http/get (results_url x y (:segments_path config)))
        predictions_response @(http/get (results_url x y (:predictions_path config)))
        segments_status    (:status segments_response)
        predictions_status (:status predictions_response)]
    (if (= 200 segments_status predictions_status)
      {:segments (parse_body segments_response) :predictions (parse_body predictions_response)}
      (do (log/errorf "problem retrieving ccdc results. status code: %s \n response %s " segments_status segments_response)
          false))))