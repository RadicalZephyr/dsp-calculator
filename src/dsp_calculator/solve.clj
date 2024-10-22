(ns dsp-calculator.solve
  (:require [clojure.java.io :as io]
            [clojure.tools.reader.edn :as edn]))

(def root-path "public/data/")

(defn read-edn-resource [filename]
  (with-open [r (java.io.PushbackReader.
                 (io/reader (io/file (io/resource root-path)
                                     (str filename ".edn"))))]
    (edn/read r)))

(defn repl-load-edn []
  (def items-edn (read-edn-resource "items_EN"))
  (def tech-edn (read-edn-resource "tech_EN"))
  (def recipes-edn (read-edn-resource "recipes_EN")))

(defn find-item-by-id [id items]
  (first (filter #(= id (:id %)) items)))

(defn find-item-by-name [name items]
  (first (filter #(= name (:name %)) items)))
