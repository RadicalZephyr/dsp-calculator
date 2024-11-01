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
    (let [rendered-state
          (reagent/reaction
           (sut/render-controls
            (let [s @state]
              [s (:recipe s)])))
          change #(swap! state sut/update-controls %1 %2)]
      (fn [state _]
        (let [{:keys [ratio
                      specific
                      timescale
                      proliferator]} @rendered-state]
          [:main.page.calculator
           [:div.combo-selector
            [sut/ratio-control change ratio "Smelting Facility"]
            [sut/specific-control change specific timescale]
            [sut/proliferator-control change proliferator]]]))))
  (reagent/atom
   {:recipe {:id 1
             :name "Iron Ingot"
             :time-spend 60
             :made-from-string "Smelting Facility"
             :items {1001 1}
             :results {1101 1}}
    :ratio 1
    :specific nil
    :timescale "minute"
    :proliferator "none"})
  {:inspect-data true})

(defcard-rg ratio-control
  (fn [state _]
    (let [ratio (reagent/cursor state [:ratio])
          change #(swap! state sut/update-controls %1 %2)]
      (fn [state _]
        [:main.page.calculator
         [:div.combo-selector
          [sut/ratio-control change @ratio "Smelting Facility"]]])))
  (reagent/atom {:ratio 1
                 :specific nil})
  {:inspect-data true})

(defcard-rg specific-control
  (fn [state _]
    (let [specific (reagent/cursor state [:specific])
          timescale (reagent/cursor state [:timescale])
          change #(swap! state sut/update-controls %1 %2)]
      (fn [state _]
        [:main.page.calculator
         [:div.combo-selector
          [sut/specific-control change @specific @timescale]]])))
  (reagent/atom {:ratio nil
                 :specific 1
                 :timescale "minute"})
  {:inspect-data true})

(defcard-rg proliferator-control
  (fn [state _]
    (let [proliferator (reagent/cursor state [:proliferator])
          change #(swap! state sut/update-controls %1 %2)]
      (fn [state _]
        [:main.page.calculator
         [:div.combo-selector
          [sut/proliferator-control change @proliferator]]])))
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

(def iron-ingots
  {:id 1101,
   :name "Iron Ingot",
   :count 1,
   :recipe 1,
   :time-spend 60,
   :made-from-string "Smelting Facility",
   :items {1001 1},
   :results {1101 1}})

(def steel
  {:id 1103
   :name "Steel"
   :count 1
   :recipe 63
   :time-spend 180
   :items {1101 1}
   :results {1103 1}})

(deftest test-render-controls
  (t/is (= {:ratio 1
            :specific 60
            :timescale "minute"
            :proliferator "none"}
           (sut/render-controls [{:ratio 1
                                  :specific nil
                                  :timescale "minute"
                                  :proliferator "none"}
                                 iron-ingots])
           (sut/render-controls [{:ratio nil
                                  :specific 60
                                  :timescale "minute"
                                  :proliferator "none"}
                                 iron-ingots])))

  (t/is (= {:ratio 1
            :specific 1
            :timescale "second"
            :proliferator "none"}
           (sut/render-controls [{:ratio 1
                                  :specific nil
                                  :timescale "second"
                                  :proliferator "none"}
                                 iron-ingots])
           (sut/render-controls [{:ratio nil
                                  :specific 1
                                  :timescale "second"
                                  :proliferator "none"}
                                 iron-ingots])))

  (t/is (= {:ratio 1
            :specific 20
            :timescale "minute"
            :proliferator "none"}
           (sut/render-controls [{:ratio 1
                                  :specific nil
                                  :timescale "minute"
                                  :proliferator "none"}
                                 steel])
           (sut/render-controls [{:ratio nil
                                  :specific 20
                                  :timescale "minute"
                                  :proliferator "none"}
                                 steel])))

  (t/is (= {:ratio 1
            :specific (/ 1 3)
            :timescale "second"
            :proliferator "none"}
           (sut/render-controls [{:ratio 1
                                  :specific nil
                                  :timescale "second"
                                  :proliferator "none"}
                                 steel])
           (sut/render-controls [{:ratio nil
                                  :specific (/ 1 3)
                                  :timescale "second"
                                  :proliferator "none"}
                                 steel]))))
