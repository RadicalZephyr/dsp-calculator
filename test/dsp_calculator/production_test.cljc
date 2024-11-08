(ns dsp-calculator.production-test
  (:require [dsp-calculator.production :as sut]
            [clojure.test :as t :refer [#?(:clj deftest)]]
            [com.gfredericks.exact :as e]
            #?(:cljs [devcards.core])
            [dsp-calculator.rational :as r])
  #?(:cljs (:require-macros
            [devcards.core :as dc :refer [defcard deftest]])))

#?(:cljs (defcard solve-tests
           "# Production Tree Tests"))

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
   1201 {:id 1201
         :name "Gear"}
   1202 {:id 1202
         :name "Magnetic Coil"}
   1203 {:id 1203
         :name "Electric Motor"}})

(def test-recipes
  {1 {:id 1
      :name "Iron Ingot"
      :time-spend 60
      :facility "Smelting Facility"
      :grid-pos {:page 1 :x 1 :y 1}
      :items {1001 1}
      :results {1101 1}}
   2 {:id 2
      :name "Magnet"
      :time-spend 90
      :facility "Smelting Facility"
      :grid-pos {:page 1 :x 2 :y 1}
      :items {1001 1}
      :results {1102 1}}
   3 {:id 3
      :name "Copper Ingot"
      :time-spend 60
      :facility "Smelting Facility"
      :grid-pos {:page 1 :x 1 :y 2}
      :items {1002 1}
      :results {1104 1}}
   5 {:id 5
      :name "Gear"
      :time-spend 60
      :facility "Assembler"
      :grid-pos {:page 1 :x 4 :y 1}
      :items {1101 1}
      :results {1201 1}}
   6 {:id 6
      :name "Magnetic Coil"
      :time-spend 60
      :facility "Assembler"
      :grid-pos {:page 1 :x 2 :y 2}
      :items {1102 2, 1104 1}
      :results {1202 2}}
   17 {:id 17
       :name "Energetic Graphite"
       :time-spend 120
       :facility "Smelting Facility"
       :grid-pos {:page 1 :x 1 :y 6}
       :items {1006 2}
       :results {1109 1}}
   60 {:id 60
       :name "Diamond"
       :time-spend 120
       :facility "Smelting Facility"
       :grid-pos {:page 1 :x 2 :y 6}
       :items {1109 1}
       :results {1112 1}}
   61 {:id 61
       :name "Diamond"
       :time-spend 90
       :facility "Smelting Facility"
       :grid-pos {:page 1 :x 3 :y 6}
       :items {1012 1}
       :results {1112 2}}
   63 {:id 63
       :name "Steel"
       :time-spend 180
       :facility "Smelting Facility"
       :grid-pos {:page 1 :x 3 :y 1}
       :items {1101 3}
       :results {1103 1}}
   97 {:id 97
       :name "Electric Motor"
       :time-spend 120
       :facility "Assembler"
       :grid-pos {:page 1 :x 3 :y 2}
       :items {1101 2, 1201 1, 1202 1}
       :results {1203 1}}})

(def test-recipes-by-output
  (sut/group-by-outputs test-recipes))

