(ns dsp-calculator.solve-test
  (:require [dsp-calculator.solve :as sut]
            [clojure.test :as t]))

(def test-items
  {1001 {:id 1001
         :name "Iron Ore"}
   1101 {:id 1101
         :name "Iron Ingot"}})

(def test-resources
  {1 {:id 1
      :name "Iron Ingot"
      :time-spend 60
      :items [1001]
      :item-counts [1]
      :results [1101]
      :result-counts [1]}})

(t/deftest test-solver
  (t/is (= 1 2)))
