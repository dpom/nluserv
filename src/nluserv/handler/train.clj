(ns nluserv.handler.train
  (:require
   [clojure.string :as str]
   [fipp.edn :refer [pprint] :rename {pprint fipp}]
   [ataraxy.core :as ataraxy]
   [ataraxy.response :as response] 
   [clojure.java.io :as io]
   [net.cgrand.enlive-html :as h]
   [duct.logger :as logger]
   [nlpcore.protocols :as core]
   [integrant.core :as ig])
  (:import java.util.Properties))



(h/deftemplate train-template "nluserv/public/train.html"
  [logs]
  [:textarea#logs] (h/content logs))

(defn render-train
  [train? logs]
  (str/join (train-template (if train? logs "Training is not available!"))))

(defrecord TrainLogger [logs]
  logger/Logger
  (-log [_ level ns-str file line id event data]
    (swap! logs conj [event data])))

(defn train
  [options _]
  (let [tlogger (->TrainLogger (atom []))
        {:keys [tools models corpora]} options]
    (doseq [c corpora]
      (core/build-corpus! c))
    (doseq [m models]
      (core/set-logger! m tlogger)
      (core/train-model! m)
      (core/save-model! m))
    (doseq [t tools]
      (core/set-logger! t tlogger)
      (core/build-tool! t))
    {:status :ok :logs @(:logs tlogger) }))

(defmethod ig/init-key ::train [_ {:keys [train?] :or {train? true}}]
  (fn [{[_] :ataraxy/result}]
    [::response/ok (render-train train? "")]))

(defmethod ig/init-key ::test-train [_ {:keys [config train?] :or {train? true}}]
  (fn [{[_] :ataraxy/result}]
    [::response/ok (render-train train? (with-out-str (fipp (:logs (train config nil)))))]))


