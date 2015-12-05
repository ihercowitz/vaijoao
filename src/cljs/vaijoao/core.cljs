(ns vaijoao.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

;; -------------------------
;; Views

(defn home-page []
  [:div {:class "container"} 
   [:div {:class "content"} [:h2 "Welcome to VaiJoao \\o/"]
   [:div [:a {:href "/new"} "NEW GAME"] " | " [:a {:href "/join"} "JOIN GAME"]]]])

(defn about-page []
  [:div [:h2 "About vaijoao"]
   [:div [:a {:href "/"} "go to the home page"]]])

   (defn join-input [value]
  [:input {:type "text"
           :value @value
           :on-change #(reset! value (-> % .-target .-value))}])

(defn join-page []
(let [val (reagent/atom "teste")]
 (fn []
 [:div {:class "container"} 
   [:div {:class "content"} [:h2 "Welcome to VaiJoao \\o/"]
    [:label {:for "id_textfield"} "Room ID: "]
   [join-input val][:div {:class "actions"} [:a {:class "styled-button-11" :href "/"} "CANCEL"][:a {:href (str "/game/"@val) :class "styled-button-11"} "JOIN GAME"]]]])))

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/about" []
  (session/put! :current-page #'about-page))

  (secretary/defroute "/join" []
  (session/put! :current-page #'join-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!)
  (accountant/dispatch-current!)
  (mount-root))
