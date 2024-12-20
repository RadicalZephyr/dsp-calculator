(ns dsp-calculator.devcards.calculator
  (:require [devcards.core]
            [ajax.core :refer [GET]]
            [ajax.edn]
            [reagent.core :as reagent]
            [spade.core :refer [defattrs defclass]]
            [dsp-calculator.rational :as r]
            [dsp-calculator.production :as prod]
            [dsp-calculator.ui.base :as ui]
            [dsp-calculator.ui.calculator :as calc]
            [dsp-calculator.ui.calculator.controls :as control]
            [dsp-calculator.ui.calculator.preferred-buildings :as pref]
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

(defn receive-edn [ratom response]
  (reset! ratom response))

(defn error-handler [type {:keys [status status-text]}]
  (.log js/console (str "Error fetching " type ". " status " " status-text)))

(defn fetch-once [type ratom]
  (when (not (seq @ratom))
    (GET (str "/data/" type ".edn")
      {:handler #(receive-edn ratom %)
       :error-handler #(error-handler type %)
       :response-format (ajax.edn/edn-response-format)})))

(def items (reagent/atom {}))

(def recipes (reagent/atom {}))

(def dialog-recipes
  (reagent/reaction
   (when-let [recipes @recipes]
     (calc/split-recipes recipes))))

(def recipes-by-output-id
  (reagent/reaction
   (when-let [recipes @recipes]
     (prod/group-by-outputs recipes))))

(defcard-rg full-calculator
  (fn [state _]
    (fetch-once "items_EN" items)
    (fetch-once "recipes_EN" recipes)
    (let [selected (reagent/cursor state [:selected])
          control-spec (reagent/cursor state [:controls])
          controls (reagent/reaction
                    (control/render-controls [@control-spec @selected]))
          update-controls #(swap! control-spec control/update-controls %1 %2)
          tree (reagent/reaction
                (when-let [id (:id @selected)]
                  (prod/production-tree @items
                                        @recipes
                                        @recipes-by-output-id
                                        id)))
          summary (reagent/reaction
                   (let [tree @tree]
                     (when (seq tree)
                       (prod/summarize tree))))
          facilities (reagent/reaction
                      (:facilities @summary))
          timescale (reagent/cursor state [:controls :timescale])
          preferences
          {:facilities facilities
           :timescale timescale
           :belt (reagent/cursor state [:belt])
           :mining-productivity (reagent/cursor state [:mining-productivity])
           :miner (reagent/cursor state [:miner])
           :smelter (reagent/cursor state [:smelter])
           :assembler (reagent/cursor state [:assembler])
           :chemical (reagent/cursor state [:chemical])}
          context (reagent/reaction
                   (when (seq @selected)
                     (let [controls @controls]
                       (-> controls
                           (assoc :belt-rate (:speed @(:belt preferences)))
                           (update :ratio r/int)))))]
      [calc/calculator
       :recipes         dialog-recipes
       :selected        selected
       :controls        controls
       :update-controls update-controls
       :context         context
       :preferences     preferences
       :summary         summary
       :tree            tree]))
  (reagent/atom
   {:selected nil
    :controls {:ratio 1
               :specific nil
               :timescale "minute"
               :proliferator "none"}
    :belt (first pref/conveyor-belts)
    :mining-productivity (first pref/mining-productivity-techs)
    :miner (first pref/miners)
    :smelter (first pref/smelters)
    :assembler (first pref/assemblers)
    :chemical (first pref/chemical-plants)})
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
          :recipes dialog-recipes
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
     :recipes   dialog-recipes
     :open?     true
     :close     (fn [])]]])
