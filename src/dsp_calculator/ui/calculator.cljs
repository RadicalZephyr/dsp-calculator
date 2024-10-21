(ns dsp-calculator.ui.calculator)

(defn calculator []
  [:main.page.calculator
   ])

(defn empty-selector []
  [:div.recipe-picker
   [:div.icon {:data-icon "ui.select-recipe" :title "Select a recipe"}]
   [:span.hint "Please select a recipe"]])

(defn selected-recipe [selected]
  [:span.recipe.icon {:data-icon (str "item." (:id selected))
                      :title (:name selected)}])

(defn combo-selector [selected]
  [:div.combo-selector
   (if selected
     [:div.recipe-picker
      [selected-recipe selected]]
     [empty-selector])])
