(ns dsp-calculator.devcards.calculator.production
  (:require [devcards.core]
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
  (let [tree (prod/production-tree prod-test/test-items
                                   prod-test/test-recipes
                                   1101)
        summary (prod/summarize tree)]
    [:main.page.calculator
     [:div.solver.has-proliferators
      [sut/production-tree-summary (vals (:raw-resources summary))]]]))

(defcard-rg production-tree
  (let [tree (prod/production-tree prod-test/test-items
                                   prod-test/test-recipes
                                   1101)]
    [:main.page.calculator
     [:div.solver.has-proliferators
      [sut/production-tree-header]
      [sut/production-tree-node 0 tree]]]))

(defcard-rg whole-production
  (let [tree (prod/production-tree prod-test/test-items
                                   prod-test/test-recipes
                                   1101)
        summary (atom (prod/summarize tree))
        tree (atom tree)]
    [:main.page.calculator
     [sut/production-tree summary tree]]))
