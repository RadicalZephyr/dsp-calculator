(ns dsp-calculator.ui.calculator
  (:require [spade.core :refer [defclass]]
            [reagent.core :as reagent]
            [dsp-calculator.ui.calculator.controls :as control]
            [dsp-calculator.ui.calculator.production :as prod]))

(declare combo-selector)

(defn calculator [& {:keys [items
                            buildings
                            selected
                            ratio
                            production-facility
                            specific
                            timescale
                            proliferator
                            summary
                            tree]}]
  [:main.page.calculator
   [combo-selector
    items
    buildings
    :selected selected
    :ratio ratio
    :production-facility production-facility
    :specific specific
    :timescale timescale
    :proliferator proliferator]
   [prod/production-tree summary tree]])

(defclass grid-pos [x y]
  {:grid-area (str x " / " y)})

(defn recipe-icon [item]
  [:span.recipe.icon {:data-icon (str "item." (:id item))
                      :title (:name item)}])

(defn recipe-grid [items selected close]
  [:ul.recipe-grid {:role "listbox"}
   (for [{[x y] :pos :as item} items]
     ^{:key (:id item)}
     [:li {:role "option"
           :class (grid-pos x y)
           :on-click (fn []
                       (reset! selected item)
                       (close))}
      [recipe-icon item]])])

(defn recipe-picker [& {:keys [id items buildings selected open? close]}]
  (let [first-tab? (reagent/atom true)
        first-tab (fn [] (reset! first-tab? true))
        second-tab (fn [] (reset! first-tab? false))]
    (fn [& {:keys [id items buildings open? close]}]
      (let [first-tab? @first-tab?]
        [:dialog.window.recipes {:id id
                                 :open open?
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
          [recipe-grid items selected close]]
         [:div#tabpanel-1.tabpanel.
          {:role "tabpanel"
           :class [(if (not first-tab?) "is-visible" "is-hidden")]}
          [recipe-grid buildings selected close]]
         [:div.corner-nav
          [:button.close {:on-click close}]]]))))

(defn selector-button [selected open-dialog]
  (let [[class icon title] (if selected
                             [".recipe" (str "item." (:id selected)) (:name selected)]
                             [nil "ui.select-recipe" "Select a recipe"])]
    [:div.icon {:class [class]
                :data-icon icon
                :title title
                :on-click open-dialog}
     (when (nil? selected) [:span.hint "Please select a recipe"])]))

(defn combo-selector [items buildings & {:keys [selected
                                                ratio
                                                production-facility
                                                specific
                                                timescale
                                                proliferator]}]
  (let [dialog-id (str (gensym "recipe-picker"))
        open-dialog (fn []
                      (let [dialog (.getElementById js/document dialog-id)]
                        (.show dialog)))
        close-dialog (fn []
                       (let [dialog (.getElementById js/document dialog-id)]
                         (.close dialog)))]
    (fn [items buildings & {:keys [selected
                                   ratio
                                   production-facility
                                   specific
                                   timescale
                                   proliferator]}]
      (let [selected-val @selected]
        `[:div.combo-selector
          ~[recipe-picker
            :id dialog-id
            :items items
            :buildings buildings
            :selected selected
            :open? false
            :close close-dialog]
          ~[:div.recipe-picker
            [selector-button selected-val open-dialog]]
          ~@(when selected-val
              [[control/ratio-control ratio production-facility]
               [control/specific-control specific timescale]
               [control/proliferator-control proliferator]])]))))
