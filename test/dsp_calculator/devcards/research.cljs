(ns dsp-calculator.devcards.research
  (:require
    [reagent.core :as reagent]
    [spade.core :refer [defattrs]]
    [dsp-calculator.ui.research :as r])
  (:require-macros
   [devcards.core :as dc :refer [defcard defcard-rg]]))

(defcard "# Research

The research tab lets the user select the technologies that they have
researched so far. Recipes that have not been researched will not
appear in the recipe picker, alternative recipes that are not
available will not show up under the alternates tab, and mining
production speed is taken into account for calculating the number of
mineral veins that need to be used.")

(def em-matrix-tech
  {"id" 1002,
   "epoch" 6000,
   "name" "Electromagnetic Matrix",
   "position" [13 1],
   "iconPath" "Icons/Tech/1002",
   "propertyItemCounts" [40],
   "preTechs" [1001],
   "items" [1202 1301],
   "itemPoints" [20 20],
   "addItems" [2901],
   "addItemCounts" [1],
   "unlockRecipes" [9 10],
   "propertyOverrideItems" [6001],
   "hashNeeded" 1800,
   "description"
   "The Electromagnetic Matrix is the foundation of all technologies as one of the five basic source codes for CentreBrain to maintain Homeland simulation. Upload Electromagnetic Matrices to unlock more technologies.",
   "conclusion"
   "You've unlocked the Matrix Lab! Also called the CentreBrain Connector, it can be used to unlock new technologies by uploading matrices to it.",
   "published" true})

(defattrs tech-bg-attrs []
  {:background-color "black"
   :padding "20px"})

(defcard-rg tech-square
  (fn [researched _]
    [:div (tech-bg-attrs)
     [:link {:rel "stylesheet" :href "/css/icons.css"}]
     [r/technology em-matrix-tech @researched (fn [] (swap! researched not))]])
  (reagent/atom false))
