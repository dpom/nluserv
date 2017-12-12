(ns nluserv.handler.dims
  (:require
   [clojure.string :as str]
   [clojure.set :as cset]
   [fipp.edn :refer [pprint] :rename {pprint fipp}]
   [duct.logger :refer [log]]
   [ataraxy.core :as ataraxy]
   [ataraxy.response :as response]
   [clojure.java.io :as io]
   [clj-duckling.tool.duckling :as duck]
   [net.cgrand.enlive-html :as h]
   [integrant.core :as ig]
   [nlpcore.protocols :as core]
   [nlpcore.spec :as nsp])
(:import java.util.Properties))

(defn get-all-dims
  "Get all dims suprted by entity-extractors tools.

  Args:
  tools (collection): a tools collection

  Returns:
  (string): a coma separated dims list"
  [tools]
  (let [xf (comp
            (map core/get-features)
            (map :entities))]
    (str/join "," (sort (map name (transduce xf cset/union tools))))))

(defn get-module-entities
  [text sdims tool logger]
  (log logger :debug ::get-entities {:dims sdims, :text text :tool (core/get-id tool)})
  (mapv (fn [e]
          (update (cset/rename-keys e {:entity :dim}) :dim keyword))
        (core/apply-tool tool text {:dims sdims})))

;; (get-entities "vreau sa fac un cadou sub 1000 lei unui baiat de 10 ani" "gender,duration,budget")

(defn get-all-entities
  [text sdims config logger]
  (let [dims (if-not (empty? sdims)
               (set (map keyword (str/split sdims #",")))
               #{})
        opts {:dims dims}
        tools (:tools config)]
    {:dims (str/join "," (map name dims))
     :text text
     :entities (mapcat #(get-module-entities text dims % logger) tools)}))

(h/deftemplate test-template "nluserv/public/test_dims.html"
  [dims q res]
  [:textarea#test_res] (h/content res)
  [:textarea#test_q] (h/content q)
  [:input#test_dims] (h/set-attr :value dims))


(defmethod ig/init-key ::get-dims [_ {:keys [logger config]}]
  (fn [{[_ message] :ataraxy/result}]
    (log logger :debug ::get-dims {:message message})
    (let [{:keys [q dims] :or {q "" dims ""}} message]
      [::response/ok (get-all-entities q dims config logger)])))

(defmethod ig/init-key ::test-dims [_ {:keys [logger config]}]
  (fn [{[_ message] :ataraxy/result}]
    (log logger :debug ::test-dims {:message message})
    (let [q (get message "q" "")
          dims (get message "dims" "")]
      [::response/ok (str/join (test-template dims q (with-out-str (fipp (get-all-entities q dims config logger)))))])))

(defmethod ig/init-key ::test-dims-get [_ options]
  (fn [{[_] :ataraxy/result}]
    [::response/ok (str/join (test-template (get-all-dims (get-in options [:config :tools] [])) "" ""))]))
