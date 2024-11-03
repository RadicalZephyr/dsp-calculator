(ns dsp-calculator.production
  (:require #?(:clj [clojure.java.io :as io])
            [clojure.tools.reader.edn :as edn]
            [com.gfredericks.exact :as e]
            [dsp-calculator.rational :as r]))

(def root-path "public/data/")

#?(:clj (defn read-edn-resource [filename]
          (with-open [r (java.io.PushbackReader.
                         (io/reader (io/file (io/resource root-path)
                                             (str filename ".edn"))))]
            (edn/read r))))

(defn find-item-by-id [items id]
  (get items id))

(defn find-item-by-name [items name]
  (->> (vals items)
       (filter #(= name (:name %)))
       first))

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

(defn list-recipeless-items [items items-zh recipes-by-output]
  (->> (keys items)
       (remove #(contains? recipes-by-output %))
       (sort)
       (mapv (fn [id] [id
                      (get-in items [id :name])
                      (get-in items-zh [id :name])]))))

(def raw-resources
  [[1000 1 "Water" "水"]
   [1001 1 "Iron Ore" "铁矿"]
   [1002 1 "Copper Ore" "铜矿"]
   [1004 1 "Titanium Ore" "钛石"]
   [1005 1 "Stone" "石矿"]
   [1006 1 "Coal" "煤矿"]
   [1007 1 "Crude Oil" "原油"]
   [1011 2 "Fire Ice" "可燃冰"]
   [1012 2 "Kimberlite Ore" "金伯利矿石"]
   [1013 2 "Fractal Silicon" "分形硅石"]
   [1014 2 "Grating Crystal" "光栅石"]
   [1015 2 "Stalagmite Crystal" "刺笋结晶"]
   [1016 2 "Unipolar Magnet" "单极磁石"]
   [1030 1 "Log" "木材"]
   [1031 1 "Plant Fuel" "植物燃料"]
   [1116 1 "Sulfuric Acid" "硫酸"]
   [1120 1 "Hydrogen" "氢"]
   [1121 1 "Deuterium" "重氢"]
   [1208 3 "Critical Photon" "临界光子"]
   [2207 3 "Accumulator (full)" "蓄电器（满）"] ;; TODO: write a recipe
                                            ;; for this and include
                                            ;; it in the EDN file.
   [5201 4 "Dark Fog Matrix" "存储单元"]
   [5202 4 "Silicon-based Neuron" "硅基神经元"]
   [5203 4 "Matter Recombinator" "物质重组器"]
   [5204 4 "Negentropy Singularity" "负熵奇点"]
   [5205 4 "Core Element" "虚粒子"]
   [5206 4 "Energy Shard" "能量碎片"]])

#?(:clj (defn pos->str [{:keys [page x y]}]
          (format "%d%d%02d" page y x)))

#?(:clj (defn item->recipe [idx [item-id row name-en name-zh]]
          (let [pos {:page 3, :x (inc idx), :y row}]
            {:id (+ 300 (- item-id 1000)),
             :name name-en
             :type "MINE",
             :facility "Miner",
             :time-spend 60,
             :grid-pos pos
             :items {},
             :results {item-id 1},
             :sid (pos->str pos)})))

(defn group-by-outputs [recipes]
  (persistent!
   (reduce
    (fn [ret r]
      (let [outs (keys (:results r))]
        (reduce (fn [ret k]
                  (assoc! ret k
                          (conj (get ret k []) r)))
                ret outs)))
    (transient {}) (vals recipes))))

(def excluded-recipes
  #{16 29 32 35 54 58 61 62 69 74 79 100 115 121})

(defn match-item-recipe [recipes [id coordinate]]
  (let [ids (->> (get recipes id)
                 (map :id)
                 (remove excluded-recipes)
                 (map #(str "recipe." %))
                 (into [(str "item." id)]))]
    [ids coordinate]))

#?(:clj (defn repl-load-edn []
          (def items (read-edn-resource "items_EN"))
          (def items-zh (read-edn-resource "items"))
          (def item-icons (read-edn-resource "item-icons"))
          (def tech (read-edn-resource "tech_EN"))
          (def recipes (read-edn-resource "recipes_EN"))
          (def recipes-by-output (group-by-outputs recipes))
          (def item-recipe-icons (mapv #(match-item-recipe recipes-by-output %)
                                       item-icons))))

#?(:clj (defn render-icon-css [[ids [x y]]]
          (format "%s{ background-position: %s%% %s%%; }"
                  (apply str (map #(format "[data-icon=\"%s\"] " %) ids))
                  x
                  y)))

