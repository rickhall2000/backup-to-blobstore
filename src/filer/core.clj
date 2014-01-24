(ns filer.core
  (:require [filer.config :as config]
            [filer.blobstore :as store])
  (:import  [java.io File FileInputStream FileOutputStream])
  )

(def directory (clojure.java.io/file config/root-folder))
(def files (filter #(.isFile %) (file-seq directory)))

(defn get-info [file]
  {:last-mod (.lastModified file)
     :fullname (.toString file)})

(def avg-date
  (/ (reduce +
             (map :last-mod work-list))
     (count work-list)))

(def new-list
  (filter (fn [x] (< (:last-mod x) avg-date)) work-list))

(defn upload-settings [f]
  {:file f
   :target (subs (.toString f) (inc (count config/root-folder)))})

(def container (store/container config/conn-str "mycontainer"))

(doseq [f (map upload-settings files)]
  ((:upload container) f))

(defn delete-blobs [ctr]
  (doseq [f ((ctr :blob-seq))]
    ((:delete ctr) f)))
