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

(def minable-resources
  [{:item-id 1000,
    :row 1,
    :facility "Water Pump",
    :name-en "Water",
    :name-zh "水"}
   {:item-id 1001,
    :row 1,
    :facility "Mining Facility",
    :name-en "Iron Ore",
    :name-zh "铁矿"}
   {:item-id 1002,
    :row 1,
    :facility "Mining Facility",
    :name-en "Copper Ore",
    :name-zh "铜矿"}
   {:item-id 1004,
    :row 1,
    :facility "Mining Facility",
    :name-en "Titanium Ore",
    :name-zh "钛石"}
   {:item-id 1005,
    :row 1,
    :facility "Mining Facility",
    :name-en "Stone",
    :name-zh "石矿"}
   {:item-id 1006,
    :row 1,
    :facility "Mining Facility",
    :name-en "Coal",
    :name-zh "煤矿"}
   {:item-id 1007,
    :row 1,
    :facility "Oil Extractor",
    :name-en "Crude Oil",
    :name-zh "原油"}
   {:item-id 1011,
    :row 2,
    :facility "Mining Facility",
    :name-en "Fire Ice",
    :name-zh "可燃冰"}
   {:item-id 1012,
    :row 2,
    :facility "Mining Facility",
    :name-en "Kimberlite Ore",
    :name-zh "金伯利矿石"}
   {:item-id 1013,
    :row 2,
    :facility "Mining Facility",
    :name-en "Fractal Silicon",
    :name-zh "分形硅石"}
   {:item-id 1014,
    :row 2,
    :facility "Mining Facility",
    :name-en "Grating Crystal",
    :name-zh "光栅石"}
   {:item-id 1015,
    :row 2,
    :facility "Mining Facility",
    :name-en "Stalagmite Crystal",
    :name-zh "刺笋结晶"}
   {:item-id 1016,
    :row 2,
    :facility "Mining Facility",
    :name-en "Unipolar Magnet",
    :name-zh "单极磁石"}
   {:item-id 1116,
    :row 1,
    :facility "Water Pump",
    :name-en "Sulfuric Acid",
    :name-zh "硫酸"}
   {:item-id 1120,
    :row 1,
    :facility "Orbital Collector",
    :name-en "Hydrogen",
    :name-zh "氢"}
   {:item-id 1121,
    :row 1,
    :facility "Orbital Collector",
    :name-en "Deuterium",
    :name-zh "重氢"}])

(def special-recipes
  ;; Graviton Spheres are consumed by Ray Receiver's at a rate of
  ;; 0.1/min

  ;; Critical Photon's are produced by Ray Receiver's at highly variable
  ;; rates. It probably depends on several factors:
  ;;  - Ray receiving efficiency research level
  ;;  - Building efficiency (time spent able to receive)
  ;;  - Use of graviton spheres (x2 plus longer uptime)

  ;; The upshot of this is that I think for now it's probably beyond the
  ;; scope of my undertaking to figure out how to calculate that rate
  ;; and suggest a number Ray Receiver's to use.
  ;;
  ;; I might be able to find a spreadsheet someone has created with the
  ;; data needed, but I don't think I want to dive into actually
  ;; collecting that data, especially since that is so variable.
  [{:item-id 1208,
    :row 3,
    :count-per-min nil,
    :facility "Ray Receiver",
    :name-en "Critical Photon",
    :name-zh "临界光子"}

   ;; When fully supplied with power, these appear to be made at a
   ;; rate of 6/min/exchanger.
   {:item-id 2207,
    :row 3,
    :count-per-min 6,
    :facility "Energy Exchanger",
    :name-en "Accumulator (full)",
    :name-zh "蓄电器（满）"}])

