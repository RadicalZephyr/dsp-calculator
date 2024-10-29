(ns dsp-calculator.devcards.calculator.preferred-buildings)

(defmacro pb-card
  "Creates the boilerplate for creating a preferred-buildings devcard.

  Within the body of this macro, the symbol passed as `args-sym` is
  bound to a dictionary of the arguments needed by the
  `preferred-buildings` reagent component. The proper way to utilize
  this, is to just reference this symbol in the body of this macro
  wherever it needs to be passed to `preferred-buildings` like this:

  ```clojure
  (defcard-rg example-card
    (pb-card [args]
      [preferred-buildings args]))
  ```"
  {:style/indent 1}
  [[args-sym] & body]
  `(fn [state# _#]
     (let [~args-sym
           {:belt (reagent.core/cursor state# [:belt])
            :mining-productivity (reagent.core/cursor state# [:mining-productivity])
            :miner (reagent.core/cursor state# [:miner])
            :smelter (reagent.core/cursor state# [:smelter])
            :assembler (reagent.core/cursor state# [:assembler])
            :chemical (reagent.core/cursor state# [:chemical])}]
       [:main.page.calculator
        ~@body])))