(deftest test-solver
  (t/is (= {:id 1101
            :name "Iron Ingot"
            :count (r/int 1)
            :recipe 1
            :time-spend 60
            :facility "Smelting Facility"
            :results {1101 1}
            :items {1001 {:id 1001
                          :name "Iron Ore"
                          :count (r/int 1)
                          :recipe nil
                          :time-spend nil
                          :facility nil
                          :results {}
                          :items {}}}}
           (sut/production-tree test-items test-recipes test-recipes-by-output 1)))
  (t/is (= {:id 1103
            :name "Steel"
            :count (r/int 1)
            :recipe 63
            :time-spend 180
            :facility "Smelting Facility"
            :results {1103 1}
            :items {1101 {:id 1101
                          :name "Iron Ingot"
                          :count (r/int 1)
                          :recipe 1
                          :time-spend 60
                          :facility "Smelting Facility"
                          :results {1101 1}
                          :items {1001 {:id 1001
                                        :name "Iron Ore"
                                        :count (r/int 1)
                                        :recipe nil
                                        :time-spend nil
                                        :facility nil
                                        :results {}
                                        :items {}}}}}}
           (sut/production-tree test-items test-recipes test-recipes-by-output 63)))
  (t/is (= {:id 1101
            :name "Iron Ingot"
            :count (r/int 1)
            :recipe 1
            :time-spend 60
            :facility "Smelting Facility"
            :results {1101 1}
            :items {nil {:error "max depth reached"}}}
           (binding [sut/*max-depth* 1]
             (sut/production-tree test-items test-recipes test-recipes-by-output 1))))
  (t/is (= {:id 1112
            :name "Diamond"
            :count (r/int 1)
            :recipe 60
            :time-spend 120
            :facility "Smelting Facility"
            :results {1112 1}
            :items {1109 {:id 1109
                          :name "Energetic Graphite"
                          :count (r/int 1)
                          :recipe 17
                          :time-spend 120
                          :facility "Smelting Facility"
                          :results {1109 1}
                          :items {1006 {:id 1006
                                        :name "Coal"
                                        :count (r/int 1)
                                        :recipe nil
                                        :time-spend nil
                                        :facility nil
                                        :results {}
                                        :items {}}}}}}
           (sut/production-tree test-items test-recipes test-recipes-by-output 60)))
  (t/is (= {:id 1202
            :name "Magnetic Coil"
            :count (r/int 1)
            :recipe 6
            :time-spend 60
            :facility "Assembler"
            :results {1202 2}
            :items {1104 {:id 1104
                          :name "Copper Ingot"
                          :count (r/int 1)
                          :recipe 3
                          :time-spend 60
                          :facility "Smelting Facility"
                          :results {1104 1}
                          :items {1002 {:id 1002
                                        :name "Copper Ore"
                                        :count (r/int 1)
                                        :recipe nil
                                        :time-spend nil
                                        :facility nil
                                        :results {}
                                        :items {}}}}
                    1102 {:id 1102
                          :name "Magnet"
                          :count (r/int 3)
                          :recipe 2
                          :time-spend 90
                          :facility "Smelting Facility"
                          :results {1102 1}
                          :items {1001 {:id 1001
                                        :name "Iron Ore"
                                        :count (r/int 2)
                                        :recipe nil
                                        :time-spend nil
                                        :facility nil
                                        :results {}
                                        :items {}}}}}}
           (sut/production-tree test-items test-recipes test-recipes-by-output 6)))
  (t/is (= {:id 1203
            :name "Electric Motor"
            :count (r/int 1)
            :recipe 97
            :time-spend 120
            :facility "Assembler"
            :results {1203 1}
            :items {1101 {:id 1101
                          :name "Iron Ingot"
                          :count (r/int 1)
                          :recipe 1
                          :time-spend 60
                          :facility "Smelting Facility"
                          :results {1101 1}
                          :items {1001 {:id 1001
                                        :name "Iron Ore"
                                        :count (r/int 1)
                                        :recipe nil
                                        :time-spend nil
                                        :facility nil
                                        :results {}
                                        :items {}}}}
                    1201 {:id 1201
                          :name "Gear"
                          :count (r/ratio 1 2)
                          :recipe 5
                          :time-spend 60
                          :facility "Assembler"
                          :results {1201 1}
                          :items {1101 {:id 1101
                                        :name "Iron Ingot"
                                        :count (r/ratio 1 2)
                                        :recipe 1
                                        :time-spend 60
                                        :facility "Smelting Facility"
                                        :results {1101 1}
                                        :items {1001 {:id 1001
                                                      :name "Iron Ore"
                                                      :count (r/ratio 1 2)
                                                      :recipe nil
                                                      :time-spend nil
                                                      :facility nil
                                                      :results {}
                                                      :items {}}}}}}
                    1202 {:id 1202
                          :name "Magnetic Coil"
                          :count (r/ratio 1 4)
                          :recipe 6
                          :time-spend 60
                          :facility "Assembler"
                          :results {1202 2}
                          :items {1104 {:id 1104
                                        :name "Copper Ingot"
                                        :count (r/ratio 1 4)
                                        :recipe 3
                                        :time-spend 60
                                        :facility "Smelting Facility"
                                        :results {1104 1}
                                        :items {1002 {:id 1002
                                                      :name "Copper Ore"
                                                      :count (r/ratio 1 4)
                                                      :recipe nil
                                                      :time-spend nil
                                                      :facility nil
                                                      :results {}
                                                      :items {}}}}
                                  1102 {:id 1102
                                        :name "Magnet"
                                        :count (r/ratio 3 4)
                                        :recipe 2
                                        :time-spend 90
                                        :facility "Smelting Facility"
                                        :results {1102 1}
                                        :items {1001 {:id 1001
                                                      :name "Iron Ore"
                                                      :count (r/ratio 1 2)
                                                      :recipe nil
                                                      :time-spend nil
                                                      :facility nil
                                                      :results {}
                                                      :items {}}}}}}}}
           (sut/production-tree test-items test-recipes test-recipes-by-output 97))))

(deftest test-summary
  (t/is (= {:facilities #{"Smelting Facility"}
            :raw-resources {1001 {:id 1001
                                  :name "Iron Ore"
                                  :count (r/int 1)}}}
           (sut/summarize
            (sut/production-tree test-items test-recipes test-recipes-by-output 1))
           (sut/summarize
            (sut/production-tree test-items test-recipes test-recipes-by-output 63))))

  (t/is (= {:facilities #{"Smelting Facility"}
            :raw-resources {1006 {:id 1006
                                  :name "Coal"
                                  :count (r/int 1)}}}
           (sut/summarize
            (sut/production-tree test-items test-recipes test-recipes-by-output 17))
           (sut/summarize
            (sut/production-tree test-items test-recipes test-recipes-by-output 60))))

  (t/is (= {:facilities #{"Assembler" "Smelting Facility"}
            :raw-resources {1001 {:id 1001
                                  :name "Iron Ore"
                                  :count (r/int 2)}
                            1002 {:id 1002
                                  :name "Copper Ore"
                                  :count (r/int 1)}}}
           (sut/summarize
            (sut/production-tree test-items test-recipes test-recipes-by-output 6))))

  (t/is (= {:facilities #{"Assembler" "Smelting Facility"}
            :raw-resources {1001 {:id 1001
                                  :name "Iron Ore"
                                  :count (r/int 2)}
                            1002 {:id 1002
                                  :name "Copper Ore"
                                  :count (r/ratio 1 4)}}}
           (sut/summarize
            (sut/production-tree test-items test-recipes test-recipes-by-output 97)))))
