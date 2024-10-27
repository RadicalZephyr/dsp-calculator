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
       [:span {:title "10× Smelting Facility"}
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

(defn production-tree [tree]
  [:div.solver
   [production-tree-node 0 tree]])
