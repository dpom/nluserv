(ns nluserv.handler.stopwords
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


(defn remove-stopwords
  [text stopwords logger]
  (log logger :debug ::get-stopwords {:text text})
  {:words (core/apply-tool stopwords text {})
   :text text})

(h/deftemplate test-template "nluserv/public/test_stopwords.html"
  [q res]
  [:textarea#test_q] (h/content q)
  [:textarea#test_res] (h/content res))

(defmethod ig/init-key ::remove-stopwords [_ {:keys [stopwords logger]}]
  (fn [{[_ message] :ataraxy/result}]
    (log logger :debug ::remove-stopwords {:message message})
    (let [{:keys [q] :or {q ""}} message]
    [::response/ok (remove-stopwords q stopwords logger)])))

(defmethod ig/init-key ::test-stopwords [_ {:keys [stopwords logger]}]
  (fn [{[_ message] :ataraxy/result}]
    (log logger :debug ::test-stopwords {:message message})
    (let [q (get message "q" "")]
      [::response/ok (str/join (test-template q (with-out-str (fipp (remove-stopwords q stopwords logger)))))])))

(defmethod ig/init-key ::test-stopwords-get [_ options]
  (fn [{[_] :ataraxy/result}]
    [::response/ok (str/join (test-template "" ""))]))
