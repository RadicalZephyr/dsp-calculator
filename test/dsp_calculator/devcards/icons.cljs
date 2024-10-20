(ns dsp-calculator.devcards.icons
  (:require [clojure.tools.reader.edn :as edn]
            [devcards.core :as dc]
            [reagent.core :as reagent]
            [dsp-calculator.data :as dsp-data]
            [spade.core :refer [defattrs]]
            [ajax.core :refer [GET]]
            [ajax.edn]
            [clojure.string :as str])
  (:require-macros
   [devcards.core :as dc :refer [defcard defcard-rg]]))

(declare icons)

(defn receive-edn [ratom response]
  (reset! ratom response))

(defn error-handler [type {:keys [status status-text]}]
  (.log js/console (str "Error fetching " type ". " status " " status-text)))

(defattrs icons-attrs []
  {:background "black"
   :display "grid"
   :grid-template-columns "repeat(auto-fill, minmax(80px, 1fr))"
   :place-items "center"
   :grid-gap "20px"
   :padding "30px"})

(defn icons-list [type xs]
  [:div (icons-attrs)
   (for [x xs]
     ^{:key (:id x)} [:div.icon {:data-icon (str type "." (:id x))}])])

(defn fetch-label [type ratom]
  (when (not (seq @ratom))
    (GET (str "/data/" type ".edn")
      {:handler #(receive-edn ratom %)
       :error-handler #(error-handler type %)
       :response-format (ajax.edn/edn-response-format)})
    [:span (str "Fetching " type)]))

(defcard-rg icons-css
  "This includes the css for displaying icons into the page."
  [:div
   [:link {:rel "stylesheet" :href "/css/dev-icons.css"}]])

(def valid-recipe-icons
  #{16 29 32 35 54 58 61 62 69 74 79 100 115 121})

(defcard-rg recipe-icons
  (fn [recipes _]
    [:div
     [fetch-label "recipes" recipes]
     [icons-list "recipe" (filter #(valid-recipe-icons (:id %))@recipes)]])
  (reagent/atom []))

(defcard-rg item-icons
  (fn [items _]
    [:div
     [fetch-label "items" items]
     [icons-list "item" @items]])
  (reagent/atom []))

(defcard-rg tech-icons
  (fn [tech _]
    [:div
     [fetch-label "tech" tech]
     [icons-list "tech" @tech]])
  (reagent/atom []))
