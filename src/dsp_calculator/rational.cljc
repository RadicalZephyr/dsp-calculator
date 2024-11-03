(ns dsp-calculator.rational
  (:refer-clojure :exclude [int str])
  (:require [com.gfredericks.exact :as e]))

(defn int [i]
  (e/native->integer i))

(defn ratio [x y]
  (e// (e/native->integer x)
       (e/native->integer y)))

(defn decompose [r]
  (if (e/integer? r)
    {:w (e/integer->string r)}
    (let [n (e/numerator r)
          d (e/denominator r)]
      (if (> d n)
        {:n n :d d}
        (let [whole (quot n d)
              r (rem n d)]
          {:w (when (not (= 0 whole))
                whole)
           :n r
           :d d})))))

(defn str [r]
  (let [{:keys [w n d]} (decompose r)]
    (clojure.core/str
     (when w w)
     (when (and w n d)
       "-")
     (when (and n d)
       (clojure.core/str n "/" d)))))
