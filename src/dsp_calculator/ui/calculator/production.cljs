(ns dsp-calculator.ui.calculator.production
  (:require [spade.core :refer [defclass]]
            [com.gfredericks.exact :as e]
            [dsp-calculator.rational :as r]
            [dsp-calculator.ui.base :refer [time-label]]))

(defclass depth-class [x]
  {:--depth x})

(defn depth-attrs [depth]
  (if (> depth 0)
    {:class (depth-class depth)}
    {}))

(defn item-icon [item]
  [:span.item.icon {:data-icon (str "item." (:id item))
                    :title (:name item)}])

(def duration
  {"second" (r/int 60)
   "minute" (r/int (* 60 60))})

(defn get-result-rate [recipe]
  (r/ratio (get-in recipe [:results (:id recipe)] 1)
           (or (get recipe :time-spend 60)
               60)))

(defn rational [r]
  (let [{:keys [w n d]} (r/decompose r)]
    [:math
     (when w [:mn (str w)])
     (when (and w n d)
       "-")
     (when (and n d)
       [:mfrac
        [:mn (str n)]
        [:mn (str d)]])]))

(defn production-tree-header []
  [:div.solver-header.node-header
   [:div "Buildings × Recipe"]
   [:div "Proliferator"]
   [:div "Belts"]
   [:div "Throughput"]])

(declare production-tree-node)

(defn proliferator-node-control [leaf-node?]
  [:div.proliferator
   (when (not leaf-node?)
     [:div.icon {:data-icon "ui.inc-0" :data-count "" :data-inc "none" :title "None"}
      [:select.count
       [:option {:value "none"} "none"]
       [:option {:value "speedup"} "+100% speed"]
       [:option {:value "extra"} "+25% extra"]]])])

(defn raw-resource? [node]
  (empty? (:items node)))

(defn alt-recipe-node? [n]
  (and (contains? n :selected-recipe)
       (contains? n :recipes)))

(defn node-content [context depth tree]
  (let [context @context
        facility-count (e/* (:ratio context)
                            (:count tree))
        duration (get duration (:timescale context))
        items-per (e/* facility-count
                       (get-result-rate tree)
                       duration)
        belt-rate (if (= "minute" (:timescale context))
                    (e/* (:belt-rate context)
                         (r/int 60))
                    (:belt-rate context))
        belts-per (e// items-per belt-rate)
        leaf-node? (raw-resource? tree)]
    [:div.node-header
     [:div.meta
      (when (not leaf-node?)
        [:span {:title (str (r/str facility-count)
                            "× "
                            (:facility tree))}
         [rational facility-count] "×"])
      [:span {:class (if leaf-node?
                       ["item" "named"]
                       ["recipe"])}
       [item-icon tree]
       [:span.name (:name tree)]]]
     [proliferator-node-control leaf-node?]
     [:div.logistics
      [:span.belt [:span.factor [rational belts-per]] "×"]]
     [:ul.products
      [:li.throughput.is-ingredient
       [:span.perMinute [rational items-per]]
       "×"
       [item-icon tree]
       [:span.timeScale (get time-label (:timescale context))]]]]))

(defn production-tree-leaf-node [context depth path tree]
  [:div.node.solve (depth-attrs depth)
   [node-content context depth tree]])

(defn production-tree-interior-node [context depth path tree]
  [:details.node.solve (depth-attrs depth)
   [:summary
    [node-content context depth tree]]
   (let [path (conj path :items)]
     (for [item (vals (:items tree))]
       ^{:key (:id item)} [production-tree-node
                           context
                           depth
                           (conj path (:id item))
                           item]))])

(defn production-tree-alt-node [context depth path tree]
  (let [recipe-path [:recipes (:selected-recipe tree)]
        current-recipe (get-in tree recipe-path)
        path (into path recipe-path)]
    [production-tree-interior-node context depth path current-recipe]))

(defn production-tree-node [context depth path tree]
  (cond
    (alt-recipe-node? tree) [production-tree-alt-node context (inc depth) path tree]
    (raw-resource? tree)   [production-tree-leaf-node context (inc depth) path tree]
    :else              [production-tree-interior-node context (inc depth) path tree]))

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
      ^{:key (:id resource)} [production-tree-leaf-node context 0 [] resource])]])

(defn production-tree [context summary tree]
  (let [tree @tree]
    (when (seq tree)
      [:div.solver.has-proliferators
       [production-tree-summary context (vals (:raw-resources @summary))]
       [production-tree-header]
       [production-tree-node context 0 [] tree]])))
