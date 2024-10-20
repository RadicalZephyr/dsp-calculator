(ns dsp-calculator.data-transform
  (:require
    [clojure.data.json :as json]
    [clojure.java.io :as io]
    [clojure.pprint :as pp]
    [clojure.tools.reader.edn :as edn]
    [clojure.string :as str]
    [clojure.walk :as walk]))

(defn json-string->key
  "Extract the keys of a string-keyed JSON map and create a mapping from
  that string to the keywordized version."
  [json]
  (->> (keys json)
       (map (fn [k] [k (keyword k)]))
       (reduce conj {})))

(def meta-key-renames
  {"generatedAt" :generated-at,
   "version" :version})

(def locale-key-renames
  {"lcid" :lcid,
   "name" :name,
   "locale" :locale,
   "fallback" :fallback,
   "glyph" :glyph,
   "strings" :strings})

(def item-key-renames
  {"id" :id,
   "name" :name,
   "gridIndex" :grid-index,
   "miningFrom" :mining-from,
   "type" :type,
   "productive" :productive,
   "iconPath" :icon-path,
   "unlockKey" :unlock-key,
   "stackSize" :stack-size,
   "_typeString" :type-string,
   "descFields" :desc-fields,
   "_descFields" :_desc-fields,
   "description" :description})

(def recipe-key-renames
  {"id" :id,
   "name" :name,
   "type" :type,
   "timeSpend" :time-spend,
   "items" :items,
   "itemCounts" :item-counts,
   "results" :results,
   "sid" :sid,
   "gridIndex" :grid-index,
   "resultCounts" :result-counts,
   "_madeFromString" :made-from-string,
   "handcraft" :handcraft})

(def tech-key-renames
  {"id" :id,
   "name" :name,
   "published" :published,
   "iconPath" :icon-path,
   "hashNeeded" :hash-needed,
   "position" :position,
   "description" :description})

(def root-path "public/data/")

(def files [["meta" meta-key-renames]
            ["locale" locale-key-renames]
            ["items" item-key-renames]
            ["recipes" recipe-key-renames]
            ["tech" tech-key-renames]])

(defn type-map? [item]
  (and (map? item)
       (= #{":type" "value"} (set (keys item)))))

(defn simplify [{type ":type" value "value" :as item}]
  (if (= type "BigInt")
    (edn/read-string value)
    item))

(defn make-revive [replace-keys]
  (fn revive [item]
    (cond (type-map? item) (simplify item)
          (contains? replace-keys item) (replace-keys item)
          :else item)))

(defn main []
  (let [locales (with-open [reader (io/reader (io/resource (str root-path "locale.json")))]
                  (json/read reader))
        en-locale (first (filter #(str/starts-with? (get % "locale") "en-") locales))
        en-strings (get en-locale "strings")]
    (for [[filename key-replacements] files]
      (let [raw-json (with-open [reader (io/reader
                                         (io/resource
                                          (str root-path filename ".json")))]
                       (json/read reader))
            revive (make-revive key-replacements)
            json (walk/prewalk revive raw-json)
            json-en (walk/prewalk-replace en-strings json)]
        (with-open [writer (io/writer
                            (io/file
                             (io/resource root-path) (str filename ".edn")))
                    writer-en (io/writer
                               (io/file
                                (io/resource root-path) (str filename "_EN.edn")))]
          (pp/pprint json writer)
          (pp/pprint json-en writer-en))))))
