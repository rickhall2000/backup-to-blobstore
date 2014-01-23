(ns filer.blobstore
  (:import [com.microsoft.windowsazure.services.core.storage CloudStorageAccount]
           [com.microsoft.windowsazure.services.blob.client CloudBlobClient
            CloudBlobContainer CloudBlockBlob CloudBlob BlobContainerPermissions
            BlobContainerPublicAccessType]
           [java.io File FileInputStream FileOutputStream]))

(defn container [conn-str container-name]
  (let [ctr
        (-> conn-str
            CloudStorageAccount/parse
            .createCloudBlobClient
            (.getContainerReference container-name))]
    (.createIfNotExist ctr)
    {:upload (fn [:keys [file target]]
               (let [blob-ref (.getBlockBlobReference ctr target)]
                 (.upload blob-ref (FileInputStream. file) (.length file))))

     :download (fn [:keys [blob target]]
                 (.download blob (FileOutputStream. target)))

     :find-blob (fn [blobname]
                  (.getBlockBlobReference ctr blobname))

     :delete (fn [blob]
               (.delete blob))

     :remove-container (fn []
                         (.delete ctr))

     :blob-seq (fn ([] (:blob-seq ctr))
                 ([dir]
                    (tree-seq
                     (fn [f] (complement (instance CloudBlob f)))
                     (fn [f] (.listBlobs f))
                     dir)))
     }))
