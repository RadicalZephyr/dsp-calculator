(ns dsp-calculator.devcards
  (:require
    [devcards.core]
    [dsp-calculator.devcards.core]
    [dsp-calculator.devcards.icons]
    [dsp-calculator.devcards.research]))

(defn ^:export main []
  (devcards.core/start-devcard-ui!))
