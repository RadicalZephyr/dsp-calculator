(ns dsp-calculator.ui.calculator
  (:require [spade.core :refer [defclass]]
            [reagent.core :as reagent]))

(declare combo-selector)

(defn calculator []
  [:main.page.calculator
   [combo-selector]])

(defclass grid-pos [x y]
  {:grid-area (str x " / " y)})

(defn recipe-icon [item]
  [:span.recipe.icon {:data-icon (str "item." (:id item))
                      :title (:name item)}])

(defn recipe-grid [items]
  [:ul.recipe-grid {:role "listbox"}
   (for [{[x y] :pos :as item} items]
     ^{:key (:id item)}
     [:li {:role "option"
           :class (grid-pos x y)}
      [recipe-icon item]])])

(defn recipe-picker [items buildings]
  (let [first-tab? (reagent/atom true)
        first-tab (fn [] (reset! first-tab? true))
        second-tab (fn [] (reset! first-tab? false))]
    (fn [items buildings]
      (let [first-tab? @first-tab?]
       [:dialog.window.recipes {:open true
                                :style {:position "relative"}}
        [:header "Select a Recipe"]
        [:div.tablist {:role "tablist"}
         [:button#tab-0.tab {:type "button"
                             :role "tab"
                             :on-click first-tab
                             :aria-selected (str first-tab?)
                             :aria-controls "tabpanel-0"}
          "Items"]
         [:button#tab-1.tab {:type "button"
                             :role "tab"
                             :on-click second-tab
                             :aria-selected (str (not first-tab?))
                             :aria-controls "tabpanel-1"}
          "Buildings"]]
        [:div#tabpanel-0.tabpanel
         {:role "tabpanel"
          :class [(if first-tab? "is-visible" "is-hidden")]}
         [recipe-grid items]]
        [:div#tabpanel-1.tabpanel.
         {:role "tabpanel"
          :class [(if (not first-tab?) "is-visible" "is-hidden")]}
         [recipe-grid buildings]]
        [:div.corner-nav
         [:button.close]]]))))

(defn empty-selector []
  [:div.recipe-picker
   [:div.icon {:data-icon "ui.select-recipe" :title "Select a recipe"}]
   [:span.hint "Please select a recipe"]])

(defn selected-recipe [selected]
  [recipe-icon selected])

(defn combo-selector [selected]
  [:div.combo-selector
   (if selected
     [:div.recipe-picker
      [selected-recipe selected]]
     [empty-selector])])
