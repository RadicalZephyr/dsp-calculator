(ns dsp-calculator.ui
  (:require
    [reagent.core :as reagent]
    [spade.core   :refer [defclass defattrs]]))

(defn home []
  [:div [:h2 "Home"]])

(defn calculator []
  [:div [:h2 "Calculator"]])

(defattrs menu-attrs []
  {:background "#333"
   :margin 0
   :padding 0}
  [:ul {:list-style-type "none"
        :margin 0
        :padding 0
        :overflow "hidden"}
   [:li {:float "left"}
    [:a {:display "block"
         :padding "8px"}
     [:&:hover
      {:background "#999"
       :cursor "pointer"
       :text-decoration "underline"}]]]])

(defclass menu-item-class []
  {:background "#777"
   :display "block"
   :color "#F2F"}
  [:&.active-link {:background "#AAA"}])

(def page-name
  {:dsp-ui/home "DSP Ratios"
   :dsp-ui/calculator "Calculator"
   :dsp-ui/research "Research"})

(defn nav-item [this-page current-page change-page]
  [:a {:on-click #(change-page this-page)
       :class [(menu-item-class) (when (= current-page this-page)
                                   "active-link")]}
   (page-name this-page)])

(defn navigation [current-page change-page]
  (let [current-page (or current-page ::home)]
   [:header
    [:nav (menu-attrs)
     [:ul
      (for [[idx page] (map-indexed (fn [idx item] [idx item])
                                    [:dsp-ui/home :dsp-ui/calculator :dsp-ui/research])]
        ^{:key idx} [:li [nav-item page current-page change-page]])]]]))
