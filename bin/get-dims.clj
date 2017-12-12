#!/usr/bin/env inlein

;; syntax: get-dims.clj "comma,separated,dimensions,or,empty" the text to be parsed

'{:dependencies [[org.clojure/clojure "1.9.0"]
                 [fipp "0.6.12"]
                 [clj-http "3.7.0"]
                 [cheshire "5.8.0"]]}

(require '[clj-http.client :as client]
         '[clojure.string :as str]
         '[cheshire.core :as json]
         '[fipp.edn :refer [pprint] :rename {pprint fipp}])

  (def url "http://localhost:3000/get_dims")

(let [
      {:keys [status body]} (client/post url {:form-params {:dims (first *command-line-args*)
                                                            :q (str/join " " (rest *command-line-args*))}
                                              :accept :json
                                              :content-type :json} )]
  (println (format "status = %d\nbody = %s" status (with-out-str (fipp (json/parse-string body))))))
