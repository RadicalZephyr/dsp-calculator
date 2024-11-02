(ns dsp-calculator.ui.calculator
  (:require [spade.core :refer [defclass]]
            [reagent.core :as reagent]
            [dsp-calculator.ui.calculator.controls :as control]
            [dsp-calculator.ui.calculator.production :as prod]))

(declare combo-selector)

(defn calculator [& {:keys [recipes
                            selected
                            update-selected
                            controls
                            update-controls
                            context
                            summary
                            tree]}]
  [:main.page.calculator
   [combo-selector
    :recipes recipes
    :selected selected
    :update-selected update-selected
    :controls controls
    :update-controls update-controls]
   [prod/production-tree context summary tree]])

(defclass grid-pos [x y]
  {:grid-area (str x " / " y)})

(defn recipe-icon [item]
  [:span.recipe.icon {:data-icon (str "recipe." (:id item))
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

(defn recipe-tab [id selected controls click-fn label]
  [:button.tab {:id            id
                :type          "button"
                :role          "tab"
                :on-click      click-fn
                :aria-selected selected
                :aria-controls controls}
   label])

(defn tab-panel [id tab-selected? recipes selected close]
  [:div.tabpanel
   {:id    id
    :role  "tabpanel"
    :class [(if tab-selected? "is-visible" "is-hidden")]}
   [recipe-grid recipes selected close]])

(defn recipe-picker [& {:keys [id recipes selected open? close]}]
  (let [first-tab? (reagent/atom true)
        first-tab (fn [] (reset! first-tab? true))
        second-tab (fn [] (reset! first-tab? false))]
    (fn [& {:keys [id recipes open? close]}]
      (let [first-tab? @first-tab?]
        [:dialog.window.recipes {:id id
                                 :open open?
                                 :style {:position "relative"}}
         [:header "Select a Recipe"]
         [:div.tablist {:role "tablist"}
          [recipe-tab "tab-0"
                      (str first-tab?)
                      "tabpanel-0"
                      first-tab
                      "Items"]
          [recipe-tab "tab-1"
                      (str (not first-tab?))
                      "tabpanel-1"
                      second-tab
                      "Buildings"]]
         [tab-panel "tabpanel-0"
                    first-tab?
                    (:items recipes)
                    selected
                    close]
         [tab-panel "tabpanel-1"
                    (not first-tab?)
                    (:buildings recipes)
                    selected
                    close]
         [:div.corner-nav
          [:button.close {:on-click close}]]]))))

(defn selector-button [selected open-dialog]
  (let [[class icon title] (if selected
                             [".recipe" (str "item." (:id selected)) (:name selected)]
                             [nil "ui.select-recipe" "Select a recipe"])]
    [:div.icon {:class     [class]
                :data-icon icon
                :title     title
                :on-click  open-dialog}
     (when (nil? selected) [:span.hint "Please select a recipe"])]))

(defn combo-selector [& {:keys [recipes
                                selected
                                controls
                                update-controls]}]
  (let [dialog-id (str (gensym "recipe-picker"))
        open-dialog (fn []
                      (let [dialog (.getElementById js/document dialog-id)]
                        (.show dialog)))
        close-dialog (fn []
                       (let [dialog (.getElementById js/document dialog-id)]
                         (.close dialog)))]
    (fn [& {:keys [recipes
                   selected
                   controls
                   update-controls]}]
      (let [selected-recipe @selected
            production-facility (:facility selected-recipe)
            {:keys [ratio specific timescale proliferator]} @controls]
        `[:div.combo-selector
          ~[recipe-picker
            :id        dialog-id
            :recipes   recipes
            :selected  selected
            :open?     false
            :close     close-dialog]
          ~[:div.recipe-picker
            [selector-button selected-recipe open-dialog]]
          ~@(when selected-recipe
              [[control/ratio-control update-controls ratio production-facility]
               [control/specific-control update-controls specific timescale]
               [control/proliferator-control update-controls proliferator]])]))))
