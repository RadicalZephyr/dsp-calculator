(ns dsp-calculator.devcards.calculator
  (:require [devcards.core]
            [spade.core :refer [defclass]]
            [dsp-calculator.ui.calculator :as calc]
            [reagent.core :as reagent])
  (:require-macros
   [devcards.core :as dc :refer [defcard defcard-rg]]))

(dc/defcard-doc
  "# Calculator

The calculator interface, the most important part of the site.")

(defcard-rg css
  "This includes the css for the calculator interface."
  [:div
   [:link {:rel "stylesheet" :href "/css/reset.css"}]
   [:link {:rel "stylesheet" :href "/css/variables.css"}]
   [:link {:rel "stylesheet" :href "/css/skeleton.css"}]
   [:link {:rel "stylesheet" :href "/css/cursor.css"}]
   [:link {:rel "stylesheet" :href "/css/fonts.css"}]
   [:link {:rel "stylesheet" :href "/css/icons.css"}]
   [:link {:rel "stylesheet" :href "/css/dev-icons.css"}]
   [:link {:rel "stylesheet" :href "/css/pages/calculator.css"}]
   [:link {:rel "stylesheet" :href "/css/components/combo-selector.css"}]
   [:link {:rel "stylesheet" :href "/css/components/dialog.css"}]
   [:link {:rel "stylesheet" :href "/css/components/item.css"}]
   [:link {:rel "stylesheet" :href "/css/components/recipe.css"}]
   [:link {:rel "stylesheet" :href "/css/components/tabs.css"}]
   [:style "@layer reset,skeleton,components,pages;"]])

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

(defclass grid-row [x y]
  {:grid-row (str x " / " y)})


(defn preferred-building-option [[x y] type selected building]
  [:label {:class [(grid-row x y)
                   (when (= selected (:id building)) "is-selected")]}
   [:input {:type "radio"
            :name type
            :value (str (:id building))
            :title (str (:name building) " - Production Speed: " (:count building))}]
   [:span.item.icon {:data-icon (str "item." (:id building))
                     :title (:name building)
                     :data-count (str (:count building) "×")
                     :lang "en-US"}]])

(defcard-rg preferred-buildings
  [:main.page.calculator
   [:div.combo-selector
    [:details.preferred.preferred-buildings {:open true}
     [:summary "Preferred Buildings"]
     [:div.fields]]]
   [:br]
   [:div.combo-selector
    [:details.preferred.preferred-buildings {:open true}
     [:summary "Preferred Buildings"]
     [:div.fields
      [:span.name {:class (grid-row 1 2)} "Smelting Facility"]
      [preferred-building-option
       [1 2]
       "smelter"
       2302
       {:id 2302
        :name "Arc Smelter"
        :count "1"}]
      [preferred-building-option
       [1 2]
       "smelter"
       2302
       {:id 2315
        :name "Plane Smelter"
        :count "2"}]
      [preferred-building-option
       [1 2]
       "smelter"
       2302
       {:id 2319
        :name "Negentropy Smelter"
        :count "3"}]]]]])
