(ns dsp-calculator.solve-test
  (:require [dsp-calculator.solve :as sut]
            [clojure.test :as t :refer [#?(:clj deftest)]]
            [com.gfredericks.exact :as e]
            #?(:cljs [devcards.core]))
  #?(:cljs (:require-macros
            [devcards.core :as dc :refer [defcard deftest]])))

#?(:cljs (defcard solve-tests
           "# Production Tree Tests"))

(defn ratio [n d]
  (e// (e/native->integer n)
       (e/native->integer d)))

(def test-items
  {1001 {:id 1001
         :name "Iron Ore"}
   1002 {:id 1002
         :name "Copper Ore"}
   1006 {:id 1006
         :name "Coal"}
   1012 {:id 1012
         :name "Kimberlite Ore"}
   1101 {:id 1101
         :name "Iron Ingot"}
   1102 {:id 1102
         :name "Magnet"}
   1104 {:id 1102
         :name "Copper Ingot"}
   1103 {:id 1103
         :name "Steel"}
   1109 {:id 1109
         :name "Energetic Graphite"}
   1112 {:id 1112
         :name "Diamond"}
   1202 {:id 1202
         :name "Magnetic Coil"}})

(def test-recipes
  {1101 [{:id 1
          :name "Iron Ingot"
          :time-spend 60
          :made-from-string "Smelting Facility"
          :items {1001 1}
          :results {1101 1}}]
   1102 [{:id 2
          :name "Magnet"
          :time-spend 90
          :made-from-string "Smelting Facility"
          :items {1001 1}
          :results {1102 1}}]
   1103 [{:id 63
          :name "Steel"
          :time-spend 180
          :made-from-string "Smelting Facility"
          :items {1101 3}
          :results {1103 1}}]
   1104 [{:id 3
          :name "Copper Ingot"
          :time-spend 60
          :made-from-string "Smelting Facility"
          :items {1002 1}
          :results {1104 1}}]
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
          :results {1112 2}}]
   1202 [{:id 6
          :name "Magnetic Coil"
          :time-spend 60
          :made-from-string "Assembler"
          :items {1102 2 1104 1}
          :results {1202 2}}]})

(deftest test-solver
  (t/is (= {:id 1001
            :name "Iron Ore"
            :count (e/native->integer 1)
            :recipe nil
            :facility nil
            :alt-recipes {}
            :items {}}
           (sut/production-tree test-items test-recipes 1001)))
  (t/is (= {:id 1101
            :name "Iron Ingot"
            :count (e/native->integer 1)
            :recipe 1
            :facility "Smelting Facility"
            :alt-recipes {}
            :items {1001 {:id 1001
                          :name "Iron Ore"
                          :count (e/native->integer 1)
                          :recipe nil
                          :facility nil
                          :alt-recipes {}
                          :items {}}}}
           (sut/production-tree test-items test-recipes 1101)))
  (t/is (= {:id 1103
            :name "Steel"
            :count (e/native->integer 1)
            :recipe 63
            :facility "Smelting Facility"
            :alt-recipes {}
            :items {1101 {:id 1101
                          :name "Iron Ingot"
                          :count (e/native->integer 1)
                          :recipe 1
                          :facility "Smelting Facility"
                          :alt-recipes {}
                          :items {1001 {:id 1001
                                        :name "Iron Ore"
                                        :count (e/native->integer 1)
                                        :recipe nil
                                        :facility nil
                                        :alt-recipes {}
                                        :items {}}}}}}
           (sut/production-tree test-items test-recipes 1103)))
  (t/is (= {:id 1101
            :name "Iron Ingot"
            :count (e/native->integer 1)
            :recipe 1
            :facility "Smelting Facility"
            :alt-recipes {}
            :items {nil {:error "max depth reached"}}}
           (binding [sut/*max-depth* 1]
             (sut/production-tree test-items test-recipes 1101))))
  (t/is (= {:id 1112
            :name "Diamond"
            :count (e/native->integer 1)
            :recipe 60
            :facility "Smelting Facility"
            :alt-recipes {61 {:id 1112
                              :name "Diamond"
                              :count (e/native->integer 1)
                              :recipe 61
                              :facility "Smelting Facility"
                              :items {1012 {:id 1012
                                            :name "Kimberlite Ore"
                                            :count (ratio 2 3)
                                            :recipe nil
                                            :facility nil
                                            :alt-recipes {}
                                            :items {}}}}}
            :items {1109 {:id 1109
                          :name "Energetic Graphite"
                          :count (e/native->integer 1)
                          :recipe 17
                          :facility "Smelting Facility"
                          :alt-recipes {}
                          :items {1006 {:id 1006
                                        :name "Coal"
                                        :count (e/native->integer 1)
                                        :recipe nil
                                        :facility nil
                                        :alt-recipes {}
                                        :items {}}}}}}
           (sut/production-tree test-items test-recipes 1112)))
  (t/is (= {:id 1202
            :name "Magnetic Coil"
            :count (e/native->integer 1)
            :recipe 6
            :facility "Assembler"
            :alt-recipes {}
            :items {1104 {:id 1104
                          :name "Copper Ingot"
                          :count (e/native->integer 1)
                          :recipe 3
                          :facility "Smelting Facility"
                          :alt-recipes {}
                          :items {1002 {:id 1002
                                        :name "Copper Ore"
                                        :count (e/native->integer 1)
                                        :recipe nil
                                        :facility nil
                                        :alt-recipes {}
                                        :items {}}}}
                    1102 {:id 1102
                          :name "Magnet"
                          :count (e/native->integer 3)
                          :recipe 2
                          :facility "Smelting Facility"
                          :alt-recipes {}
                          :items {1001 {:id 1001
                                        :name "Iron Ore"
                                        :count (e/native->integer 2)
                                        :recipe nil
                                        :facility nil
                                        :alt-recipes {}
                                        :items {}}}}}}
           (sut/production-tree test-items test-recipes 1202))))
