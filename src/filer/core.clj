(ns filer.core
  (:require [filer.config :as config]
            [filer.blobstore :as store])
  (:import  [java.io File FileInputStream FileOutputStream]
            [com.microsoft.windowsazure.services.blob.client CloudBlockBlob])
  )

(def directory (clojure.java.io/file config/root-folder))
(def files (filter #(.isFile %) (file-seq directory)))

(defn get-info [file]
  {:last-mod (.lastModified file)
     :fullname (.toString file)})

(defn upload-settings [f]
  {:file f
   :target (subs (.toString f) (inc (count config/root-folder)))})



(def avg-date
  (/ (reduce +
             (map :last-mod work-list))
     (count work-list)))

(def new-list
  (filter (fn [x] (< (:last-mod x) avg-date)) work-list))

(def container (store/container config/conn-str "mycontainer"))

(doseq [f (map upload-settings files)]
  ((:upload container) f))

(def on-server ((container :blob-seq)))

(def on-server ((container :list-blobs) ))
on-server



(def ctr (container :ctr))

(container :ctr)

(.listBlobs ctr)

(map #(type %) on-server)

(doseq [f  (filter #(instance? CloudBlockBlob %)  on-server)]
  ((:delete container) f))

(count on-server)
