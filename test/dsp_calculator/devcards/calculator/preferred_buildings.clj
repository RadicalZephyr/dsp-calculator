(ns dsp-calculator.devcards.calculator.preferred-buildings)

(defmacro pb-card [& body]
  `(fn [state# _#]
     (let [~'belt (reagent.core/cursor state# [:belt])
           ~'mining-productivity (reagent.core/cursor state# [:mining-productivity])
           ~'miner (reagent.core/cursor state# [:miner])
           ~'smelter (reagent.core/cursor state# [:smelter])
           ~'assembler (reagent.core/cursor state# [:assembler])
           ~'chemical (reagent.core/cursor state# [:chemical])]
       [:main.page.calculator
        ~@body])))
