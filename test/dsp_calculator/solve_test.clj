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
          :made-from-string "Smelting Facility"
          :items [{:id 1001 :count 1}]
          :results [{:id 1101 :count 1}]}]
   1103 [{:id 63
          :name "Steel"
          :time-spend 180
          :made-from-string "Smelting Facility"
          :items [{:id 1101 :count 3}]
          :results [{:id 1103 :count 1 }]}]
   1112 [{:id 60
          :name "Diamond"
          :time-spend 120
          :made-from-string "Smelting Facility"
          :items [{:id 1109 :count 1}]
          :results [{:id 1112 :count 1}]}
         {:id 61
          :name "Diamond"
          :time-spend 90
          :made-from-string "Smelting Facility"
          :items [{:id 1012 :count 1}]
          :results [{:id 1112 :count 2}]}]})

(t/deftest test-solver
  (t/is (= [{:alt-recipes #{}
             :facilities #{}
             :raw-resources {}}
            {:id 1001
             :recipe nil
             :facility nil
             :alt-recipes []
             :items {}}]
           (sut/production-tree test-recipes 1001)))
  (t/is (= [{:alt-recipes #{}
             :facilities #{"Smelting Facility"}
             :raw-resources {}}
            {:id 1101
             :recipe 1
             :facility "Smelting Facility"
             :alt-recipes []
             :items {1001 {:id 1001
                           :recipe nil
                           :facility nil
                           :alt-recipes []
                           :items {}}}}]
           (sut/production-tree test-recipes 1101)))
  (t/is (= [{:alt-recipes #{}
             :facilities #{"Smelting Facility"}
             :raw-resources {}}
            {:id 1103
             :recipe 63
             :facility "Smelting Facility"
             :alt-recipes []
             :items {1101 {:id 1101
                           :recipe 1
                           :facility "Smelting Facility"
                           :alt-recipes []
                           :items {1001 {:id 1001
                                         :recipe nil
                                         :facility nil
                                         :alt-recipes []
                                         :items {}}}}}}]
           (sut/production-tree test-recipes 1103)))
  (t/is (= [{:alt-recipes #{}
             :facilities #{"Smelting Facility"}
             :raw-resources {}}
            {:id 1101,
             :recipe 1,
             :facility "Smelting Facility"
             :alt-recipes [],
             :items {nil {:error "max depth reached"}}}]
           (binding [sut/*max-depth* 1]
             (sut/production-tree test-recipes 1101))))
  (t/is (= [{:alt-recipes #{[60 61]}
             :facilities #{"Smelting Facility"}
             :raw-resources {}}
            {:id 1112
             :recipe 60
             :facility "Smelting Facility"
             :alt-recipes [61]
             :items {1109 {:id 1109
                           :recipe nil
                           :facility nil
                           :alt-recipes []
                           :items {}}}}]
           (sut/production-tree test-recipes 1112))))
