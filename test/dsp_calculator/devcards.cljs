(ns dsp-calculator.devcards
  (:require
    [devcards.core]
    [dsp-calculator.devcards.core]))

(defn ^:export main []
  (devcards.core/start-devcard-ui!))
