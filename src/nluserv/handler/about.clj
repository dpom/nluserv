(ns nluserv.handler.about
  (:require
   [clojure.string :as str]
   [ataraxy.core :as ataraxy]
   [ataraxy.response :as response] 
   [clojure.java.io :as io]
   [net.cgrand.enlive-html :as h]
   [duct.logger :as logger]
   [integrant.core :as ig])
  (:import java.util.Properties))

(defn get-version
  [dep]
  (let [path (str "META-INF/maven/" (or (namespace dep) (name dep)) "/" (name dep) "/pom.properties")
        props (io/resource path)]
    (when props
      (with-open [stream (io/input-stream props)]
        (let [props (doto (Properties.) (.load stream))]
          (.getProperty props "version"))))))

(h/deftemplate about-template "nluserv/public/about.html"
  [version cloj duck nlptools]
  [:span#nluserv] (h/content version)
  [:span#clojure] (h/content cloj)
  [:span#clj-duckling] (h/content duck)
  [:span#nlptools] (h/content nlptools))

(defn render-about
  []
  (str/join (about-template
             (get-version 'nluserv)
             (get-version 'org.clojure/clojure)
             (get-version 'dpom/clj-duckling)
             (get-version 'dpom/nlptools))))


(defmethod ig/init-key :nluserv.handler/about [_ options]
  (fn [{[_] :ataraxy/result}]
    [::response/ok (render-about)]))




