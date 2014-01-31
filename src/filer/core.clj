(ns filer.core
  (:gen-class)
  (:require [filer.config :as config]
            [filer.blobstore :as store]
            [filer.filestore :as files]))

(defn make-container [root-folder]
  (store/container config/conn-str
    (config/container-name root-folder)))

(defn upload-settings [f root-folder]
  {:file f
   :target (subs (.toString f) (inc (count root-folder)))})

(defn upload-file [file container root-folder]
  ((:upload container) (upload-settings file root-folder)))

(defn backup-folder [folder container]
  (doseq [f (files/all-files folder)]
    (upload-file f container folder)))

(defn delete-blobs [ctr]
  ((:delete-container ctr)))

(defn get-destination [blob]
  (files/ms-name
   (str config/restore-folder "/" (.getName blob))))

(defn download-settings [blob]
  {:blob blob
   :target (get-destination blob)})

(defn restore-folder [folder container]
  (doseq [f ((:blob-seq container))]
    ((:download container) (download-settings f))))

(defn -main [& args]
  (cond
   (= "delete" (first args))
     (delete-blobs (store/container config/conn-str (second args)))
   (= "restore" (first args))
    (restore-folder config/restore-folder (store/container config/conn-str (second args)))
   :default
     (doseq [p config/back-folders]
       (backup-folder p (make-container p)))))
