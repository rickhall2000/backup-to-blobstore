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

(fn [x] x)

(defn get-destination [blob folder]
  (files/ms-name
   (str folder "/" (.getName blob))))

(defn download-settings [blob folder]
  {:blob blob
   :target (get-destination blob folder)})

(defn restore-folder [folder container]
  (doseq [f ((:blob-seq container))]
    ((:download container) (download-settings f folder))))

(defn -main [& args]
  (cond
   (= "delete" (first args))
     (delete-blobs (store/container config/conn-str (second args)))
   (= "restore" (first args))
    (restore-folder config/restore-folder (store/container config/conn-str (second args)))
   :default
     (doseq [p config/back-folders]
       (backup-folder p (make-container p)))))