(def non-minable-resources
  [{:item-id 1030,
    :row 1,
    :name-en "Log",
    :name-zh "木材"}
   {:item-id 1031,
    :row 1,
    :name-en "Plant Fuel",
    :name-zh "植物燃料"}
   {:item-id 5201,
    :row 4,
    :name-en "Dark Fog Matrix",
    :name-zh "存储单元"}
   {:item-id 5202,
    :row 4,
    :name-en "Silicon-based Neuron",
    :name-zh "硅基神经元"}
   {:item-id 5203,
    :row 4,
    :name-en "Matter Recombinator",
    :name-zh "物质重组器"}
   {:item-id 5204,
    :row 4,
    :name-en "Negentropy Singularity",
    :name-zh "负熵奇点"}
   {:item-id 5205,
    :row 4,
    :name-en "Core Element",
    :name-zh "虚粒子"}
   {:item-id 5206,
    :row 4,
    :name-en "Energy Shard",
    :name-zh "能量碎片"}])

(def facility-en->zh
  {"Mining Facility" "采矿设备"
   "Ray Receiver" "射线接收站"
   "Orbital Collector" "轨道采集器"
   "Water Pump" "抽水站"
   "Oil Extractor" "原油萃取站"})

(defn pos->str [{:keys [page x y]}]
  (str page y
       (if (> x 9)
         x
         (str "0" x))))

(defn per-min->time-spend [count-per-min]
  (r/ratio (* 60 60)
           count-per-min))

(defn item->recipe [idx {:keys [item-id row count-per-min facility name-en name-zh]}]
  (let [pos {:page 3, :x (inc idx), :y row}]
    {:id (+ 300 (- item-id 1000))
     :name name-zh
     :type "MINE"
     :facility (facility-en->zh facility)
     :time-spend (per-min->time-spend count-per-min)
     :grid-pos pos
     :items {}
     :results {item-id 1}
     :sid (pos->str pos)}))

(defn render-minable-resource-recipes [resources]
  (->> resources
       (group-by second)
       vals
       (mapcat #(map-indexed item->recipe %))
       vec))

(def needed-item-names
  ["Ray Receiver"
   "Orbital Collector"
   "Water Pump"
   "Oil Extractor"])

(defn lookup-item-translation [items-from items-to sources]
  (->> sources
       (map #(:id (find-item-by-name items-from %)))
       (map (juxt (comp :name items-from)
                  (comp :name items-to)))
       (into {})))

(defn create-multi-recipe-map [items recipes-by-output]
  (->> recipes-by-output
       (filter #(> (count (second %)) 1))
       (map (fn [[id rs]]
              [[id (get-in items [id :name])]
               (->> rs
                    (map (juxt :id :name))
                    (into (sorted-map)))]))
       (into (sorted-map))))

(def multiple-recipes
  {[1109 "Energetic Graphite"] {17 "Energetic Graphite",
                                58 "X-ray Cracking"},
   [1112 "Diamond"] {60 "Diamond",
                     61 "Diamond (advanced)"},
   [1113 "Crystal Silicon"] {37 "Crystal Silicon",
                             62 "Crystal Silicon (advanced)"},
   [1114 "Refined Oil"] {16 "Plasma Refining",
                         121 "Reformed Refinement"},
   [1117 "Organic Crystal"] {25 "Organic Crystal",
                             54 "Organic Crystal (original)"},
   [1120 "Hydrogen"] {16 "Plasma Refining",
                      32 "Graphene (advanced)",
                      58 "X-ray Cracking",
                      74 "Mass-energy Storage"},
   [1121 "Deuterium"] {40 "Deuterium",
                       115 "Deuterium Fractionation"},
   [1123 "Graphene"] {31 "Graphene",
                      32 "Graphene (advanced)"},
   [1124 "Carbon Nanotube"] {33 "Carbon Nanotube",
                             35 "Carbon Nanotube (advanced)"},
   [1126 "Casimir Crystal"] {28 "Casimir Crystal",
                             29 "Casimir Crystal (advanced)"}
   [1206 "Particle Container"] {99 "Particle Container",
                                100 "Particle Container (advanced)"},
   [1210 "Space Warper"] {78 "Space Warper",
                          79 "Space Warper (advanced)"},
   [1404 "Photon Combiner"] {68 "Photon Combiner",
                             69 "Photon Combiner (advanced)"}})

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
