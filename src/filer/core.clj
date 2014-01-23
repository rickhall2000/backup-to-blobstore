(ns filer.core
  (:require [filer.config :as config]
            [filer.blobstore :as store])
  (:import  [java.io File FileInputStream FileOutputStream]))

(def directory (clojure.java.io/file config/root-folder))
(def files (filter #(.isFile %) (file-seq directory)))

(defn get-info [file]
  {:last-mod (.lastModified file)
     :fullname (.toString file)})

(def work-list
  (map get-info (filter #(.isFile %) files)))

(def avg-date
  (/ (reduce +
             (map :last-mod work-list))
     (count work-list)))

(def new-list
  (filter (fn [x] (< (:last-mod x) avg-date)) work-list))

(def container (store/container config/conn-str "mycontainer"))
