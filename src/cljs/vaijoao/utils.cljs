(ns vaijoao.utils
  (:require [goog.net.XhrIo :as xhr]
            [ajax.core :refer [GET]]))

(defn get-uuid []
  (let [process (fn [data]
                  (str data))]
    (GET "/new" {:handler process})))
