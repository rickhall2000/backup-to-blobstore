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
    {:upload (fn [{:keys [file target]}]
               (let [blob-ref (.getBlockBlobReference ctr target)]
                 (.upload blob-ref (FileInputStream. file) (.length file))))

     :download (fn [{:keys [blob target]}]
                 (do
                   (.mkdirs (.getParentFile (File. target)))
                   (with-open [w (FileOutputStream. target)]
                   (.download blob w))))


     :find-blob (fn [blobname]
                  (.getBlockBlobReference ctr blobname))

     :delete (fn [blob]
               (.delete blob))

     :remove-container (fn []
                         (.delete ctr))

     :blob-seq (fn []
                 (filter #(instance? CloudBlockBlob %)
                  (tree-seq
                   (fn [f] (not (instance? CloudBlockBlob f)))
                   (fn [f] (.listBlobs f))
                   ctr)))

     :list-blobs (fn []
                   (filter
                    #(instance? CloudBlockBlob %)
                    (.listBlobs ctr)))

     :delete-container (fn []
                         (.delete ctr))


     }))
