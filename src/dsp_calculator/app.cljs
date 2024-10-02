(ns dsp-calculator.app
  (:require
    [reagent.dom :as rd]
    [re-frame.core :as re-frame]
    [day8.re-frame.tracing :refer-macros [fn-traced]]))

(defn setup! []
  (re-frame/reg-event-db
   :initialise-db
   (fn-traced
    initialize-db-event
    [_ _]
    {})))

(defn main-panel []
  [:div
   [:h1 "DSP Calculator"]])

(defn init-render! []
  (re-frame/dispatch [:initialise-db])
  (rd/render [main-panel]
                  (.getElementById js/document "app")))

(defn ^:export start! []
  (setup!)
  (init-render!))

(defn dev-reload []
  (re-frame/clear-subscription-cache!)
  (setup!))
