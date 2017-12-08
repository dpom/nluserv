(ns nluserv.main
  (:gen-class)
  (:require
   [integrant.core :as ig]
   [clojure.java.io :as io]
   [environ.core :refer [env]]
   [duct.core :as duct]))

(duct/load-hierarchy)

(defn -main [& args]
  (let [keys (or (duct/parse-keys args) [:duct/daemon])]
    (-> (duct/read-config (io/resource (get env :configfile "nluserv/prod.edn")))
        (duct/prep keys)
        (duct/exec keys))))


