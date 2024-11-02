(ns dsp-calculator.devcards.calculator.production
  (:require [devcards.core]
            [com.gfredericks.exact :as e]
            [dsp-calculator.production :as prod]
            [dsp-calculator.production-test :as prod-test]
            [dsp-calculator.ui.base :as base]
            [dsp-calculator.ui.calculator.production :as sut])
  (:require-macros
   [devcards.core :refer [defcard-rg]]))

(defcard-rg css
  "This includes the css for the calculator interface."
  (base/stylesheet-includes))

(defcard-rg production-tree-header
  [:main.page.calculator
   [:div.solver.has-proliferators
    [sut/production-tree-header]]])

(defcard-rg production-summary
  (let [context {:ratio (e/native->integer 1)
                 :timescale "minute"
                 :belt-rate (e/native->integer 6)}
        tree (prod/production-tree prod-test/test-items
                                   prod-test/test-recipes
                                   prod-test/test-recipes-by-output
                                   1)
        summary (prod/summarize tree)]
    [:main.page.calculator
     [:div.solver.has-proliferators
      [sut/production-tree-summary context (vals (:raw-resources summary))]]]))

(defcard-rg empty-production-tree
  "An empty production tree is not rendered."
  [:main.page.calculator
   [sut/production-tree (atom {}) (atom {}) (atom {})]])

(defcard-rg production-tree
  (let [context {:ratio (e/native->integer 1)
                 :timescale "minute"
                 :belt-rate (e/native->integer 6)}
        tree (prod/production-tree prod-test/test-items
                                   prod-test/test-recipes
                                   prod-test/test-recipes-by-output
                                   1)]
    [:main.page.calculator
     [:div.solver.has-proliferators
      [sut/production-tree-header]
      [sut/production-tree-node context 0 tree]]]))

(defcard-rg whole-production
  (let [context (atom {:ratio (e/native->integer 1)
                       :timescale "minute"
                       :belt-rate (e/native->integer 6)})
        tree (prod/production-tree prod-test/test-items
                                   prod-test/test-recipes
                                   prod-test/test-recipes-by-output
                                   97)
        summary (atom (prod/summarize tree))
        tree (atom tree)]
    [:main.page.calculator
     [sut/production-tree context summary tree]]))