#?(:clj (defn render-all-icon-recipe-css [item-recipe-icons]
          (with-open [w (io/writer
                         (io/file
                          (io/resource "public/css")
                          "icon-recipe.css"))]
            (binding [*out* w]
              (doseq [row (map #(render-icon-css %) item-recipe-icons)]
                (println row))))))

(def ^:dynamic *max-depth* 10)

(defn get-item-rate [recipe item-id]
  (r/ratio (get-in recipe [:items item-id] 1)
           (get recipe :time-spend 60)))

(defn get-result-rate [recipe item-id]
  (r/ratio (get-in recipe [:results item-id] 1)
           (get recipe :time-spend 60)))

(defn production-tree
  "Produce a production tree describing the ratios needed to produce a
  given recipe.

  Each node in the tree is a map representing a specific production
  stage for one item. Each map has these keys:

  - :id          - The item id of this node.
  - :name        - The item name of this node.
  - :count       - The ratio of this recipe needed to produce 1 of the root node.
  - :recipe      - The id of the recipe this node is using.
  - :time-spend  - The time (in game ticks) it takes to run this recipe once.
  - :facility    - The type of production facility this recipe is made in.
  - :alt-recipes - A map of alternate recipe trees to produce this
                   item keyed by id that could be used.
  - :results     - A map of the item ids of the outputs of this recipe to
                   the number produced in one cycle.
  - :items       - A map of item ids to a node for each item needed to
                   produce this recipe.

  Note, because this tree shows the full production chain, there can
  be multiple instances of any given product. Each individual stage
  for that item can use a different recipe."
  [items recipes recipes-by-output recipe-id]
  (letfn [(process-recipe [depth scale recipe item-id]
            (let [count (e// scale (get-result-rate recipe item-id))]
              {:id item-id
               :name (get-in items [item-id :name] "")
               :count count
               :recipe (:id recipe)
               :time-spend (:time-spend recipe)
               :facility (:facility recipe)
               :results (get recipe :results {})
               :items (let [depth (inc depth)]
                        (->> (:items recipe)
                             (keys)
                             (map #(r depth
                                      (e/* count (get-item-rate recipe %))
                                      %))
                             (map (juxt :id identity))
                             (into {})))}))
          (r [depth scale item-id]
            (if (< depth *max-depth*)
              ;; NOTE: we can't process all recipes at once by mapping
              ;; process-recipe across the recipes because we always
              ;; want to process at least one recipe to create the
              ;; leaf nodes for raw resources, even if there's no
              ;; explicit recipe present.
              (let [item-recipes (get recipes-by-output item-id)
                    first-recipe (first item-recipes)
                    alt-recipes (rest item-recipes)
                    node (process-recipe depth scale first-recipe item-id)
                    alt-recipes (->> alt-recipes
                                     (map #(process-recipe depth scale % item-id))
                                     (map (juxt :recipe identity))
                                     (into {}))]
                (assoc node :alt-recipes alt-recipes))
              {:error "max depth reached"}))]
    ;; Handle the first layer of recursion specially, because we want
    ;; the scale passed in to result in the `:count` being 1 for the
    ;; top-level recipe, we need to process it slightly differently
    ;; than we would when recursing.
    (let [depth 0
          item-recipe (get recipes recipe-id)

          ;; NOTE: Getting the first result is arbitrary since the
          ;; goal is to produce a recipe "count" of 1, as long as we
          ;; pick the same item-id both here and inside
          ;; process-recipe, the result will be correct.
          item-id (-> item-recipe
                      (get :results)
                      keys
                      first)]
      (process-recipe depth (get-result-rate item-recipe item-id) item-recipe item-id))))

(def default-summary
  {:facilities #{}
   :raw-resources {}})

(defn summarize
  "Recurse through a production-tree, collecting details on which
  production facilities are used and totaling up the raw resources
  needed."
  [tree]
  (letfn [(process-leaf [resource-summary node]
            (if resource-summary
              (update resource-summary :count e/+ (:count node))
              (select-keys node [:id :name :count])))
          (r [summary tree]
            (let [summary (if-let [facility (:facility tree)]
                            (update summary :facilities conj facility)
                            summary)]
              (if-let [items (vals (:items tree))]
                ;; If has input items, recurse
                (reduce r summary items)
                (update-in summary [:raw-resources (:id tree)] process-leaf tree))))]
    (let [summary default-summary]
      (r summary tree))))
