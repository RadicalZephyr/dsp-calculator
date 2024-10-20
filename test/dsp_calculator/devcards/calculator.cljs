(ns dsp-calculator.devcards.calculator
  (:require [devcards.core])
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
   [:link {:rel "stylesheet" :href "/css/components/tabs.css"}]])

(defcard-rg combo-selector
  [:main.calculator
   [:div.combo-selector
    [:div.recipe-picker
     [:div.icon {:data-icon "ui.select-recipe" :title "Select a recipe"}]
     [:span.hint "Please select a recipe"]]]
   [:div]
   [:div.combo-selector
    [:div.recipe-picker
     (let [item {:id 1101 :name "Iron Ingot"}]
       [:span.recipe.icon {:data-icon (str "item." item)
                           :title (:name item)}])]]])

(defcard-rg recipe-picker-dialog
  [:main.calculator
   [:div.combo-selector
    [:dialog.window.recipes {:open true
                             :style {:position "relative"}}
     [:header "Select a Recipe"]
     [:div.tablist {:role "tablist"}
      [:button#tab-0.tab {:type "button" :role "tab" :tabIndex "0"}
       "Items"]
      [:button#tab-1.tab {:type "button" :role "tab" :tabIndex "-1"}
       "Buildings"]]
     [:div#tabpanel-0.tabpanel.is-visible {:role "tabpanel"}
      [:ul.recipe-grid {:role "listbox"}
       (for [item [{:id 1101 :name "Iron Ingot"}]]
         ^{:key item}
         [:li {:role "option"
               :style {:grid-area "1 / 1"}}
          [:span.recipe.icon {:data-icon (str "item." item)
                              :title (:name item)}]])]]
     [:div#tabpanel-1.tabpanel.is-hidden {:role "tabpanel"}]
     [:div.corner-nav
      [:button.close]]]]])

(defcard-rg controls
  [:main.calculator
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
