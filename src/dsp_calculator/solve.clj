(ns dsp-calculator.solve
  (:require [clojure.java.io :as io]
            [clojure.tools.reader.edn :as edn]))

(def root-path "public/data/")

(defn read-edn-resource [filename]
  (with-open [r (java.io.PushbackReader.
                 (io/reader (io/file (io/resource root-path)
                                     (str filename ".edn"))))]
    (edn/read r)))

(defn find-item-by-id [id items]
  (first (filter #(= id (:id %)) items)))

(defn find-item-by-name [name items]
  (first (filter #(= name (:name %)) items)))

(def production-facilities
  ["Assembling Machine Mk.I"
   "Assembling Machine Mk.II"
   "Assembling Machine Mk.III"
   "Re-composing Assembler"
   "Arc Smelter"
   "Plane Smelter"
   "Negentropy Smelter"
   "Oil Refinery"
   "Chemical Plant"
   "Quantum Chemical Plant"
   "Fractionator"
   "Miniature Particle Collider"])

(def raw-resources
  [[1001 "Iron Ore"]
   [1002 "Copper Ore"]
   [1004 "Titanium Ore"]
   [1005 "Stone"]
   [1006 "Coal"]
   [1030 "Log"]
   [1031 "Plant Fuel"]
   [1011 "Fire Ice"]
   [1012 "Kimberlite Ore"]
   [1013 "Fractal Silicon"]
   [1014 "Grating Crystal"]
   [1015 "Stalagmite Crystal"]
   [1016 "Unipolar Magnet"]
   [1000 "Water"]
   [1007 "Crude Oil"]
   [1208 "Critical Photon"]
   [5201 "Dark Fog Matrix"]
   [5202 "Silicon-based Neuron"]
   [5203 "Matter Recombinator"]
   [5204 "Negentropy Singularity"]
   [5205 "Core Element"]
   [5206 "Energy Shard"]
   [2207 "Accumulator (full)"] ;; TODO: write a recipe for this and
                               ;; include it in the EDN file.
   ])

(defn group-by-outputs [recipes]
  (persistent!
   (reduce
    (fn [ret r]
      (let [outs (map :id (:results r))]
        (reduce (fn [ret k]
                  (assoc! ret k
                          (conj (get ret k []) r)))
                ret outs)))
    (transient {}) recipes)))

(defn repl-load-edn []
  (def items (read-edn-resource "items_EN"))
  (def tech (read-edn-resource "tech_EN"))
  (def recipes (read-edn-resource "recipes_EN"))
  (def recipes-by-output (group-by-outputs recipes)))

(def ^:dynamic *max-depth* 10)

(def production-summary
  {:alt-recipes #{}
   :facilities #{}
   :raw-resources {}})

(defn get-item-count [recipe item-id]
  (get-in recipe [:items item-id] 1))

(defn production-tree
  "Produce a production tree describing the ratios needed to produce a
  given recipe.

  Each node in the tree is a map representing a specific production
  stage for one item. Each map has these keys:

  - :id          - The item id of this node.
  - :recipe      - The id of the recipe this node is using.
  - :alt-recipes - A vector of alternate recipe ids that could be used.
  - :items       - A map of item ids to a node for each item needed to
                   produce this recipe.

  Note, because this tree shows the full production chain, there can
  be multiple instances of any given product. Each individual stage
  for that item can use a different recipe."
  [recipes item-id]
  (letfn [(r [recipes summary depth scale item-id]
            (if (< depth *max-depth*)
              (let [item-recipes (get recipes item-id)
                    first-recipe (first item-recipes)
                    output-count (get-item-count first-recipe item-id)
                    alt-recipes (map :id (rest item-recipes))]
                (when (seq alt-recipes)
                  (swap! summary update :alt-recipes
                         conj (vec (cons (:id first-recipe) alt-recipes))))
                (when (seq item-recipes)
                  (swap! summary update :facilities
                         into (map :made-from-string item-recipes)))
                {:id item-id
                 :count (* scale output-count)
                 :recipe (:id first-recipe)
                 :facility (:made-from-string first-recipe)
                 :alt-recipes (vec alt-recipes)
                 :items (let [depth (inc depth)]
                          (->> (:items first-recipe)
                               (keys)
                               (map #(r recipes
                                        summary
                                        depth
                                        (* scale (get-item-count first-recipe %))
                                        %))
                               (map (juxt :id identity))
                               (into {})))})
              {:error "max depth reached"}))]
    (let [summary (atom production-summary)
          tree (r recipes summary 0 1 item-id)]
      [@summary tree])))
