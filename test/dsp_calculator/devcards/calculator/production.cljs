(ns dsp-calculator.devcards.calculator.production
  (:require [devcards.core]
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
  [:main.page.calculator
   [:div.solver.has-proliferators
    [sut/production-tree-summary [{:id 1001
                                    :name "Iron Ore"}]]]])

(defcard-rg production-tree
  [:main.page.calculator
   [:div.solver.has-proliferators
    [sut/production-tree-header]
    [sut/production-tree-node 0 {:id 1101
                                  :name "Iron Ingot"
                                  :items [{:id 1001
                                           :name "Iron Ore"}]}]]])

(defcard-rg whole-production
  [:main.page.calculator
   [sut/production-tree
    (atom {:raw-resources {1001 {:id 1001
                                 :name "Iron Ore"}}})
    (atom {:id 1101
           :name "Iron Ingot"
           :items [{:id 1001
                    :name "Iron Ore"}]})]])
