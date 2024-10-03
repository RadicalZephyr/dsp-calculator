(ns dsp-calculator.app
  (:require
    [reagent.dom :as rd]
    [re-frame.core :as re-frame]
    [day8.re-frame.tracing :refer-macros [fn-traced]]
    [dsp-calculator.ui :as dsp-ui]))

(defn setup! []
  (dsp-ui/setup!)
  (re-frame/reg-event-db
   ::initialise-db
   (fn-traced initialize-db-event [_ _]
              {:dsp-calculator.ui/page :dsp-ui/home})))

(defn main-panel []
  (let [current-page (re-frame/subscribe [:dsp-calculator.ui/page])]
    (fn []
      (let [current-page (deref current-page)]
        [:div
         [dsp-ui/navigation-container current-page]
         [dsp-ui/content current-page]]))))

(defn init-render! []
  (re-frame/dispatch [::initialise-db])
  (rd/render [main-panel]
                  (.getElementById js/document "app")))

(defn ^:export start! []
  (setup!)
  (init-render!))

(defn dev-reload []
  (re-frame/clear-subscription-cache!)
  (setup!))
