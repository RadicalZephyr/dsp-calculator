(ns dsp-calculator.devcards.core
  (:require [devcards.core :as dc]
            [reagent.core :as reagent]
            [spade.core   :refer [defglobal defclass defattrs]]
            [garden.units :refer [deg px]]
            [garden.color :refer [rgba]])
  (:require-macros
   [devcards.core :as dc :refer [defcard defcard-rg]]))

(defcard
  "# DSP Calculator")

(defattrs box-attrs []
  {:background "#999"
   :border "medium solid #111"
   :border-radius "5px"})

(defcard-rg reagent-test
  (reagent/as-element
   [:div (box-attrs)
    [:div.box [:h1 "Hello Heading"]]]))
