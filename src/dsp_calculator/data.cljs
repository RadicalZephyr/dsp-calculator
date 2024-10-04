(ns dsp-calculator.data)

(def assemble-production-speed
  {2303 0.75
   2304 1
   2305 1.5
   2318 3})

(def smelter-production-speed
  {2302 1
   2315 2
   2319 3})

(def chemical-production-speed
  {2309 1
   2317 2})

(def belt-transport-speed
  {2001 (* 6 60)
   2002 (* 12 60)
   2003 (* 30 60)})

(def research-speed
  {2901 1
   2902 3})

(def mining-speed
  {2301 30
   2316 60})

(def auto-replenish-fuels
  [1804 1803 1802 1801 1130 1129 1128 1121 1120 1109 1011 1114 1007 1006 1117 1030 1031])
