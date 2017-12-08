(ns nluserv.handler.intent
  (:require
   [clojure.string :as str]
   [fipp.edn :refer (pprint) :rename {pprint fipp}]
   [duct.logger :refer [log]]
   [ataraxy.core :as ataraxy]
   [ataraxy.response :as response] 
   [clojure.java.io :as io]
   [net.cgrand.enlive-html :as h]
   [nlpcore.protocols :as core]
   [integrant.core :as ig])
  (:import java.util.Properties))


(defn get-intent
  [text catint logger]
  (log logger :debug ::get-intent {:text text})
  ;; (log/debugf "get-intent params: text = |%s|" text)
  {:intent (core/apply-tool catint text {})
   :text text})

(h/deftemplate test-template "nluserv/public/test_intent.html"
  [q res]
  [:textarea#test_q] (h/content q)
  [:textarea#test_res] (h/content res))

(defmethod ig/init-key ::get-intent [_ {:keys [catint logger]}]
  (fn [{[_ message] :ataraxy/result}]
    (log logger :debug ::get-intent {:message message})
    (let [{:keys [q] :or {q ""}} message]
    [::response/ok (get-intent q catint logger)])))

(defmethod ig/init-key ::test-intent [_ {:keys [catint logger]}]
  (fn [{[_ message] :ataraxy/result}]
    (log logger :debug ::test-intent {:message message})
    (let [q (get message "q" "")]
      [::response/ok (str/join (test-template q (with-out-str (fipp (get-intent q catint logger)))))])))

(defmethod ig/init-key ::test-intent-get [_ options]
  (fn [{[_] :ataraxy/result}]
    [::response/ok (str/join (test-template "" ""))]))
