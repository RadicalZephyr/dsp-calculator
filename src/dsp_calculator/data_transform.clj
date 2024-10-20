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

(def root-path "public/data/")

(def files ["meta" "locale" "items" "recipes" "tech"])

(defn type-map? [item]
  (and (map? item)
       (= #{":type" "value"} (set (keys item)))))

(defn simplify [{type ":type" value "value" :as item}]
  (if (= type "BigInt")
    (edn/read-string value)
    item))

(defn kebab-keywordify [k]
  [k (keyword (csk/->kebab-case k))])

(defn cljify-map [m]
  (let [smap (into {} (map kebab-keywordify (keys m)))]
    (set/rename-keys m smap)))

(defn revive [item]
  (cond (type-map? item) (simplify item)
        (map? item) (cljify-map item)
        :else item))


(defn main []
  (let [locales (with-open [reader (io/reader (io/resource (str root-path "locale.json")))]
                  (json/read reader))
        en-locale (first (filter #(str/starts-with? (get % "locale") "en-") locales))
        en-strings (get en-locale "strings")]
    (doseq [filename files]
      (prn filename)
      (let [raw-json (with-open [reader (io/reader
                                         (io/resource
                                          (str root-path filename ".json")))]
                       (json/read reader))
            json (walk/postwalk revive raw-json)
            json-en (walk/prewalk-replace en-strings json)]
        (with-open [writer (io/writer
                            (io/file
                             (io/resource root-path) (str filename ".edn")))
                    writer-en (io/writer
                               (io/file
                                (io/resource root-path) (str filename "_EN.edn")))]
          (pp/pprint json writer)
          (pp/pprint json-en writer-en))))))
