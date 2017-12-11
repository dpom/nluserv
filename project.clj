(defproject nluserv "0.2-dev05"
  :description "A NLU Web Services."
  :license {:name "Apache License 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0.html"
            :comments "see LICENSE"
            :distribution :repo}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [duct/core "0.6.1"]
                 [duct/module.logging "0.3.1" :exclusions [com.taoensso/truss com.taoensso/encore]]
                 [duct/module.web "0.6.3" :exclusions [commons-codec
                                                       com.fasterxml.jackson.core/jackson-core]]
                 [duct/module.ataraxy "0.2.0"]
                 [environ "1.1.0"]
                 [enlive "1.1.6"]
                 [integrant "0.6.1"]
                 [cheshire "5.8.0"]
                 [fipp "0.6.12"]
                 [ring/ring-codec "1.1.0"]
                 [dpom/nlpcore "1.2" :exclusions [duct/logger]]
                 [dpom/clj-duckling "0.7.2"]
                 [dpom/nlptools "0.7" :exclusions [org.jsoup/jsoup
                                                   instaparse
                                                   com.taoensso/truss 
                                                   org.apache.commons/commons-lang3
                                                   com.fasterxml.jackson.core/jackson-databind
                                                   com.taoensso/encore
                                                   prismatic/schema]]]
  :pedantic? :warning
  :plugins [[duct/lein-duct "0.10.2"]
            [lein-ancient "0.6.10" :exclusions [commons-logging
                                                org.clojure/clojure]]
            [jonase/eastwood "0.2.6-beta2"]
            [lein-kibit "0.1.6" :exclusions [org.clojure/clojure]]
            [lein-cljfmt "0.5.6" :exclusions [org.clojure/clojure rewrite-clj]]
            [lein-codox "0.10.3" :exclusions [org.clojure/clojure]]
            [lein-environ "1.1.0"]]
  :main ^:skip-aot nluserv.main
  ;; :uberjar-name  "nluserv-standalone.jar"
  :resource-paths ["resources" "target/resources"]
  :prep-tasks     ["javac" "compile" ["run" ":duct/compiler"]]
  :profiles {:dev  [:project/dev :profiles/dev]
             :repl {:prep-tasks   ^:replace ["javac" "compile"]
                    :repl-options {:init-ns user}}
             :uberjar {:aot :all}
             :profiles/dev {}
             :project/dev  {:source-paths   ["dev/src"]
                            :resource-paths ["dev/resources"]
                            :dependencies   [[integrant/repl "0.2.0"]
                                             [eftest "0.4.1"]
                                             [kerodon "0.9.0" :exclusions [commons-codec]]]}}
  :codox {:doc-files []
          :exclude-vars nil
          :project {:name "nlptools"}
          :source-paths ["src"]
          :output-path "docs/api"}

  )
