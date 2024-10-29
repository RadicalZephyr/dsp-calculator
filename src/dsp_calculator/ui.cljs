(ns dsp-calculator.ui
  (:require
    [reagent.core :as reagent]
    [re-frame.core :as re-frame]
    [day8.re-frame.tracing :refer-macros [fn-traced]]
    [spade.core   :refer [defclass defattrs]]
    [dsp-calculator.ui.calculator :as c]
    [dsp-calculator.ui.research :as r]))

(defn stylesheet-includes []
  [:div
   [:link {:rel "stylesheet" :href "/css/reset.css"}]
   [:link {:rel "stylesheet" :href "/css/variables.css"}]
   [:link {:rel "stylesheet" :href "/css/skeleton.css"}]
   [:link {:rel "stylesheet" :href "/css/cursor.css"}]
   [:link {:rel "stylesheet" :href "/css/fonts.css"}]
   [:link {:rel "stylesheet" :href "/css/icons.css"}]
   [:link {:rel "stylesheet" :href "/css/dev-icons.css"}]
   [:link {:rel "stylesheet" :href "/css/pages/calculator.css"}]
   [:link {:rel "stylesheet" :href "/css/components/combo-selector.css"}]
   [:link {:rel "stylesheet" :href "/css/components/dialog.css"}]
   [:link {:rel "stylesheet" :href "/css/components/item.css"}]
   [:link {:rel "stylesheet" :href "/css/components/recipe.css"}]
   [:link {:rel "stylesheet" :href "/css/components/tabs.css"}]
   [:link {:rel "stylesheet" :href "/css/components/solver.css"}]
   [:style "@layer reset,skeleton,components,pages;"]])

(defn home []
  [:div [:h2 "Home"]])

(defn home-container []
  (let [subs []]
    (fn []
      [home])))

(defn calculator-container []
  (let [subs []]
    (fn []
      [c/calculator])))

(defn research-container []
  (let [subs []]
    (fn []
      [r/research])))

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

(defn navigation-container [current-page]
  [navigation current-page
   (fn [new-page] (re-frame/dispatch [::change-page new-page]))])

(defn content [current-page]
  (case current-page
    :dsp-ui/home [home-container]
    :dsp-ui/calculator [calculator-container]
    :dsp-ui/research [research-container]
    [:h2 (str "Unknown page: " (pr-str current-page))]))

(defn setup! []
  (re-frame/reg-event-db
   ::change-page
   (fn-traced
    change-page-event
    [app-db [_ new-page]]
    (assoc app-db ::page new-page)))

  (re-frame/reg-sub
   ::page
   (fn [db _]
     (::page db))))
