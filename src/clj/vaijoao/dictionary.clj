(ns vaijoao.dictionary
  (:require [clojure.java.io :as io])
  (:import [java.util.zip ZipFile GZIPInputStream]))

(def URL "http://mirror.ctan.org/systems/win32/winedt/dict/")

(defn- read-dict [lang]
  (-> (str "dicts/" lang ".zip") 
      io/resource
      io/file))

(defn download-dict [lang]
  (let [url (str URL lang ".zip")
        save-to (str "resources/dict/" lang ".zip")]
    (with-open [in  (io/input-stream (io/as-url url))]
      (io/copy in (java.io.File. (str "resources/dicts/" lang ".zip")))))
  

  (defn- unzip [zip-file dict]
    (let [zip (ZipFile. zip-file)
          read-zip (fn [x] (.getInputStream zip x))]
      (first (for [f (enumeration-seq (.entries zip))
                   :when (= (str dict ".dic") (clojure.string/lower-case (.getName f)))]
               (-> f
                   read-zip
                   io/reader))))))

(defn open-dict [lang]
  (if-let [dict (read-dict lang)]
    dict
    (do
      (download-dict lang)
      (recur lang))))

(defn valid-word? [word lang]
  (let [dict (open-dict lang)]
    (not 
     (empty? 
      (filter #(= word %) (line-seq (unzip dict lang)))))))
