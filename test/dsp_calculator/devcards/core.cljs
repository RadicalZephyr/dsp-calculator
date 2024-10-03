(ns dsp-calculator.devcards.core
  (:require [devcards.core :as dc]
            [reagent.core :as reagent]
            [dsp-calculator.ui :as dsp-ui])
  (:require-macros
   [devcards.core :as dc :refer [defcard defcard-rg]]))

(defcard
  "# DSP Calculator")


(defcard-rg navigation
  (fn [page _]
    [dsp-ui/navigation @page (fn [new-page]
                               (swap! page
                                      (fn [old new] new)
                                      new-page))])
  (reagent/atom :dsp-ui/home)
  {:inspect-data true})
