(ns dsp-calculator.ui.calculator.preferred-buildings
  (:require [spade.core :refer [defclass]]
            [com.gfredericks.exact :as e]
            [dsp-calculator.rational :as r]))

(def conveyor-belts
  [{:id 2001
    :name "Conveyor Belt MK.I"
    :speed (r/int 6)}
   {:id 2002
    :name "Conveyor Belt MK.II"
    :speed (r/int 12)}
   {:id 2003
    :name "Conveyor Belt MK.III"
    :speed (r/int 30)}])

(def mining-productivity-techs
  [{:id 3600
    :name "No Vein Utilization"
    :speed (r/ratio 1 1)}
   {:id 3601
    :name "Vein Utilization I"
    :speed (r/ratio 11 10)}
   {:id 3602
    :name "Vein Utilization II"
    :speed (r/ratio 12 10)}
   {:id 3603
    :name "Vein Utilization III"
    :speed (r/ratio 13 10)}
   {:id 3604
    :name "Vein Utilization IV"
    :speed (r/ratio 14 10)}
   {:id 3605
    :name "Vein Utilization V"
    :speed (r/ratio 15 10)}])

(def miners
  [{:id 2301
    :name "Mining Machine"
    :speed (r/ratio 1 2)}
   {:id 2316
    :name "Advanced Mining Machine"
    :speed (r/int 1)}])

(def smelters
  [{:id 2302
    :name "Arc Smelter"
    :count (r/int 1)}
   {:id 2315
    :name "Plane Smelter"
    :count (r/int 2)}
   {:id 2319
    :name "Negentropy Smelter"
    :count (r/int 3)}])

(def assemblers
  [{:id 2303
    :name "Assembling Machine Mk.I"
    :count (r/ratio 3 4)}
   {:id 2304
    :name "Assembling Machine Mk.II"
    :count (r/int 1)}
   {:id 2305
    :name "Assembling Machine Mk.III"
    :count (r/int 2)}
   {:id 2318
    :name "Re-composing Assembler"
    :count (r/int 3)}])

(def chemical-plants
  [{:id 2309
    :name "Chemical Plant"
    :count (r/int 1)}
   {:id 2317
    :name "Quantum Chemical Plant"
    :count (r/int 2)}])

(declare item-id tech-id)

(def preferred-building-customizations
  {"belt" (fn [building timescale]
            (let [speed (r/str (:speed building))]
              {:id-fn item-id
               :title-suffix (str " — Transport Speed: "
                                  speed
                                  " items per minute")
               :data-key :data-per
               :data-val speed}))
   "mining-productivity" (fn [building timescale]
                           (let [hundred (r/int  100)
                                 percent-increase (e/- (e/* hundred
                                                            (:speed building))
                                                       hundred)]
                             {:id-fn tech-id
                              :title-suffix (str " — Mining Efficiency: +"
                                                 percent-increase
                                                 "%")
                              :data-key :data-count
                              :data-val (str "+" percent-increase "%")}))
   "miner" (fn [building timescale]
             (let [speed (r/str (:speed building))]
               {:id-fn item-id
                :title-suffix (str " — Mining Speed: "
                                   speed
                                   " items per minute per vein")
                :data-key :data-per
                :data-val speed}))
   :else (fn [building timescale]
           (let [speed (r/str (:count building))]
             {:id-fn item-id
              :title-suffix (str " — Production Speed: " speed)
              :data-key :data-count
              :data-val (str speed "×")}))})

(defn get-pb-customizations [type building timescale]
  (let [k (if (contains? preferred-building-customizations type)
            type
            :else)]
    ((get preferred-building-customizations k) building timescale)))

(defclass grid-row [x y]
  {:grid-row (str x " / " y)})

(defn item-id [item]
  (str "item." (:id item)))

(defn tech-id [tech]
  (str "tech." (:id tech)))

(defn preferred-building-option [timescale [x y] type selected change building]
  (let [{:keys [id-fn
                title-suffix
                data-key
                data-val]} (get-pb-customizations type building timescale)]
    [:label {:class [(grid-row x y)
                     (when (= (:id selected) (:id building)) "is-selected")]}
     [:input {:type "radio"
              :name type
              :value (str (:id building))
              :selected (= (:id selected) (:id building))
              :on-change #(change building)
              :title (str (:name building)
                          title-suffix)}]
     [:span.item.icon {:title (:name building)
                       :data-icon (id-fn building)
                       data-key data-val
                       :lang "en-US"}]]))

(defn preferred-building-row [timescale data [x y :as row] type selected ra label]
  (->> data
       (map (fn [item]
              [preferred-building-option timescale row type selected #(reset! ra %) item]))
       (into [[:span.name {:class (grid-row x y)} label]])))

(defn preferred-buildings [& {:keys [facilities
                                     timescale
                                     belt
                                     mining-productivity
                                     miner
                                     smelter
                                     assembler
                                     chemical]}]
  (let [facilities @facilities
        timescale @timescale
        belt-val @belt
        mining-productivity-val @mining-productivity
        miner-val @miner
        smelter-val @smelter
        assembler-val @assembler
        chemical-val @chemical]
    (when (seq facilities)
      [:details.preferred.preferred-buildings {:open true}
       [:summary "Preferred Buildings"]
       (let [row (atom 1)]
         `[:div.fields
           ~@(preferred-building-row timescale
                                     conveyor-belts
                                     [@row (inc @row)]
                                     "belt"
                                     belt-val
                                     belt
                                     "Logistics")

           ~@(when (contains? facilities "Miner")
               (swap! row + 2)
               (preferred-building-row timescale
                                       mining-productivity-techs
                                       [@row (inc @row)]
                                       "mining-productivity"
                                       mining-productivity-val
                                       mining-productivity
                                       "Mining Productivity"))

           ~@(when (contains? facilities "Miner")
               (swap! row + 2)
               (preferred-building-row timescale
                                       miners
                                       [@row (inc @row)]
                                       "miner"
                                       miner-val
                                       miner
                                       "Miner"))

           ~@(when (contains? facilities "Smelting Facility")
               (swap! row + 2)
               (preferred-building-row timescale
                                       smelters
                                       [@row (inc @row)]
                                       "smelter"
                                       smelter-val
                                       smelter
                                       "Smelting Facility"))

           ~@(when (contains? facilities "Assembler")
               (swap! row + 2)
               (preferred-building-row timescale
                                       assemblers
                                       [@row (inc @row)]
                                       "assembler"
                                       assembler-val
                                       assembler
                                       "Assembler"))

           ~@(when (contains? facilities "Chemical Facility")
               (swap! row + 2)
               (preferred-building-row timescale
                                       chemical-plants
                                       [@row (inc @row)]
                                       "chemical"
                                       chemical-val
                                       chemical
                                       "Chemical Facility"))])])))
