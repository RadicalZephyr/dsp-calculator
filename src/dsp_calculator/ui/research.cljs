(ns dsp-calculator.ui.research
  (:require
    [garden.units :as u]
    [garden.util :as gutil]
    [spade.core :refer [defclass]])
  (:require-macros
   [garden.def :refer [defcssfn]]))

(defcssfn url)

(defclass tech-class []
  {:position "relative"}
  [:span {:color "neon-white"}]
  [:div.tile
   {:display "flex"
    :align-items "center"
    :justify-content "center"
    :position "relative"
    :width "64px"
    :height "64.064px"
    :background-image (url "/images/window-1.svg")}
   [:div.icon
    {:cursor [(url "/images/cursor-pointer.svg") "pointer"]
     :margin "5px"}]])

(defn technology [tech done? toggle-done]
  [:div {:class (tech-class)
         :data-id (tech "id")
         :data-epoch (tech "epoch")}
   [:span (tech "name")]
   [:div.tile
    [:div.icon {:data-icon (str "tech." (tech "id"))}]]])

(defn research []
  [:div [:h2 "Research"]])
