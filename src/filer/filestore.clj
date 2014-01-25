(ns filer.filestore
    (:import [java.io File]))

(defn all-files [directory]
  (filter #(.isFile %)
          (file-seq (clojure.java.io/file directory))))

(defn get-info [file]
  {:last-mod (.lastModified file)
     :fullname (.toString file)})
