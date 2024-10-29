(ns dsp-calculator.devcards.calculator.controls
  (:require [devcards.core]
            [reagent.core :as reagent]
            [dsp-calculator.ui :as ui]
            [dsp-calculator.ui.calculator.controls :as sut])
  (:require-macros
   [devcards.core :refer [defcard-rg]]))

(defcard-rg css
  "This includes the css for the calculator interface."
  (ui/stylesheet-includes))

(defcard-rg controls
  (fn [state _]
    (let [ratio (reagent/cursor state [:ratio])
          specific (reagent/cursor state [:specific])
          timescale (reagent/cursor state [:timescale])
          proliferator (reagent/cursor state [:proliferator])]
      (fn [state _]
        [:main.page.calculator
         [:div.combo-selector
          [sut/ratio-control ratio "Smelting Facility"]
          [sut/specific-control specific timescale]
          [sut/proliferator-control proliferator]]])))
  (reagent/atom
   {:ratio 1
    :specific 1
    :timescale "minute"
    :proliferator "none"})
  {:inspect-data true})

(defcard-rg ratio-control
  (fn [state _]
    (let [ratio (reagent/cursor state [:ratio])]
      [:main.page.calculator
       [:div.combo-selector
        [sut/ratio-control ratio "Smelting Facility"]]]))
  (reagent/atom {:ratio 1})
  {:inspect-data true})

(defcard-rg specific-control
  (fn [state _]
    (let [specific (reagent/cursor state [:specific])
          timescale (reagent/cursor state [:timescale])]
      [:main.page.calculator
       [:div.combo-selector
        [sut/specific-control specific timescale]]]))
  (reagent/atom {:specific 1
                 :timescale "minute"})
  {:inspect-data true})

(defcard-rg proliferator-control
  (fn [state _]
    (let [proliferator (reagent/cursor state [:proliferator])]
      [:main.page.calculator
       [:div.combo-selector
        [sut/proliferator-control proliferator]]]))
  (reagent/atom {:proliferator "none"})
  {:inspect-data true})
