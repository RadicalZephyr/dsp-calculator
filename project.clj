(defproject dev.zefira/dsp-calculator "lein-git-inject/version"
  :description "A Dyson Sphere production calculator."
  :url "https://github.com/RadicalZephyr/dsp-calculator"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :scm {:name "git"
        :url "https://github.com/RadicalZephyr/dsp-calculator"}
  :plugins [[day8/lein-git-inject "0.0.15"]]

  :dependencies [[org.clojure/clojure "1.12.0"]
                 [cljsjs/react "18.2.0-1"]
                 [cljsjs/react-dom "18.2.0-1"]
                 [reagent "1.2.0"]
                 [re-frame "1.4.3"]
                 [garden "1.3.10"]
                 [net.dhleong/spade "1.1.0"]]
  :repl-options {:init-ns dsp-calculator.core}
  :profiles {:dev {:dependencies [[org.clojure/clojurescript "1.11.60"]
                                  [day8.re-frame/tracing "0.6.2"]
                                  [day8.re-frame/re-frame-10x "1.9.9"]
                                  [com.bhauman/figwheel-main "0.2.18"]
                                  [org.clojars.earthlingzephyr/devcards "0.3.0-SNAPSHOT"]
                                  ;; Specifically for processing DSP JSON data into EDN.
                                  [org.clojure/data.json "2.5.0"]
                                  [camel-snake-kebab "0.4.3"]
                                  [org.slf4j/slf4j-nop "1.7.30"]]
                   :resource-paths ["target" "dev-resources"]
                   :clean-targets ^{:protect false} ["resources/public/js/compiled/"
                                                     "dev-resources/public/devcards/js/compiled/"]}}
  :release-tasks [["vcs" "assert-committed"]
                  ["deploy"]]
  :aliases {"fig" ["trampoline" "run" "-m" "figwheel.main"]})
