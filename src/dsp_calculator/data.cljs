(ns dsp-calculator.data
  (:require
    [ajax.core :as ajax]
    [ajax.edn]
    [re-frame.core :as re-frame]
    [day8.re-frame.tracing :refer-macros [fn-traced]]))

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

(defn init-db [db]
  (assoc db ::data {}))

(defn setup! []
  (re-frame/reg-event-fx
   ::fetch-data
   (fn-traced [_ [_ type key]]
     {:http-xhrio {:method          :get
                   :uri             (str "data/" type ".edn")
                   :timeout         5000
                   :response-format (ajax.edn/edn-response-format)
                   :on-success      [::store-data key]
                   :on-failure      [::failure-fetch-data type key]}}))

  (re-frame/reg-event-fx
   ::failure-fetch-data
   (fn-traced [ctx [_ type key]]
     {}))

  (re-frame/reg-event-db
   ::store-data
   (fn-traced [db [_ key data]]
     (assoc-in db [::data key] data)))

  (re-frame/dispatch [::fetch-data "recipes_EN" ::recipes])
  (re-frame/dispatch [::fetch-data "items_EN" ::items])
  (re-frame/dispatch [::fetch-data "tech_EN" ::tech]))
