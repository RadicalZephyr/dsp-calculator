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
  (reagent/atom (sut/default-preferences)))

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
             :facilities (atom #{"Water Pump"}))]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      (assoc args
             :facilities (atom #{"Mining Facility"}))]]
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
             :facilities (atom #{"Mining Facility" "Smelting Facility"}))]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      (assoc args
             :facilities (atom #{"Mining Facility" "Assembler"}))]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      (assoc args
             :facilities (atom #{"Mining Facility" "Chemical Facility"}))]]
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
             :facilities (atom #{"Mining Facility" "Smelting Facility" "Assembler"}))]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      (assoc args
             :facilities (atom #{"Smelting Facility" "Assembler" "Chemical Facility"}))]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      (assoc args
             :facilities (atom #{"Mining Facility" "Smelting Facility" "Chemical Facility"}))]]
    [:br]
    [:div.combo-selector
     [sut/preferred-buildings
      (assoc args
             :facilities (atom #{"Mining Facility" "Assembler" "Chemical Facility"}))]])
  (preferred-state)
  {:inspect-data true})

(defcard-rg preferred-buildings-full
  (pb-card [args]
    [:div.combo-selector
     [sut/preferred-buildings
      (assoc args
             :facilities (atom #{"Mining Facility" "Smelting Facility" "Assembler" "Chemical Facility"}))]])
  (preferred-state)
  {:inspect-data true})
