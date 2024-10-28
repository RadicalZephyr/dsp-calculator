(ns dsp-calculator.solve-test
  (:require [dsp-calculator.solve :as sut]
            [clojure.test :as t]))

(def test-items
  {1001 {:id 1001
         :name "Iron Ore"}
   1006 {:id 1006
         :name "Coal"}
   1101 {:id 1101
         :name "Iron Ingot"}
   1103 {:id 1103
         :name "Steel"}
   1109 {:id 1109
         :name "Energetic Graphite"}
   1012 {:id 1012
         :name "Kimberlite Ore"}
   1112 {:id 1112
         :name "Diamond"}})

(def test-recipes
  {1101 [{:id 1
          :name "Iron Ingot"
          :time-spend 60
          :made-from-string "Smelting Facility"
          :items {1001 1}
          :results {1101 1}}]
   1103 [{:id 63
          :name "Steel"
          :time-spend 180
          :made-from-string "Smelting Facility"
          :items {1101 3}
          :results {1103 1}}]
   1109 [{:id 17
          :name "Energetic Graphite"
          :time-spend 120
          :made-from-string "Smelting Facility"
          :items {1006 2}
          :results {1 1109}}]
   1112 [{:id 60
          :name "Diamond"
          :time-spend 120
          :made-from-string "Smelting Facility"
          :items {1109 1}
          :results {1112 1}}
         {:id 61
          :name "Diamond"
          :time-spend 90
          :made-from-string "Smelting Facility"
          :items {1012 1}
          :results {1112 2}}]})

(t/deftest test-solver
  (t/is (= {:id 1001
            :name "Iron Ore"
            :count 1
            :recipe nil
            :facility nil
            :alt-recipes {}
            :items {}}
           (sut/production-tree test-items test-recipes 1001)))
  (t/is (= {:id 1101
            :name "Iron Ingot"
            :count 1
            :recipe 1
            :facility "Smelting Facility"
            :alt-recipes {}
            :items {1001 {:id 1001
                          :name "Iron Ore"
                          :count 1
                          :recipe nil
                          :facility nil
                          :alt-recipes {}
                          :items {}}}}
           (sut/production-tree test-items test-recipes 1101)))
  (t/is (= {:id 1103
            :name "Steel"
            :count 1
            :recipe 63
            :facility "Smelting Facility"
            :alt-recipes {}
            :items {1101 {:id 1101
                          :name "Iron Ingot"
                          :count 3
                          :recipe 1
                          :facility "Smelting Facility"
                          :alt-recipes {}
                          :items {1001 {:id 1001
                                        :name "Iron Ore"
                                        :count 3
                                        :recipe nil
                                        :facility nil
                                        :alt-recipes {}
                                        :items {}}}}}}
           (sut/production-tree test-items test-recipes 1103)))
  (t/is (= {:id 1101
            :name "Iron Ingot"
            :count 1
            :recipe 1
            :facility "Smelting Facility"
            :alt-recipes {}
            :items {nil {:error "max depth reached"}}}
           (binding [sut/*max-depth* 1]
             (sut/production-tree test-items test-recipes 1101))))
  (t/is (= {:id 1112
            :name "Diamond"
            :count 1
            :recipe 60
            :facility "Smelting Facility"
            :alt-recipes {61 {:id 1112
                              :name "Diamond"
                              :count 1
                              :recipe 61
                              :facility "Smelting Facility"
                              :items {1012 {:id 1012
                                            :name "Kimberlite Ore"
                                            :count 1
                                            :recipe nil
                                            :facility nil
                                            :alt-recipes {}
                                            :items {}}}}}
            :items {1109 {:id 1109
                          :name "Energetic Graphite"
                          :count 1
                          :recipe 17
                          :facility "Smelting Facility"
                          :alt-recipes {}
                          :items {1006 {:id 1006
                                        :name "Coal"
                                        :count 2
                                        :recipe nil
                                        :facility nil
                                        :alt-recipes {}
                                        :items {}}}}}}
           (sut/production-tree test-items test-recipes 1112))))
