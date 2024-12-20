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
   {:timescale "minute"
    :belt (first sut/conveyor-belts)
    :mining-productivity (first sut/mining-productivity-techs)
    :miner (first sut/miners)
    :smelter (first sut/smelters)
    :assembler (first sut/assemblers)
    :chemical (first sut/chemical-plants)}))

(defcard-rg preferred-buildings-empty
  "The preferred buildings section is not rendered when the facilities
  key is empty or nil."
  (pb-card [args]
    [:main.page.calculator
     [:div.combo-selector
      [sut/preferred-buildings
       (assoc args
              :facilities (atom nil))]]])
  (preferred-state))

(defcard-rg preferred-buildings-singles
  (pb-card [args]
    [:div.combo-selector
     [sut/preferred-buildings
      (assoc args
             :facilities (atom #{"Miner"}))]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      (assoc args
             :facilities (atom #{"Smelting Facility"}))]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      (assoc args
             :facilities (atom #{"Assembler"}))]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      (assoc args
             :facilities (atom #{"Chemical Facility"}))]])
  (preferred-state)
  {:inspect-data true})

(defcard-rg preferred-buildings-pairs
  (pb-card [args]
    [:div.combo-selector
     [sut/preferred-buildings
      (assoc args
             :facilities (atom #{"Miner" "Smelting Facility"}))]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      (assoc args
             :facilities (atom #{"Miner" "Assembler"}))]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      (assoc args
             :facilities (atom #{"Miner" "Chemical Facility"}))]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      (assoc args
             :facilities (atom #{"Smelting Facility" "Assembler"}))]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      (assoc args
             :facilities (atom #{"Smelting Facility" "Chemical Facility"}))]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      (assoc args
             :facilities (atom #{"Assembler" "Chemical Facility"}))]])
  (preferred-state)
  {:inspect-data true})

(defcard-rg preferred-buildings-triples
  (pb-card [args]
    [:div.combo-selector
     [sut/preferred-buildings
      (assoc args
             :facilities (atom #{"Miner" "Smelting Facility" "Assembler"}))]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      (assoc args
             :facilities (atom #{"Smelting Facility" "Assembler" "Chemical Facility"}))]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      (assoc args
             :facilities (atom #{"Miner" "Smelting Facility" "Chemical Facility"}))]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      (assoc args
             :facilities (atom #{"Miner" "Assembler" "Chemical Facility"}))]])
  (preferred-state)
  {:inspect-data true})

(defcard-rg preferred-buildings-full
  (pb-card [args]
    [:div.combo-selector
     [sut/preferred-buildings
      (assoc args
             :facilities (atom #{"Miner" "Smelting Facility" "Assembler" "Chemical Facility"}))]])
  (preferred-state)
  {:inspect-data true})
