(ns dsp-calculator.devcards.calculator
  (:require [devcards.core]
            [spade.core :refer [defclass]]
            [dsp-calculator.ui.calculator :as calc])
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

(defcard-rg combo-selector
  [:main.calculator
   [:div.combo-selector
    [calc/empty-selector]]
   [:br]
   [:div.combo-selector
    [:div.recipe-picker
     (let [item {:id 1101 :name "Iron Ingot"}]
       [calc/selected-recipe item])]]])

(defcard-rg recipe-picker-dialog
  [:main.page.calculator
   [:div.combo-selector
    [calc/recipe-picker
     [{:id 1101 :name "Iron Ingot" :pos [1 1]}
      {:id 1104 :name "Copper Ingot" :pos [1 2]}
      {:id 1102 :name "Magnet" :pos [2 1]}]
     [{:id 2201 :name "Tesla Tower" :pos [1 1]}
      {:id 2202 :name "Wireless Power Tower" :pos [1 2]}
      {:id 2001 :name "Conveyor Belt Mk.I" :pos [2 1]}]]]])

(defcard-rg controls
  [:main.page.calculator
   [:div.combo-selector
    [:label.ratio
     [:input.factor {:type "number" :min "0"}]
     [:div.steppers
      [:button.increment]
      [:button.decrement]]
     [:span.text "× Smelting Facility"]]
    [:label.specific
     [:input.factor {:type "number" :min "0"}]
     [:div.steppers
      [:button.increment]
      [:button.decrement]]
     [:span.text "items"
      [:select.timescale
       [:option {:value "minute"} "per minute"]
       [:option {:value "second"} "per second"]]]]
    [:label.proliferator
     (let [max-proliferator "Proliferator Mk.III"]
       [:span {:title (str "Highest unlocked tier will be used." max-proliferator)}
        "Proliferator: "])
     [:select {:title "No proliferator to be used"}
      [:option {:value "none"} "None"]
      [:option {:value "mixed.tsp"} "Mix by The Superior Tentacle"]
      [:option {:value "mixed.ab"} "Mix by Aaronbog"]
      [:option {:value "speedup"} "Production Speedup"]
      [:option {:value "extra"} "Extra Products"]]]]])

(defclass grid-pos [x y]
  {:grid-row (str x " / " y)})


(defn preferred-building-option [[x y] type selected building]
  [:label {:class [(grid-pos x y)
                   (when (= selected (:id building)) "is-selected")]}
   [:input {:type "radio"
            :name type
            :value (str (:id building))
            :title (str (:name building) " - Production Speed: " (:count building))}]
   [:span.item.icon {:data-icon (str "item." (:id building))
                     :title (:name building)
                     :data-count (:count building)
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
      [:span.name {:class (grid-pos 1 2)} "Smelting Facility"]
      [preferred-building-option
       [1 2]
       "smelter"
       2302
       {:id 2302
        :name "Arc Smelter"
        :count "1×"}]
      [preferred-building-option
       [1 2]
       "smelter"
       2302
       {:id 2315
        :name "Plane Smelter"
        :count "2×"}]
      [preferred-building-option
       [1 2]
       "smelter"
       2302
       {:id 2319
        :name "Negentropy Smelter"
        :count "3×"}]]]]])
