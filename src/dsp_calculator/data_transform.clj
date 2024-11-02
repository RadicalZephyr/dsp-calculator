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
        [page x y] (map #(Integer/parseInt %) grid-pos-s)]
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

(def files ["meta" "items" "recipes" "tech"])

(defn main []
  (let [locales (read-json-resource "locale")
        en-locale (first (filter #(str/starts-with? (get % "locale") "en-") locales))
        en-strings (get en-locale "strings")]
    (doseq [filename files]
      (prn filename)
      (let [raw-json (read-json-resource filename)
            json (walk/prewalk transform raw-json)
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
