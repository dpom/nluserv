(ns nluserv.handler.stemmer
  (:require
   [clojure.string :as str]
   [fipp.edn :refer (pprint) :rename {pprint fipp}]
   [ataraxy.core :as ataraxy]
   [ataraxy.response :as response] 
   [clojure.java.io :as io]
   [net.cgrand.enlive-html :as h]
   [duct.logger :refer [log]]
   [nlptools.tool.core :as tool]
   [integrant.core :as ig])
  (:import java.util.Properties))


(defn get-stem
  [text stemmer]
  {:stem (tool/apply-tool stemmer (str/trim text))
   :text text})

(h/deftemplate test-template "nluserv/public/test_stemmer.html"
  [q res]
  [:input#test_q] (h/set-attr :value q)
  [:textarea#test_res] (h/content res))

(defmethod ig/init-key ::get-stem [_ {:keys [stemmer logger]}]
  (fn [{[_ message] :ataraxy/result}]
    (log logger :debug ::get-stem {:message message})
    (let [{:keys [q] :or {q ""}} message]
    [::response/ok (get-stem q stemmer)])))

(defmethod ig/init-key ::test-stemmer [_ {:keys [stemmer logger]}]
  (fn [{[_ message] :ataraxy/result}]
    (log logger :debug ::test-stemmer {:message message})
    (let [q (get message "q" "")]
      [::response/ok (str/join (test-template q (with-out-str (fipp (get-stem q stemmer)))))])))

(defmethod ig/init-key ::test-stemmer-get [_ options]
  (fn [{[_] :ataraxy/result}]
    [::response/ok (str/join (test-template "" ""))]))
