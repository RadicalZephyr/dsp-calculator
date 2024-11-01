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
  (let [context {:ratio (e/native->integer 1)}
        tree (prod/production-tree prod-test/test-items
                                   prod-test/test-recipes
                                   1101)
        summary (prod/summarize tree)]
    [:main.page.calculator
     [:div.solver.has-proliferators
      [sut/production-tree-summary context (vals (:raw-resources summary))]]]))

(defcard-rg production-tree
  (let [context {:ratio (e/native->integer 1)}
        tree (prod/production-tree prod-test/test-items
                                   prod-test/test-recipes
                                   1101)]
    [:main.page.calculator
     [:div.solver.has-proliferators
      [sut/production-tree-header]
      [sut/production-tree-node context 0 tree]]]))

(defcard-rg whole-production
  (let [context (atom {:ratio (e/native->integer 1)})
        tree (prod/production-tree prod-test/test-items
                                   prod-test/test-recipes
                                   1203)
        summary (atom (prod/summarize tree))
        tree (atom tree)]
    [:main.page.calculator
     [sut/production-tree context summary tree]]))
