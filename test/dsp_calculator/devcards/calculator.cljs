(ns dsp-calculator.devcards.calculator
  (:require [devcards.core]
            [com.gfredericks.exact :as e]
            [reagent.core :as reagent]
            [spade.core :refer [defattrs defclass]]
            [dsp-calculator.ui.base :as ui]
            [dsp-calculator.ui.calculator :as calc]
            [dsp-calculator.ui.calculator.controls :as control]
            [dsp-calculator.devcards.calculator.controls]
            [dsp-calculator.devcards.calculator.preferred-buildings]
            [dsp-calculator.devcards.calculator.production])
  (:require-macros
   [devcards.core :as dc :refer [defcard defcard-rg]]))

(dc/defcard-doc
  "# Calculator

The calculator interface, the most important part of the site.")

(defcard-rg css
  "This includes the css for the calculator interface."
  (ui/stylesheet-includes))

(def test-recipes
  {:items [{:id 1101
            :name "Iron Ingot"
            :facility "Smelting Facility"
            :pos [1 1]}
           {:id 1104
            :name "Copper Ingot"
            :facility "Smelting Facility"
            :pos [1 2]}
           {:id 1102
            :name "Magnet"
            :facility "Smelting Facility"
            :pos [2 1]}]
   :buildings [{:id 2201
                :name "Tesla Tower"
                :facility "Assembler"
                :pos [1 1]}
               {:id 2202
                :name "Wireless Power Tower"
                :facility "Assembler"
                :pos [1 2]}
               {:id 2001
                :name "Conveyor Belt Mk.I"
                :facility "Assembler"
                :pos [2 1]}]})

(defcard-rg full-calculator
  (fn [state _]
    (let [selected (reagent/cursor state [:selected])
          control-spec (reagent/cursor state [:controls])
          controls (reagent/reaction
                    (control/render-controls [@control-spec @selected]))
          update-controls #(swap! control-spec control/update-controls %1 %2)
          context (reagent/cursor state [:context])
          summary (reagent/cursor state [:summary])
          tree (reagent/cursor state [:production-tree])]
      [calc/calculator
       :recipes         test-recipes
       :selected        selected
       :controls        controls
       :update-controls update-controls
       :context         context
       :summary         summary
       :tree            tree]))
  (reagent/atom
   {:selected nil
    :controls {:ratio 1
               :specific nil
               :timescale "minute"
               :proliferator "none"}
    :context {:ratio (e/native->integer 1)
              :timescale "minute"
              :belt-rate (e/native->integer 6)}
    :summary {}
    :production-tree {}})
  {:inspect-data true})

(defcard-rg full-calculator-control
  (fn [state _]
    (let [selected (reagent/cursor state [:selected])
          control-spec (reagent/cursor state [:controls])
          controls (reagent/reaction
                    (control/render-controls [@control-spec @selected]))
          update-controls #(swap! control-spec control/update-controls %1 %2)]
      (fn [state _]
        [:main.page.calculator
         [calc/combo-selector
          :recipes test-recipes
          :selected selected
          :controls controls
          :update-controls update-controls]])))
  (reagent/atom
   {:selected nil
    :controls {:ratio 1
               :specific nil
               :timescale "minute"
               :proliferator "none"}})
  {:inspect-data true})

(defcard-rg selector-button
  [:main.calculator
   [:div.combo-selector
    [:div.recipe-picker
     [calc/selector-button nil]]]
   [:br]
   [:div.combo-selector
    [:div.recipe-picker
     (let [item {:id 1101 :name "Iron Ingot"}]
       [calc/selector-button item])]]])

(defcard-rg recipe-picker-dialog
  [:main.page.calculator
   [:div.combo-selector
    [calc/recipe-picker
     :id        (str (gensym "recipe-picker"))
     :recipes   test-recipes
     :open?     true
     :close     (fn [])]]])
