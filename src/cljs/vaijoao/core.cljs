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

(defn join-page []
 [:div {:class "container"} 
   [:div {:class "content"} [:h2 "Welcome to VaiJoao \\o/"]
    [:label {:for "id_textfield"} "Room ID: "]
   [:input {:type "text" :id "id_textfield"}] [:div {:class "actions"} [:a {:class "styled-button-11" :href "/"} "CANCEL"][:input {:type "submit" :class "styled-button-11" :value "JOIN GAME"}]]]])

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
