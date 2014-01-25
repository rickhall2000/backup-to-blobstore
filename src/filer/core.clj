(ns filer.core
  (:gen-class)
  (:require [filer.config :as config]
            [filer.blobstore :as store]
            [filer.filestore :as files]))

(def container (store/container config/conn-str "mycontainer"))

(defn upload-settings [f]
  {:file f
   :target (subs (.toString f) (inc (count config/root-folder)))})

(defn upload-file [file container]
  ((:upload container) (upload-settings file)))

(defn delete-blobs [ctr]
  (doseq [f ((ctr :blob-seq))]
    ((:delete ctr) f)))

(defn backup-folder [folder container]
  (doseq [f (files/all-files folder)]
    (upload-file f container)))

(defn -main [& args]
  (cond
   (= "delete" (first args)) (delete-blobs container)
   :default
   (backup-folder config/root-folder container)))
