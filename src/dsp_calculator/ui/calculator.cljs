(ns dsp-calculator.ui.calculator
  (:require [spade.core :refer [defclass]]
            [reagent.core :as reagent]))

(declare combo-selector production-tree)

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
    :production-facility @production-facility
    :specific specific
    :timescale timescale
    :proliferator proliferator]
   [production-tree @summary @tree]])

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

(defn inc-dec-buttons [ratom]
  [:div.steppers
   [:button.increment {:on-click #(swap! ratom inc)}]
   [:button.decrement {:on-click #(swap! ratom dec)}]])

(defn ratio-control [ratio production-facility]
  [:label.ratio
   [:input.factor
    {:type "number"
     :min "0"
     :value @ratio
     :on-change #(reset! ratio (-> % .-target .-value))}]
   [inc-dec-buttons ratio]
   [:span.text (str "× " production-facility)]])

(defn specific-control [specific timescale]
  [:label.specific
   [:input.factor
    {:type "number"
     :min "0"
     :value @specific
     :on-change #(reset! specific (-> % .-target .-value))}]
   [inc-dec-buttons specific]
   [:span.text "items"
    [:select.timescale {:on-change #(reset! timescale (-> % .-target .-value))}
     [:option {:value "minute"} "per minute"]
     [:option {:value "second"} "per second"]]]])

(defn proliferator-control [proliferator]
  [:label.proliferator
   (let [max-proliferator "Proliferator Mk.III"]
     [:span {:title (str "Highest unlocked tier will be used." max-proliferator)}
      "Proliferator: "])
   [:select {:title "No proliferator to be used"
             :on-change #(reset! proliferator (-> % .-target .-value))}
    [:option {:value "none"} "None"]
    [:option {:value "mixed.tsp"} "Mix by The Superior Tentacle"]
    [:option {:value "mixed.ab"} "Mix by Aaronbog"]
    [:option {:value "speedup"} "Production Speedup"]
    [:option {:value "extra"} "Extra Products"]
    [:option {:value "custom"} "Customized"]]])

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
              [[ratio-control ratio production-facility]
               [specific-control specific timescale]
               [proliferator-control proliferator]])]))))

(defclass grid-row [x y]
  {:grid-row (str x " / " y)})

(defn item-id [item]
  (str "item." (:id item)))

(defn preferred-building-option [[x y] type selected change building]
  [:label {:class [(grid-row x y)
                   (when (= (:id selected) (:id building)) "is-selected")]}
   [:input {:type "radio"
            :name type
            :value (str (:id building))
            :selected (= (:id selected) (:id building))
            :on-change #(change building)
            :title (str (:name building)
                        " - Production&nbsp;Speed:&nbsp;"
                        (:count building))}]
   [:span.item.icon {:title (:name building)
                     :data-icon (item-id building)
                     :data-count (str (:count building) "×")
                     :lang "en-US"}]])

(defn preferred-belt-option [[x y] selected change building]
  [:label {:class [(grid-row x y)
                   (when (= (:id selected) (:id building)) "is-selected")]}
   [:input {:type "radio"
            :name "belt"
            :value (str (:id building))
            :selected (= (:id selected) (:id building))
            :on-change #(change building)
            :title (str (:name building)
                        " — Transport&nbsp;Speed:&nbsp;"
                        (:speed building)
                        "&nbsp;items&nbsp;per&nbsp;minute")}]
   [:span.item.icon {:title (:name building)
                     :data-icon (item-id building)
                     :data-per (str (:speed building))
                     :lang "en-US"}]])

(def conveyor-belts
  [{:id 2001
    :name "Conveyor Belt MK.I"
    :speed 360}
   {:id 2002
    :name "Conveyor Belt MK.II"
    :speed 720}
   {:id 2003
    :name "Conveyor Belt MK.III"
    :speed 1800}])

(def smelters
  [{:id 2302
    :name "Arc Smelter"
    :count 1}
   {:id 2315
    :name "Plane Smelter"
    :count 2}
   {:id 2319
    :name "Negentropy Smelter"
    :count 3}])

(def assemblers
  [{:id 2303
    :name "Assembling Machine Mk.I"
    :count 0.75}
   {:id 2304
    :name "Assembling Machine Mk.II"
    :count 1}
   {:id 2305
    :name "Assembling Machine Mk.III"
    :count 2}
   {:id 2318
    :name "Re-composing Assembler"
    :count 3}])

(def chemical-plants
  [{:id 2309
    :name "Chemical Plant"
    :count 1}
   {:id 2317
    :name "Quantum Chemical Plant"
    :count 2}])

(defn preferred-buildings [facilities belt smelter assembler chemical]
  (let [belt-val @belt
        smelter-val @smelter
        assembler-val @assembler
        chemical-val @chemical]
    [:details.preferred.preferred-buildings {:open true}
     [:summary "Preferred Buildings"]
     `[:div.fields
       [:span.name {:class ~(grid-row 1 2)} "Logistics"]
       ~@(->> conveyor-belts
              (map
               (fn [item]
                 [preferred-belt-option [1 2] belt-val #(reset! belt %) item])))

       ~@(when (contains? facilities "Smelting Facility")
           (->> smelters
                (map (fn [item]
                       [preferred-building-option [2 3] "smelter" smelter-val #(reset! smelter %) item]))
                (into [[:span.name {:class (grid-row 2 3)} "Smelting Facility"]])))

       ~@(when (contains? facilities "Assembler")
           (->> assemblers
                (map (fn [item]
                       [preferred-building-option [3 4] "assembler" assembler-val #(reset! assembler %) item]))
                (into [[:span.name {:class (grid-row 3 4)} "Assembler"]])))

       ~@(when (contains? facilities "Chemical Facility")
           (->> chemical-plants
                (map (fn [item]
                       [preferred-building-option [4 5] "chemical" chemical-val #(reset! chemical %) item]))
                (into [[:span.name {:class (grid-row 4 5)} "Chemical Facility"]])))]]))

(defn production-tree-header []
  [:div.solver-header.node-header
   [:div "Buildings × Recipe"]
   [:div "Proliferator"]
   [:div "Belts"]
   [:div "Throughput"]])

(defclass depth-class [x]
  {:--depth x})

(defn depth-attrs [depth]
  (if (> depth 0)
    {:class (depth-class depth)}
    {}))

(defn item-icon [item]
  [:span.item.icon {:data-icon (str "item." (:id item))
                    :title (:name item)}])

(defn production-tree-leaf-node [depth tree]
  [:div.node.solve (depth-attrs depth)
   [:div.node-header
    [:div.meta
     [:span.item.named
      [item-icon tree]
      [:span.name (:name tree)]]]
    [:div.proliferator]
    [:div.logistics
     [:span.belt [:span.factor "1." [:span.repeat "6"]] "×"]]
    [:ul.products
     [:li.throughput.is-ingredient
      [:span.perMinute "600"]
      "×"
      [item-icon tree]
      [:span.timeScale "per minute"]]]]])

(declare production-tree-node)

(defn proliferator-node-control []
  [:div.proliferator
   [:div.icon {:data-icon "ui.inc-0" :data-count "" :data-inc "none" :title "None"}
    [:select.count
     [:option {:value "none"} "none"]
     [:option {:value "speedup"} "+100% speed"]
     [:option {:value "extra"} "+25% extra"]]]])

(defn production-tree-interior-node [depth tree]
  `[:details.node.solve ~(depth-attrs depth)
    [:summary
     [:div.node-header
      [:div.meta
       [:span {:title (str "10" "×&nbsp;" (:facility tree))}
        [:span.factor "10"] "×"]
       ~[:span.recipe
         [item-icon tree]
         [:span.name (:name tree)]]]
      ~[proliferator-node-control]
      [:div.logistics
       [:span.belt [:span.factor "1." [:span.repeat "6"]] "×"]]
      [:ul.products
       [:li.throughput.is-ingredient
        [:span.perMinute "600"]
        "×"
        ~[item-icon tree]
        [:span.timeScale "per minute"]]]]]
    ~@(for [item (:items tree)]
        [production-tree-node 1 item])])

(defn raw-resource? [node]
  (empty? (:items node)))

(defn production-tree-node [depth tree]
  (if (raw-resource? tree)
    [production-tree-leaf-node (inc depth) tree]
    [production-tree-interior-node (inc depth) tree]))

(defn production-tree-summary [raw-resources]
  [:details.node.solve
   [:summary
    [:div.node-header
     [:div.meta
      [:span.recipe
       [:span.tech.icon {:data-icon "tech.1102"}]
       [:span.name "Raw Resources Summary"]]]]]
   [:div.solver.has-proliferators
    [:div.solver-header.node-header
     [:div "Buildings × Recipe"]
     [:div]
     [:div "Belts"]
     [:div "Throughput"]]
    (for [resource raw-resources]
      ^{:key (:id resource)} [production-tree-leaf-node 0 resource])]])

(defn production-tree [summary tree]
  [:div.solver.has-proliferators
   [production-tree-summary (:raw-resources summary)]
   [production-tree-header]
   [production-tree-node 0 tree]])
