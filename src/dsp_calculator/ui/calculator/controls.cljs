(ns dsp-calculator.ui.calculator.controls
  (:require [clojure.tools.reader.edn :as edn]))

(defn inc-dec-buttons [change control value]
  [:div.steppers
   [:button.increment {:on-click #(change control (inc value))}]
   [:button.decrement {:on-click #(change control (dec value))}]])

(defn ratio-control [change ratio production-facility]
  [:label.ratio
   [:input.factor
    {:type "number"
     :min "0"
     :value (str ratio)
     :on-change #(change :ratio (-> % .-target .-value edn/read-string))}]
   [inc-dec-buttons change :ratio ratio]
   [:span.text (str "× " production-facility)]])

(defn specific-control [change specific timescale]
  [:label.specific
   [:input.factor
    {:type "number"
     :min "0"
     :value (str specific)
     :on-change #(change :specific (-> % .-target .-value edn/read-string))}]
   [inc-dec-buttons change :specific specific]
   [:span.text "items"
    [:select.timescale {:on-change #(change :timescale (-> % .-target .-value))}
     [:option {:value "minute"} "per minute"]
     [:option {:value "second"} "per second"]]]])

(defn proliferator-control [change proliferator]
  [:label.proliferator
   (let [max-proliferator "Proliferator Mk.III"]
     [:span {:title (str "Highest unlocked tier will be used." max-proliferator)}
      "Proliferator: "])
   [:select {:title "No proliferator to be used"
             :on-change #(change :proliferator (-> % .-target .-value))}
    [:option {:value "none"} "None"]
    [:option {:value "mixed.tsp"} "Mix by The Superior Tentacle"]
    [:option {:value "mixed.ab"} "Mix by Aaronbog"]
    [:option {:value "speedup"} "Production Speedup"]
    [:option {:value "extra"} "Extra Products"]
    [:option {:value "custom"} "Customized"]]])

(defn default-controls []
  {:ratio 1
   :specific nil
   :timescale "minute"
   :proliferator "none"})

(defn rescale-specific [specific old-timescale new-timescale]
  (when specific
    (cond
      (= old-timescale new-timescale) specific
      (= new-timescale "second") (/ specific 60)
      (= new-timescale "minute") (* specific 60)
      :else specific)))

(defn update-controls [controls setting value]
  (case setting
    :specific (assoc controls
                     :ratio nil
                     :specific value)
    :ratio (assoc controls
                  :ratio value
                  :specific nil)
    :timescale (-> controls
                   (assoc :timescale value)
                   (update :specific rescale-specific (:timescale controls) value))
    :proliferator (assoc controls
                         :proliferator value)))

(def ticks-per
  {"second" 60
   "minute" (* 60 60)})

(defn output-quantity [recipe]
  (get-in recipe [:results (:id recipe)] 1))

(defn render-ratio [control-spec selected-recipe]
  (let [ratio (:ratio control-spec)
        num-ticks (ticks-per (:timescale control-spec))
        num-produced (output-quantity selected-recipe)
        production-time (:time-spend selected-recipe)]
    (assoc control-spec :specific (/ (* ratio num-produced num-ticks)
                                     production-time))))

(defn render-specific [control-spec selected-recipe]
  (let [specific (:specific control-spec)
        num-ticks (ticks-per (:timescale control-spec))
        num-produced (output-quantity selected-recipe)
        production-time (:time-spend selected-recipe)]
    (assoc control-spec :ratio (/ (* specific production-time)
                                  (* num-produced num-ticks)))))

(defn render-controls [[control-spec selected-recipe]]
  (cond
    (nil? (:specific control-spec)) (render-ratio control-spec selected-recipe)
    (nil? (:ratio control-spec)) (render-specific control-spec selected-recipe)
    :else control-spec))
