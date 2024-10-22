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

(defn group-by-outputs [recipes]
  (persistent!
   (reduce
    (fn [ret r]
      (let [outs (:results r)]
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

(defn production-tree
  "Produce a production tree describing the ratios needed to produce a
  given recipe.

  Each node in the tree is a map representing a specific production
  stage for one item. Each map has these keys:

  - :id - The item id of this node.
  - :recipe - The id of the recipe this node is using.
  - :alt-recipes - A vector of alternate recipe ids that could be used.

  Note, because this tree shows the full production chain, there can
  be multiple instances of any given product. Each individual stage
  for that item can use a different recipe."
  [recipe]
  )
