(ns dsp-calculator.ui.calculator
  (:require [spade.core :refer [defclass]]
            [reagent.core :as reagent]
            [com.gfredericks.exact :as e]))

(defn ratio [x y]
  (e// (e/native->integer x)
       (e/native->integer y)))

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

(def mining-productivity-techs
  [{:id 3600
    :name "No Vein Utilization"
    :speed (ratio 1 1)}
   {:id 3601
    :name "Vein Utilization I"
    :speed (ratio 11 10)}
   {:id 3602
    :name "Vein Utilization II"
    :speed (ratio 12 10)}
   {:id 3603
    :name "Vein Utilization III"
    :speed (ratio 13 10)}
   {:id 3604
    :name "Vein Utilization IV"
    :speed (ratio 14 10)}
   {:id 3605
    :name "Vein Utilization V"
    :speed (ratio 15 10)}])

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

(declare item-id tech-id)

(def preferred-building-customizations
  {"belt" (fn [building]
            {:id-fn item-id
             :title-suffix (str " — Transport Speed: "
                                (:speed building)
                                " items per minute")
             :data-key :data-per
             :data-val (str (:speed building))})
   "mining-productivity" (fn [building]
                           {:id-fn tech-id
                            :title-suffix (str " — Mining Efficiency: "
                                               (:speed building))
                            :data-key :data-count
                            :data-val (let [hundred (e/native->integer 100)]
                                        (str "+" (e/- (e/* hundred
                                                           (:speed building))
                                                      hundred) "%"))})
   :else (fn [building]
           {:id-fn item-id
            :title-suffix (str " — Production Speed: "
                               (:count building))
            :data-key :data-count
            :data-val (str (:count building) "×")})})

(defn get-pb-customizations [type building]
  (let [k (if (contains? preferred-building-customizations type)
            type
            :else)]
    ((get preferred-building-customizations k) building)))

(defclass grid-row [x y]
  {:grid-row (str x " / " y)})

(defn item-id [item]
  (str "item." (:id item)))

(defn tech-id [tech]
  (str "tech." (:id tech)))

(defn preferred-building-option [[x y] type selected change building]
  (let [{:keys [id-fn
                title-suffix
                data-key
                data-val]} (get-pb-customizations type building)]
    [:label {:class [(grid-row x y)
                     (when (= (:id selected) (:id building)) "is-selected")]}
     [:input {:type "radio"
              :name type
              :value (str (:id building))
              :selected (= (:id selected) (:id building))
              :on-change #(change building)
              :title (str (:name building)
                          title-suffix)}]
     [:span.item.icon {:title (:name building)
                       :data-icon (id-fn building)
                       data-key data-val
                       :lang "en-US"}]]))

(defn preferred-building-row [data [x y :as row] type selected ra label]
  (->> data
       (map (fn [item]
              [preferred-building-option row type selected #(reset! ra %) item]))
       (into [[:span.name {:class (grid-row x y)} label]])))

(defn preferred-buildings [facilities belt mining-productivity smelter assembler chemical]
  (let [belt-val @belt
        mining-productivity-val @mining-productivity
        smelter-val @smelter
        assembler-val @assembler
        chemical-val @chemical]
    [:details.preferred.preferred-buildings {:open true}
     [:summary "Preferred Buildings"]
     (let [row (atom 0)]
       `[:div.fields
         ~@(preferred-building-row conveyor-belts
                                   [@row (inc @row)]
                                   "belt"
                                   belt-val
                                   belt
                                   "Logistics")

         ~@(when (contains? facilities "Miner")
             (swap! row + 2)
             (preferred-building-row mining-productivity-techs
                                     [@row (inc @row)]
                                     "mining-productivity"
                                     mining-productivity-val
                                     mining-productivity
                                     "Mining Productivity"))

         ~@(when (contains? facilities "Smelting Facility")
             (swap! row + 2)
             (preferred-building-row smelters
                                     [@row (inc @row)]
                                     "smelter"
                                     smelter-val
                                     smelter
                                     "Smelting Facility"))

         ~@(when (contains? facilities "Assembler")
             (swap! row + 2)
             (preferred-building-row assemblers
                                     [@row (inc @row)]
                                     "assembler"
                                     assembler-val
                                     assembler
                                     "Assembler"))

         ~@(when (contains? facilities "Chemical Facility")
             (swap! row + 2)
             (preferred-building-row chemical-plants
                                     [@row (inc @row)]
                                     "chemical"
                                     chemical-val
                                     chemical
                                     "Chemical Facility"))])]))

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
       [:span {:title (str "10" "× " (:facility tree))}
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
