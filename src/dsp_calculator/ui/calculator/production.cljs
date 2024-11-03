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

(defn reduce-rational [r]
  (if (e/integer? r)
    {:w (e/integer->string r)}
    (let [n (e/numerator r)
          d (e/denominator r)]
      (if (> d n)
        {:n n :d d}
        (let [whole (quot n d)
              r (rem n d)]
          {:w (when (not (= 0 whole))
                whole)
           :n r
           :d d})))))

(defn render-rational [r]
  (let [{:keys [w n d]} (reduce-rational r)]
    (str (when w w)
         (when (and w n d)
           "-")
         (when (and n d)
           (str n "/" d)))))

(defn rational [r]
  (let [{:keys [w n d]} (reduce-rational r)]
    [:span
     (when w (str w))
     (when (and w n d)
       "-")
     (when (and n d)
       [:span.fraction (str n "/" d)])]))

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

(defn node-content [context depth tree]
  (let [facility-count (e/* (:ratio context)
                            (:count tree))
        duration (get duration (:timescale context))
        items-per (e/* facility-count
                       (get-result-rate tree)
                       duration)
        belt-rate (if (= "minute" (:timescale context))
                    (:belt-rate context)
                    (e// (:belt-rate context)
                         (e/native->integer 60)))
        belts-per (e// items-per belt-rate)
        leaf-node? (raw-resource? tree)]
    [:div.node-header
     [:div.meta
      (when (not leaf-node?)
        [:span {:title (str (render-rational facility-count)
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

(defn production-tree-leaf-node [context depth tree]
  [:div.node.solve (depth-attrs depth)
   [node-content context depth tree]])

(defn production-tree-interior-node [context depth tree]
  [:details.node.solve (depth-attrs depth)
   [:summary
    [node-content context depth tree]]
   (for [item (vals (:items tree))]
     ^{:key (:id item)} [production-tree-node context depth item])])

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
  (let [context @context
        tree @tree]
    (when (seq tree)
      [:div.solver.has-proliferators
       [production-tree-summary context (vals (:raw-resources @summary))]
       [production-tree-header]
       [production-tree-node context 0 tree]])))
