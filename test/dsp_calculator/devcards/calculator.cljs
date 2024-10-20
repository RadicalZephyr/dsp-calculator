(ns dsp-calculator.devcards.calculator
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
   [:link {:rel "stylesheet" :href "/css/cursor.css"}]
   [:link {:rel "stylesheet" :href "/css/fonts.css"}]
   [:link {:rel "stylesheet" :href "/css/icons.css"}]
   [:link {:rel "stylesheet" :href "/css/pages/calculator.css"}]
   [:link {:rel "stylesheet" :href "/css/components/combo-selector.css"}]])

(defcard-rg combo-selector
  [:div.combo-selector
   [:div.recipe-picker
    [:div.icon {:data-icon "ui.select-recipe" :title "Select a recipe"}]
    [:span.hint "Please select a recipe"]]])
