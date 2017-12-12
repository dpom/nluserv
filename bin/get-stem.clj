#!/usr/bin/env inlein

'{:dependencies [[org.clojure/clojure "1.9.0"]
                 [clj-http "3.7.0"]
                 [cheshire "5.8.0"]]}

(require '[clj-http.client :as client])
(require '[clojure.string :as str])
(require '[cheshire.core :as json])
(require '[clojure.pprint :as pp])

(def url "http://localhost:3000/get_stem")

(let [{:keys [status body]} (client/post url {:form-params {:q (first *command-line-args*)}
                                              :accept :json
                                              :content-type :json} )]
  (println (format "status = %d\nbody = %s" status (with-out-str (pp/pprint (json/parse-string body))))))
