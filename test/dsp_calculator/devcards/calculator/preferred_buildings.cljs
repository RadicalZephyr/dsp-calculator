(ns dsp-calculator.devcards.calculator.preferred-buildings
  (:require [devcards.core]
            [dsp-calculator.ui :as ui]
            [dsp-calculator.ui.calculator.preferred-buildings :as sut]
            [reagent.core :as reagent])
  (:require-macros
   [devcards.core :refer [defcard-rg]]))

(defcard-rg css
  "This includes the css for the calculator interface."
  (ui/stylesheet-includes))

(defcard-rg preferred-buildings
  (fn [state _]
    (let [belt (reagent/cursor state [:belt])
          mining-productivity (reagent/cursor state [:mining-productivity])
          miner (reagent/cursor state [:miner])
          smelter (reagent/cursor state [:smelter])
          assembler (reagent/cursor state [:assembler])
          chemical (reagent/cursor state [:chemical])]
      (fn [state _]
        [:main.page.calculator
         [:div.combo-selector
          [:details.preferred.preferred-buildings {:open true}
           [:summary "Preferred Buildings"]
           [:div.fields]]]
         [:br]
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
           belt mining-productivity miner smelter assembler chemical]]

         [:br]
         [:div.combo-selector
          [sut/preferred-buildings
           #{"Smelting Facility"
             "Assembler"}
           belt mining-productivity miner smelter assembler chemical]]
         [:br]
         [:div.combo-selector
          [sut/preferred-buildings
           #{"Smelting Facility"
             "Chemical Facility"}
           belt mining-productivity miner smelter assembler chemical]]
         [:br]
         [:div.combo-selector
          [sut/preferred-buildings
           #{"Assembler"
             "Chemical Facility"}
           belt mining-productivity miner smelter assembler chemical]]

         [:br]
         [:div.combo-selector
          [sut/preferred-buildings
           #{"Miner"
             "Smelting Facility"
             "Assembler"
             "Chemical Facility"}
           belt mining-productivity miner smelter assembler chemical]]])))
  (reagent/atom
   {:belt (first sut/conveyor-belts)
    :mining-productivity (first sut/mining-productivity-techs)
    :miner (first sut/miners)
    :smelter (first sut/smelters)
    :assembler (first sut/assemblers)
    :chemical (first sut/chemical-plants)})
  {:inspect-data true})
