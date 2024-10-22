(ns dsp-calculator.solve-test
  (:require [dsp-calculator.solve :as sut]
            [clojure.test :as t]))

(def test-items
  {1001 {:id 1001
         :name "Iron Ore"}
   1101 {:id 1101
         :name "Iron Ingot"}
   1103 {:id 1103
         :name "Steel"}})

(def test-recipes
  {1101 [{:id 1
          :name "Iron Ingot"
          :time-spend 60
          :items [{:id 1001 :count 1}]
          :results [{:id 1101 :count 1}]}]
   1103 [{:id 63
          :name "Steel"
          :items [{:id 1101 :count 3}]
          :results [{:id 1103 :count 1 }]}]})

(t/deftest test-solver
  (t/is (= {:id 1001
            :recipe nil
            :alt-recipes []
            :items {}}
           (sut/production-tree test-recipes 1001)))
  (t/is (= {:id 1101
            :recipe 1
            :alt-recipes []
            :items {1001 {:id 1001
                          :recipe nil
                          :alt-recipes []
                          :items {}}}}
           (sut/production-tree test-recipes 1101)))
  (t/is (= {:id 1103
            :recipe 63
            :alt-recipes []
            :items {1101 {:id 1101
                          :recipe 1
                          :alt-recipes []
                          :items {1001 {:id 1001
                                        :recipe nil
                                        :alt-recipes []
                                        :items {}}}}}}
           (sut/production-tree test-recipes 1103))))
