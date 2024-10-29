(ns dsp-calculator.devcards.calculator
  (:require [devcards.core]
            [spade.core :refer [defattrs defclass]]
            [dsp-calculator.ui :as ui]
            [dsp-calculator.ui.calculator :as calc]
            [dsp-calculator.devcards.calculator.preferred-buildings]
            [dsp-calculator.devcards.calculator.production]
            [reagent.core :as reagent])
  (:require-macros
   [devcards.core :as dc :refer [defcard defcard-rg]]))

(dc/defcard-doc
  "# Calculator

The calculator interface, the most important part of the site.")

(defcard-rg css
  "This includes the css for the calculator interface."
  (ui/stylesheet-includes))

(defcard-rg full-calculator
  (fn [state _]
    (let [selected (reagent/cursor state [:selected])
          ratio (reagent/cursor state [:ratio])
          production-facility (reagent/cursor state [:production-facility])
          specific (reagent/cursor state [:specific])
          timescale (reagent/cursor state [:timescale])
          proliferator (reagent/cursor state [:proliferator])
          summary (reagent/cursor state [:summary])
          tree (reagent/cursor state [:production-tree])]
      [calc/calculator
       :items [{:id 1101 :name "Iron Ingot" :pos [1 1]}
               {:id 1104 :name "Copper Ingot" :pos [1 2]}
               {:id 1102 :name "Magnet" :pos [2 1]}]
       :buildings [{:id 2201 :name "Tesla Tower" :pos [1 1]}
                   {:id 2202 :name "Wireless Power Tower" :pos [1 2]}
                   {:id 2001 :name "Conveyor Belt Mk.I" :pos [2 1]}]
       :selected            selected
       :ratio               ratio
       :production-facility production-facility
       :specific            specific
       :timescale           timescale
       :proliferator        proliferator
       :summary             summary
       :tree                tree]))
  (reagent/atom
   {:selected nil
    :ratio 1
    :specific nil
    :production-facility "Smelting Facility" ; TODO: FIXME!
    :timescale "minute"
    :proliferator "none"
    :summary {}
    :production-tree {}})
  {:inspect-data true})

(defcard-rg full-calculator-control
  (fn [state _]
    (let [selected (reagent/cursor state [:selected])
          ratio (reagent/cursor state [:ratio])
          production-facility (reagent/cursor state [:production-facility])
          specific (reagent/cursor state [:specific])
          timescale (reagent/cursor state [:timescale])
          proliferator (reagent/cursor state [:proliferator])]
      [:main.page.calculator
       [calc/combo-selector
        [{:id 1101 :name "Iron Ingot" :pos [1 1]}
         {:id 1104 :name "Copper Ingot" :pos [1 2]}
         {:id 1102 :name "Magnet" :pos [2 1]}]
        [{:id 2201 :name "Tesla Tower" :pos [1 1]}
         {:id 2202 :name "Wireless Power Tower" :pos [1 2]}
         {:id 2001 :name "Conveyor Belt Mk.I" :pos [2 1]}]
        :selected            selected
        :ratio               ratio
        :production-facility @production-facility
        :specific            specific
        :timescale           timescale
        :proliferator        proliferator]]))
  (reagent/atom
   {:selected nil
    :ratio 1
    :specific nil
    :production-facility "Smelting Facility" ; TODO: FIXME!
    :timescale "minute"
    :proliferator "none"})
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
     :id (str (gensym "recipe-picker"))
     :items [{:id 1101 :name "Iron Ingot" :pos [1 1]}
             {:id 1104 :name "Copper Ingot" :pos [1 2]}
             {:id 1102 :name "Magnet" :pos [2 1]}]
     :buildings [{:id 2201 :name "Tesla Tower" :pos [1 1]}
                 {:id 2202 :name "Wireless Power Tower" :pos [1 2]}
                 {:id 2001 :name "Conveyor Belt Mk.I" :pos [2 1]}]
     :open? true
     :close (fn [])]]])

(defcard-rg ratio-control
  (fn [state _]
    [:main.page.calculator
     [:div.combo-selector
      [calc/ratio-control state "Smelting Facility"]]])
  (reagent/atom 1)
  {:inspect-data true})

(defcard-rg specific-control
  (fn [state _]
    (let [specific (reagent/cursor state [:specific])
          timescale (reagent/cursor state [:timescale])]
      [:main.page.calculator
       [:div.combo-selector
        [calc/specific-control specific timescale]]]))
  (reagent/atom {:specific 1
                 :timescale "minute"})
  {:inspect-data true})

(defcard-rg controls
  (fn [state _]
    (let [ratio (reagent/cursor state [:ratio])
          production-facility (:production-facility @state)
          specific (reagent/cursor state [:specific])
          timescale (reagent/cursor state [:timescale])
          proliferator (reagent/cursor state [:proliferator])]
      (fn [state _]
        [:main.page.calculator
         [:div.combo-selector
          [calc/ratio-control ratio production-facility]
          [calc/specific-control specific timescale]
          [calc/proliferator-control proliferator]]])))
  (reagent/atom
   {:ratio 1
    :production-facility "Smelting Facility"
    :specific 1
    :timescale "minute"
    :proliferator "none"})
  {:inspect-data true})
