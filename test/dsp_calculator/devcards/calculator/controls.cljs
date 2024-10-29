(ns dsp-calculator.devcards.calculator.controls
  (:require [clojure.test :as t]
            [devcards.core]
            [reagent.core :as reagent]
            [dsp-calculator.ui.base :as base]
            [dsp-calculator.ui.calculator.controls :as sut])
  (:require-macros
   [devcards.core :refer [defcard-rg deftest]]))

(defcard-rg css
  "This includes the css for the calculator interface."
  (base/stylesheet-includes))

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

(deftest test-update-controls
  (t/is (= {:ratio nil
            :specific 10
            :timescale "minute"
            :proliferator "none"}
           (sut/update-controls {:ratio 1
                                 :specific nil
                                 :timescale "minute"
                                 :proliferator "none"}
                                :specific 10)))
  (t/is (= {:ratio 1
            :specific nil
            :timescale "minute"
            :proliferator "none"}
           (sut/update-controls {:ratio nil
                                 :specific 10
                                 :timescale "minute"
                                 :proliferator "none"}
                                :ratio 1)))
  (t/is (= {:ratio nil
            :specific 1
            :timescale "second"
            :proliferator "none"}
           (sut/update-controls {:ratio nil
                                 :specific 60
                                 :timescale "minute"
                                 :proliferator "none"}
                                :timescale "second")))
  (t/is (= {:ratio nil
            :specific 60
            :timescale "minute"
            :proliferator "none"}
           (sut/update-controls {:ratio nil
                                 :specific 1
                                 :timescale "second"
                                 :proliferator "none"}
                                :timescale "minute")))
  (t/is (= {:ratio 1
            :specific nil
            :timescale "second"
            :proliferator "none"}
           (sut/update-controls {:ratio 1
                                 :specific nil
                                 :timescale "minute"
                                 :proliferator "none"}
                                :timescale "second")))
  (t/is (= {:ratio 1
            :specific nil
            :timescale "minute"
            :proliferator "none"}
           (sut/update-controls {:ratio 1
                                 :specific nil
                                 :timescale "second"
                                 :proliferator "none"}
                                :timescale "minute")))
  (t/is (= {:ratio 1
            :specific nil
            :timescale "second"
            :proliferator "mixed.tsp"}
           (sut/update-controls {:ratio 1
                                 :specific nil
                                 :timescale "second"
                                 :proliferator "none"}
                                :proliferator "mixed.tsp")))
  (t/is (= {:ratio nil
            :specific 1
            :timescale "second"
            :proliferator "none"}
           (sut/update-controls {:ratio nil
                                 :specific 1
                                 :timescale "second"
                                 :proliferator "mixed.tsp"}
                                :proliferator "none"))))
