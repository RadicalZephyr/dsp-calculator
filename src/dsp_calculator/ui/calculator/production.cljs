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

(def duration
  {"second" (e/native->integer 60)
   "minute" (e/native->integer (* 60 60))})

(def time-label
  {"second" "per second"
   "minute" "per minute"})

(defn get-result-rate [recipe]
  (e// (e/native->integer
        (get-in recipe [:results (:id recipe)] 1))
       (e/native->integer
        (or (get recipe :time-spend 60)
            60))))

(defn render-rational [r]
  (if (e/integer? r)
    (e/integer->string r)
    (str (e/numerator r)
         "/"
         (e/denominator r))))

(defn production-tree-header []
  [:div.solver-header.node-header
   [:div "Buildings × Recipe"]
   [:div "Proliferator"]
   [:div "Belts"]
   [:div "Throughput"]])

(defn production-tree-leaf-node [context depth tree]
  [:div.node.solve (depth-attrs depth)
   (let [facility-count (e/* (:ratio context)
                             (:count tree))
         facility-str (render-rational facility-count)
         duration (get duration (:timescale context))
         items-per (e/* facility-count
                        (get-result-rate tree)
                        duration)
         belt-rate (if (= "minute" (:timescale context))
                     (e/* (e/native->integer 60) (:belt-rate context))
                     (:belt-rate context))
         belts-per (e// items-per belt-rate)]
     [:div.node-header
      [:div.meta
       [:span.item.named
        [item-icon tree]
        [:span.name (:name tree)]]]
      [proliferator-node-control false]
      [:div.logistics
       [:span.belt [:span.factor (render-rational belts-per)] "×"]]
      [:ul.products
       [:li.throughput.is-ingredient
        [:span.perMinute (render-rational items-per)]
        "×"
        [item-icon tree]
        [:span.timeScale (get time-label (:timescale context))]]]])])

(declare production-tree-node)

(defn proliferator-node-control [render?]
  [:div.proliferator
   (when render?
     [:div.icon {:data-icon "ui.inc-0" :data-count "" :data-inc "none" :title "None"}
      [:select.count
       [:option {:value "none"} "none"]
       [:option {:value "speedup"} "+100% speed"]
       [:option {:value "extra"} "+25% extra"]]])])

(defn production-tree-interior-node [context depth tree]
  [:details.node.solve (depth-attrs depth)
   [:summary
    (let [facility-count (e/* (:ratio context)
                              (:count tree))
          facility-str (render-rational facility-count)
          duration (get duration (:timescale context))
          items-per (e/* facility-count
                         (get-result-rate tree)
                         duration)
          belt-rate (if (= "minute" (:timescale context))
                      (e/* (e/native->integer 60) (:belt-rate context))
                      (:belt-rate context))
          belts-per (e// items-per belt-rate)]
      [:div.node-header
       [:div.meta
        [:span.fraction {:title (str facility-str
                                     "× "
                                     (:facility tree))}
         [:span.factor facility-str] "×"]
        [:span.recipe
         [item-icon tree]
         [:span.name (:name tree)]]]
       [proliferator-node-control true]
       [:div.logistics
        [:span.belt [:span.factor (render-rational belts-per)] "×"]]
       [:ul.products
        [:li.throughput.is-ingredient
         [:span.perMinute (render-rational items-per)]
         "×"
         [item-icon tree]
         [:span.timeScale (get time-label (:timescale context))]]]])]
   (for [item (vals (:items tree))]
     ^{:key (:id item)} [production-tree-node context depth item])])

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
