
(defproject color-map "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [quil "2.4.0"]
                 [org.clojure/clojurescript "1.8.34"]]

  :plugins [[lein-cljsbuild "1.1.3"]
            [lein-figwheel "0.5.0-6"]]
  :clean-targets [:target-path "out"]
  :figwheel {:server-port 3333}

  :hooks [leiningen.cljsbuild]

  :cljsbuild {:builds {:dev {:source-paths ["src"]
                             :figwheel true
                             :compiler {:output-to "js/main.js"
                                        :output-dir "out"
                                        :main "color_map.core"
                                        :optimizations :none
                                        :pretty-print true}}
                       :min {:source-paths ["src"]
                             :compiler {:main "color_map.core"
                                        :output-to "dist/js/main.js"
                                        :optimizations :advanced
                                        :pretty-print false}}}})
