(ns filer.core
  (:gen-class)
  (:require [filer.config :as config]
            [filer.blobstore :as store]
            [filer.filestore :as files]))

(def container
  (store/container config/conn-str
                   (config/container-name config/root-folder)))

(defn upload-settings [f]
  {:file f
   :target (subs (.toString f) (inc (count config/root-folder)))})

(defn upload-file [file container]
  ((:upload container) (upload-settings file)))

(defn delete-blobs [ctr]
  ((:delete-container ctr)))

(defn backup-folder [folder container]
  (doseq [f (files/all-files folder)]
    (upload-file f container)))

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
     (backup-folder config/root-folder container)))
