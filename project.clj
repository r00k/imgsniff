(defproject imgsniff "0.1.0-SNAPSHOT"
  :description "Downloads imgur albums/images that were liked on reddit"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/data.json "0.2.5"]
                 [clj-http "1.0.1"]]
  :main ^:skip-aot imgsniff.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
