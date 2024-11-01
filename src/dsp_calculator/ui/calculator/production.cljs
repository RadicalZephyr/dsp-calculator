(ns dsp-calculator.ui.calculator.production
  (:require [spade.core :refer [defclass]]))

(defclass depth-class [x]
  {:--depth x})

(defn depth-attrs [depth]
  (if (> depth 0)
    {:class (depth-class depth)}
    {}))

(defn item-icon [item]
  [:span.item.icon {:data-icon (str "item." (:id item))
                    :title (:name item)}])

(defn production-tree-header []
  [:div.solver-header.node-header
   [:div "Buildings × Recipe"]
   [:div "Proliferator"]
   [:div "Belts"]
   [:div "Throughput"]])

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
   [production-tree-summary (:raw-resources @summary)]
   [production-tree-header]
   [production-tree-node 0 @tree]])
