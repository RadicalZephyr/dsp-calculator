(ns dsp-calculator.devcards
  (:require
    [devcards.core]
    [dsp-calculator.devcards.core]
    [dsp-calculator.devcards.icons]))

(defn ^:export main []
  (devcards.core/start-devcard-ui!))
