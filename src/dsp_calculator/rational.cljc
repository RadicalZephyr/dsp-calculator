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
      (if (e/> d n)
        {:n (e/integer->string n)
         :d (e/integer->string d)}
        (let [whole (e/quot n d)
              r (e/rem n d)]
          {:w (when (not (e/zero? whole))
                (e/integer->string whole))
           :n (e/integer->string r)
           :d (e/integer->string d)})))))

(defn str [r]
  (let [{:keys [w n d]} (decompose r)]
    (clojure.core/str
     (when w w)
     (when (and w n d)
       "-")
     (when (and n d)
       (clojure.core/str n "/" d)))))
