(ns dsp-calculator.data-transform
  (:require
    [clojure.data.json :as json]
    [clojure.java.io :as io]
    [clojure.pprint :as pp]
    [clojure.tools.reader.edn :as edn]
    [clojure.string :as str]
    [clojure.walk :as walk]))

(def root-path "public/data/")

(def files ["locale" "items" "meta" "recipes" "tech"])

(defn main []
  (let [locales (with-open [reader (io/reader (io/resource (str root-path "locale.json")))]
                  (json/read reader))
        en-locale (first (filter #(str/starts-with? (get % "locale") "en-") locales))
        en-strings (get en-locale "strings")]
    (for [file files]
      (with-open [reader (io/reader (io/resource (str root-path file ".json")))
                  writer (io/writer (io/file (io/resource root-path) (str file ".edn")))
                  writer-en (io/writer (io/file (io/resource root-path) (str file "_EN.edn")))]
        (let [json (json/read reader)
              json-en (walk/prewalk-replace en-strings json)]
          (pp/pprint json writer)
          (pp/pprint json-en writer-en))))))
