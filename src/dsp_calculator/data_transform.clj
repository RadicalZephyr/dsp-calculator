(ns dsp-calculator.data-transform
  (:require
    [clojure.data.json :as json]
    [clojure.java.io :as io]
    [clojure.pprint :as pp]
    [clojure.set :as set]
    [clojure.string :as str]
    [clojure.tools.reader.edn :as edn]
    [clojure.walk :as walk]
    [camel-snake-kebab.core :as csk]))

(defn type-map? [item]
  (and (map? item)
       (= #{":type" "value"} (set (keys item)))))

(defn simplify [{type ":type" value "value" :as item}]
  (if (= type "BigInt")
    (edn/read-string value)
    item))


(defn ascii? [s]
  (if (string? s)
    (re-matches #"\p{ASCII}+" s)))

(defn kebab-keywordify [k]
  (when (ascii? k)
    [k (keyword (csk/->kebab-case k))]))

(defn assoc-desc-field [m {:strs [key value position]}]
  (if (some? key)
    (assoc m key {"value" value "position" position})
    m))

(defn merge-desc-fields [m]
  (if (and (contains? m "descFields")
           (contains? m "_descFields"))
    (let [num-d-fields (get m "descFields")
          map-d-fields (get m "_descFields")
          desc-fields (->> (map #(assoc %1 "position" %2)
                                map-d-fields
                                num-d-fields)
                           (reduce assoc-desc-field (sorted-map)))]
      (-> m
          (dissoc "descFields" "_descFields")
          (assoc "descFields" desc-fields)))
    m))

(defn recipe-map? [m]
  (and (contains? m "items")
       (contains? m "itemCounts")
       (contains? m "results")
       (contains? m "resultCounts")))

(defn parse-grid-index [grid-index]
  (let [s (str grid-index)
        grid-pos-s [(subs s 0 1) (subs s 1 2) (subs s 2)]
        [page y x] (map #(Integer/parseInt %) grid-pos-s)]
    {"page" page "x" x "y" y}))

(defn merge-split-recipe-fields [m]
  (if (recipe-map? m)
    (let [items (get m "items")
          item-counts (get m "itemCounts")
          new-items (->> item-counts
                         (map vector items)
                         (into {}))
          results (get m "results")
          result-counts (get m "resultCounts")
          new-results (->> result-counts
                           (map vector results)
                           (into {}))
          grid-pos (parse-grid-index (get m "gridIndex"))]
      (-> m
          (dissoc "_madeFromString" "gridIndex"
                  "items" "itemCounts"
                  "results" "resultCounts")
          (assoc "items" new-items
                 "results" new-results
                 "gridPos" grid-pos
                 "facility" (get m "_madeFromString"))))
    m))

(defn cljify-map [m]
  (let [ascii-keys (map ascii? (keys m))]
    (when (and (some identity ascii-keys)
               (not (every? identity ascii-keys)))
      (println "found a map with mixed ASCII and non-ASCII keys")
      (prn m)))
  (let [m (-> m
              merge-desc-fields
              merge-split-recipe-fields)
        smap (into {} (map kebab-keywordify (keys m)))]
    (into (sorted-map) (set/rename-keys m smap))))

(defn transform [item]
  (cond (type-map? item) (simplify item)
        (map? item) (cljify-map item)
        :else item))

(def root-path "public/data/")

(defn read-json-resource [filename]
  (with-open [reader (io/reader
                      (io/file
                       (io/resource root-path)
                       (str  filename ".json")))]
    (json/read reader)))

(defn resource-writer [filename]
  (io/writer
   (io/file
    (io/resource root-path)
    filename)))

(defn repl-load-json []
  (def meta-json (read-json-resource "meta"))
  (def locale-json (read-json-resource "locale"))
  (def item-json (read-json-resource "items"))
  (def recipe-json (read-json-resource "recipes"))
  (def tech-json (read-json-resource "tech")))

(declare mining-recipes)

(def files ["meta" "items" "recipes" "tech"])

(defn main []
  (let [locales (read-json-resource "locale")
        en-locale (first (filter #(str/starts-with? (get % "locale") "en-") locales))
        en-strings (get en-locale "strings")]
    (doseq [filename files]
      (prn filename)
      (let [raw-json (read-json-resource filename)
            json (walk/prewalk transform raw-json)
            json (if (= "recipes" filename)
                   (into json mining-recipes)
                   json)
            json (if (not (= "meta" filename))
                   (->> json
                        (map (juxt :id identity))
                        (into (sorted-map)))
                   json)
            json-en (walk/prewalk-replace en-strings json)]
        (with-open [writer (resource-writer (str filename ".edn"))
                    writer-en (resource-writer (str filename "_EN.edn"))]
          (pp/pprint json writer)
          (pp/pprint json-en writer-en))))
    (prn "locale")
    (let [filename "locale"
          raw-json (read-json-resource filename)
          json (vec (map transform raw-json))]
      (with-open [writer (resource-writer (str filename ".edn"))]
        (pp/pprint json writer)))))

(def mining-recipes
  [{:facility "Water Pump",
    :time-spend 60,
    :name "Water",
    :type "MINE",
    :grid-pos {:page 3, :x 1, :y 1},
    :id 300,
    :items {},
    :sid "3101",
    :results {1000 1}}
   {:facility "Miner",
    :time-spend 60,
    :name "Iron Ore",
    :type "MINE",
    :grid-pos {:page 3, :x 2, :y 1},
    :id 301,
    :items {},
    :sid "3102",
    :results {1001 1}}
   {:facility "Miner",
    :time-spend 60,
    :name "Copper Ore",
    :type "MINE",
    :grid-pos {:page 3, :x 3, :y 1},
    :id 302,
    :items {},
    :sid "3103",
    :results {1002 1}}
   {:facility "Miner",
    :time-spend 60,
    :name "Titanium Ore",
    :type "MINE",
    :grid-pos {:page 3, :x 4, :y 1},
    :id 304,
    :items {},
    :sid "3104",
    :results {1004 1}}
   {:facility "Miner",
    :time-spend 60,
    :name "Stone",
    :type "MINE",
    :grid-pos {:page 3, :x 5, :y 1},
    :id 305,
    :items {},
    :sid "3105",
    :results {1005 1}}
   {:facility "Miner",
    :time-spend 60,
    :name "Coal",
    :type "MINE",
    :grid-pos {:page 3, :x 6, :y 1},
    :id 306,
    :items {},
    :sid "3106",
    :results {1006 1}}
   {:facility "Oil Pump",
    :time-spend 60,
    :name "Crude Oil",
    :type "MINE",
    :grid-pos {:page 3, :x 7, :y 1},
    :id 307,
    :items {},
    :sid "3107",
    :results {1007 1}}
   {:facility "Miner",
    :time-spend 60,
    :name "Sulfuric Acid",
    :type "MINE",
    :grid-pos {:page 3, :x 10, :y 1},
    :id 416,
    :items {},
    :sid "3110",
    :results {1116 1}}
   {:facility "Orbital Collector",
    :time-spend 60,
    :name "Hydrogen",
    :type "MINE",
    :grid-pos {:page 3, :x 11, :y 1},
    :id 420,
    :items {},
    :sid "3111",
    :results {1120 1}}
   {:facility "Orbital Collector",
    :time-spend 60,
    :name "Deuterium",
    :type "MINE",
    :grid-pos {:page 3, :x 12, :y 1},
    :id 421,
    :items {},
    :sid "3112",
    :results {1121 1}}
   {:facility "Miner",
    :time-spend 60,
    :name "Fire Ice",
    :type "MINE",
    :grid-pos {:page 3, :x 1, :y 2},
    :id 311,
    :items {},
    :sid "3201",
    :results {1011 1}}
   {:facility "Miner",
    :time-spend 60,
    :name "Kimberlite Ore",
    :type "MINE",
    :grid-pos {:page 3, :x 2, :y 2},
    :id 312,
    :items {},
    :sid "3202",
    :results {1012 1}}
   {:facility "Miner",
    :time-spend 60,
    :name "Fractal Silicon",
    :type "MINE",
    :grid-pos {:page 3, :x 3, :y 2},
    :id 313,
    :items {},
    :sid "3203",
    :results {1013 1}}
   {:facility "Miner",
    :time-spend 60,
    :name "Grating Crystal",
    :type "MINE",
    :grid-pos {:page 3, :x 4, :y 2},
    :id 314,
    :items {},
    :sid "3204",
    :results {1014 1}}
   {:facility "Miner",
    :time-spend 60,
    :name "Stalagmite Crystal",
    :type "MINE",
    :grid-pos {:page 3, :x 5, :y 2},
    :id 315,
    :items {},
    :sid "3205",
    :results {1015 1}}
   {:facility "Miner",
    :time-spend 60,
    :name "Unipolar Magnet",
    :type "MINE",
    :grid-pos {:page 3, :x 6, :y 2},
    :id 316,
    :items {},
    :sid "3206",
    :results {1016 1}}
   {:facility "Ray Receiver",
    :time-spend 60,
    :name "Critical Photon",
    :type "MINE",
    :grid-pos {:page 3, :x 1, :y 3},
    :id 508,
    :items {},
    :sid "3301",
    :results {1208 1}}
   {:facility "Assembler",
    :time-spend 60,
    :name "Accumulator (full)",
    :type "ASSEMBLE",
    :grid-pos {:page 3, :x 2, :y 3},
    :id 1507,
    :items {2206 1},
    :sid "3302",
    :results {2207 1}}])

(def non-minable-resources
  [{:facility "Miner",
    :time-spend 60,
    :name "Log",
    :type "MINE",
    :grid-pos {:page 3, :x 8, :y 1},
    :id 330,
    :items {},
    :sid "3108",
    :results {1030 1}}
   {:facility "Miner",
    :time-spend 60,
    :name "Plant Fuel",
    :type "MINE",
    :grid-pos {:page 3, :x 9, :y 1},
    :id 331,
    :items {},
    :sid "3109",
    :results {1031 1}}
   {:facility "Miner",
    :time-spend 60,
    :name "Dark Fog Matrix",
    :type "MINE",
    :grid-pos {:page 3, :x 1, :y 4},
    :id 4501,
    :items {},
    :sid "3401",
    :results {5201 1}}
   {:facility "Miner",
    :time-spend 60,
    :name "Silicon-based Neuron",
    :type "MINE",
    :grid-pos {:page 3, :x 2, :y 4},
    :id 4502,
    :items {},
    :sid "3402",
    :results {5202 1}}
   {:facility "Miner",
    :time-spend 60,
    :name "Matter Recombinator",
    :type "MINE",
    :grid-pos {:page 3, :x 3, :y 4},
    :id 4503,
    :items {},
    :sid "3403",
    :results {5203 1}}
   {:facility "Miner",
    :time-spend 60,
    :name "Negentropy Singularity",
    :type "MINE",
    :grid-pos {:page 3, :x 4, :y 4},
    :id 4504,
    :items {},
    :sid "3404",
    :results {5204 1}}
   {:facility "Miner",
    :time-spend 60,
    :name "Core Element",
    :type "MINE",
    :grid-pos {:page 3, :x 5, :y 4},
    :id 4505,
    :items {},
    :sid "3405",
    :results {5205 1}}
   {:facility "Miner",
    :time-spend 60,
    :name "Energy Shard",
    :type "MINE",
    :grid-pos {:page 3, :x 6, :y 4},
    :id 4506,
    :items {},
    :sid "3406",
    :results {5206 1}}])
