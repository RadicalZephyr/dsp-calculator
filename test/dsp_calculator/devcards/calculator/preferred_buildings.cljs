(ns dsp-calculator.devcards.calculator.preferred-buildings
  (:require [devcards.core]
            [dsp-calculator.ui.base :as base]
            [dsp-calculator.ui.calculator.preferred-buildings :as sut]
            [reagent.core :as reagent])
  (:require-macros
   [devcards.core :refer [defcard-rg]]
   [dsp-calculator.devcards.calculator.preferred-buildings :refer [pb-card]]))

(defcard-rg css
  "This includes the css for the calculator interface."
  (base/stylesheet-includes))

(defn preferred-state []
  (reagent/atom
   {:belt (first sut/conveyor-belts)
    :mining-productivity (first sut/mining-productivity-techs)
    :miner (first sut/miners)
    :smelter (first sut/smelters)
    :assembler (first sut/assemblers)
    :chemical (first sut/chemical-plants)}))

(defcard-rg preferred-buildings-empty
  [:main.page.calculator
   [:div.combo-selector
    [:details.preferred.preferred-buildings {:open true}
     [:summary "Preferred Buildings"]
     [:div.fields]]]])

(defcard-rg preferred-buildings-singles
  (pb-card [args]
    [:div.combo-selector
     [sut/preferred-buildings
      #{"Miner"}
      args]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      #{"Smelting Facility"}
      args]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      #{"Assembler"}
      args]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      #{"Chemical Facility"}
      args]])
  (preferred-state)
  {:inspect-data true})

(defcard-rg preferred-buildings-pairs
  (pb-card [args]
    [:div.combo-selector
     [sut/preferred-buildings
      #{"Miner" "Smelting Facility"}
      args]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      #{"Miner" "Assembler"}
      args]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      #{"Miner" "Chemical Facility"}
      args]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      #{"Smelting Facility" "Assembler"}
      args]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      #{"Smelting Facility" "Chemical Facility"}
      args]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      #{"Assembler" "Chemical Facility"}
      args]])
  (preferred-state)
  {:inspect-data true})

(defcard-rg preferred-buildings-triples
  (pb-card [args]
    [:div.combo-selector
     [sut/preferred-buildings
      #{"Miner" "Smelting Facility" "Assembler"}
      args]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      #{"Smelting Facility" "Assembler" "Chemical Facility"}
      args]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      #{"Miner" "Smelting Facility" "Chemical Facility"}
      args]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      #{"Miner" "Assembler" "Chemical Facility"}
      args]])
  (preferred-state)
  {:inspect-data true})

(defcard-rg preferred-buildings-full
  (pb-card [args]
    [:div.combo-selector
     [sut/preferred-buildings
      #{"Miner" "Smelting Facility" "Assembler" "Chemical Facility"}
      args]])
  (preferred-state)
  {:inspect-data true})
