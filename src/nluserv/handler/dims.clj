(ns nluserv.handler.dims
  (:require
   [clojure.string :as str]
   [clojure.pprint :as pp]
   [duct.logger :refer [log]]
   [ataraxy.core :as ataraxy]
   [ataraxy.response :as response] 
   [clojure.java.io :as io]
   [clj-duckling.core :as p]
   [net.cgrand.enlive-html :as h]
   [integrant.core :as ig])
  (:import java.util.Properties))


(defn get-entities
  [text sdims logger]
  (log logger :debug ::get-entities {:dims sdims, :text text})
  (let [dims (if-not (empty? sdims)
               (vec (map keyword (str/split sdims #",")))
               [])]
    {:dims (str/join "," dims)
     :text text
     :entities (p/parse :ro$core text dims)}))

;; (get-entities "vreau sa fac un cadou sub 1000 lei unui baiat de 10 ani" "gender,duration,budget") 



(h/deftemplate test-template "nluserv/public/test_dims.html"
  [dims q res]
  [:textarea#test_res] (h/content res)
  [:textarea#test_q] (h/content q)
  [:input#test_dims] (h/set-attr :value dims))


(defmethod ig/init-key ::get-dims [_ {:keys [logger]}]
  (fn [{[_ message] :ataraxy/result}]
    (log logger :debug ::get-dims {:message message})
    (let [{:keys [q dims] :or {q "" dims ""}} message]
    [::response/ok (get-entities q dims logger)])))

(defmethod ig/init-key ::test-dims [_ {:keys [logger]}]
  (fn [{[_ message] :ataraxy/result}]
    (log logger :debug ::test-dims {:message message})
    (let [q (get message "q" "")
          dims (get message "dims" "")]
      [::response/ok (str/join (test-template dims q (with-out-str (pp/pprint (get-entities q dims logger)))))])))

(defmethod ig/init-key ::test-dims-get [_ options]
  (fn [{[_] :ataraxy/result}]
    [::response/ok (str/join (test-template "" "" ""))]))
