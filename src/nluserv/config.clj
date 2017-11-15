(ns nluserv.config
  (:require
   [clojure.java.io :as io]
   [integrant.core :as ig]))

(defmethod ig/init-key :nluserv/config [_ options]
  options)

(defmethod ig/init-key ::absfilename [_ {:keys [path filename]}]
  (.getAbsolutePath (io/file path filename)))
