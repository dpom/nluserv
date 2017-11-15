(ns nluserv.handler.train
  (:require
   [clojure.string :as str]
   [fipp.edn :refer [pprint] :rename {pprint fipp}]
   [ataraxy.core :as ataraxy]
   [ataraxy.response :as response] 
   [clojure.java.io :as io]
   [net.cgrand.enlive-html :as h]
   [duct.logger :as logger]
   [nlptools.tool.core :as tool]
   [nlptools.corpus.core :as corp]
   [nlptools.model.core :as modl]
   [integrant.core :as ig])
  (:import java.util.Properties))



(h/deftemplate train-template "nluserv/public/train.html"
  [logs]
  [:textarea#logs] (h/content logs))

(defn render-train
  [logs]
  (str/join (train-template
             logs)))

(defrecord TrainLogger [logs]
  logger/Logger
  (-log [_ level ns-str file line id event data]
    (swap! logs conj [event data])))

(defn train
  [options _]
  (let [tlogger (->TrainLogger (atom []))
        {:keys [tools models corpora]} options]
    (doseq [c corpora]
      (corp/build-corpus! c))
    (doseq [m models]
      (modl/set-logger! m tlogger)
      (modl/train-model! m)
      (modl/save-model! m))
    (doseq [t tools]
      (tool/set-logger! t tlogger)
      (tool/build-tool! t))
    {:status :ok :logs @(:logs tlogger) }))

(defmethod ig/init-key ::train [_ options]
  (fn [{[_] :ataraxy/result}]
    [::response/ok (render-train "")]))

(defmethod ig/init-key ::test-train [_ options]
  (fn [{[_] :ataraxy/result}]
    [::response/ok (render-train (with-out-str (fipp (:logs (train (:config options) nil)))))]))


