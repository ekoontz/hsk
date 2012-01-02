(ns hsk.shell)
(import '(org.apache.hadoop.conf Configuration))
(import '(org.apache.hadoop.fs Path FsShell))
(use 'clojure.tools.logging)

;; example usage: (shell "ls hdfs://localhost:9000/")
;; note: no initial dash '-' character before command, as with
;; the hadoop command line tool.
(defn shell [command]
  (.run (FsShell. (Configuration.)) (.split (str "-" command) " ")))
