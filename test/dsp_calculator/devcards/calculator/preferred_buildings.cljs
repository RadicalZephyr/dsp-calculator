(ns dsp-calculator.devcards.calculator.preferred-buildings
  (:require [devcards.core]
            [dsp-calculator.ui :as ui]
            [dsp-calculator.ui.calculator.preferred-buildings :as sut]
            [reagent.core :as reagent])
  (:require-macros
   [devcards.core :refer [defcard-rg]]
   [dsp-calculator.devcards.calculator.preferred-buildings :refer [pb-card]]))

(defcard-rg css
  "This includes the css for the calculator interface."
  (ui/stylesheet-includes))

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
  (pb-card
    [:div.combo-selector
     [sut/preferred-buildings
      #{"Miner"}
      belt mining-productivity miner smelter assembler chemical]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      #{"Smelting Facility"}
      belt mining-productivity miner smelter assembler chemical]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      #{"Assembler"}
      belt mining-productivity miner smelter assembler chemical]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      #{"Chemical Facility"}
      belt mining-productivity miner smelter assembler chemical]])
  (preferred-state)
  {:inspect-data true})

(defcard-rg preferred-buildings-pairs
  (pb-card
    [:div.combo-selector
     [sut/preferred-buildings
      #{"Miner" "Smelting Facility"}
      belt mining-productivity miner smelter assembler chemical]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      #{"Miner" "Assembler"}
      belt mining-productivity miner smelter assembler chemical]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      #{"Miner" "Chemical Facility"}
      belt mining-productivity miner smelter assembler chemical]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      #{"Smelting Facility" "Assembler"}
      belt mining-productivity miner smelter assembler chemical]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      #{"Smelting Facility" "Chemical Facility"}
      belt mining-productivity miner smelter assembler chemical]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      #{"Assembler" "Chemical Facility"}
      belt mining-productivity miner smelter assembler chemical]])
  (preferred-state)
  {:inspect-data true})

(defcard-rg preferred-buildings-triples
  (pb-card
    [:div.combo-selector
     [sut/preferred-buildings
      #{"Miner" "Smelting Facility" "Assembler"}
      belt mining-productivity miner smelter assembler chemical]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      #{"Smelting Facility" "Assembler" "Chemical Facility"}
      belt mining-productivity miner smelter assembler chemical]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      #{"Miner" "Smelting Facility" "Chemical Facility"}
      belt mining-productivity miner smelter assembler chemical]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      #{"Miner" "Assembler" "Chemical Facility"}
      belt mining-productivity miner smelter assembler chemical]])
  (preferred-state)
  {:inspect-data true})

(defcard-rg preferred-buildings-full
  (pb-card
    [:div.combo-selector
     [sut/preferred-buildings
      #{"Miner" "Smelting Facility" "Assembler" "Chemical Facility"}
      belt mining-productivity miner smelter assembler chemical]])
  (preferred-state)
  {:inspect-data true})
