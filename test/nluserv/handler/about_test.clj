(ns nluserv.handler.about-test
  (:require [clojure.test :refer :all]
            [kerodon.core :refer :all]
            [kerodon.test :refer :all]
            [integrant.core :as ig]
            [nluserv.handler.about :as about]))

;; (def handler
;;   (ig/init-key :nluserv.handler/about {}))

;; (deftest smoke-test
;;   (testing "about page exists"
;;     (-> (session handler)
;;         (visit "/about")
;;         (has (status? 200) "page exists"))))
