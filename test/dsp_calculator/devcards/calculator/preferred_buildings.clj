(ns dsp-calculator.devcards.calculator.preferred-buildings)

(defmacro pb-card
  "Creates the boilerplate for creating a preferred-buildings devcard.

  Within the body of this macro, the symbol `args` is unhygiencally
  bound to a list of all the arguments needed by the
  `preferred-buildings` reagent component. The proper way to utilize
  this, is to use syntax quote within the body of this macro, and then
  do an unquoting splice on `args` to pass all the arguments to
  `preferred-buildings`, like this:

  ```clojure
  (defcard-rg example-card
    (pb-card
      `[preferred-buildings ~@args]))
  ```

  To future me who reads this code, this may seem overly complex, but
  it has the advantage of eliminating the need to repeat the list of
  arguments to `preferred-buildings` for every call, allowing us to
  have lots of devcards to showcase every combination while keeping
  the maintenance burden of adding more arguments relatively low."
  [& body]
  (let [body-fn (eval `(fn [~'args] [~@body]))]
    `(fn [state# _#]
       (let [belt# (reagent.core/cursor state# [:belt])
             mining-productivity# (reagent.core/cursor state# [:mining-productivity])
             miner# (reagent.core/cursor state# [:miner])
             smelter# (reagent.core/cursor state# [:smelter])
             assembler# (reagent.core/cursor state# [:assembler])
             chemical# (reagent.core/cursor state# [:chemical])]
         [:main.page.calculator
          ~@(body-fn
             `[belt# mining-productivity# miner# smelter# assembler# chemical#])]))))
