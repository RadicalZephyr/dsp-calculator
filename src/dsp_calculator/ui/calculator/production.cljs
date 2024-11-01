(ns dsp-calculator.ui.calculator.production
  (:require [spade.core :refer [defclass]]
            [com.gfredericks.exact :as e]))

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

(defn production-tree-leaf-node [context depth tree]
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

(defn render-rational [r]
  (if (e/integer? r)
    (e/integer->string r)
    [:span.fraction (str (e/numerator r)
                         "/"
                         (e/denominator r))]))

(defn production-tree-interior-node [context depth tree]
  `[:details.node.solve ~(depth-attrs depth)
    [:summary
     [:div.node-header
      [:div.meta
       ~(let [facility-count (render-rational
                              (e/* (:ratio context)
                                   (:count tree)))]
          [:span {:title (str facility-count
                              "× "
                              (:facility tree))}
           [:span.factor facility-count] "×"])
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
    ~@(for [item (vals (:items tree))]
        [production-tree-node context depth item])])

(defn raw-resource? [node]
  (empty? (:items node)))

(defn production-tree-node [context depth tree]
  (if (raw-resource? tree)
    [production-tree-leaf-node context (inc depth) tree]
    [production-tree-interior-node context (inc depth) tree]))

(defn production-tree-summary [context raw-resources]
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
      ^{:key (:id resource)} [production-tree-leaf-node context 0 resource])]])

(defn production-tree [context summary tree]
  (let [context @context]
    [:div.solver.has-proliferators
     [production-tree-summary context (vals (:raw-resources @summary))]
     [production-tree-header]
     [production-tree-node context 0 @tree]]))
