(defproject filer "0.1.0-1"
  :description "This program backs up file system folders to windows azure blob storage."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [com.microsoft.windowsazure/microsoft-windowsazure-api "0.4.6"]
                 [clj-time "0.6.0"]]
  :main filer.core
  :aot [filer.core])
