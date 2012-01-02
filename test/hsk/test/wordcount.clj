(ns hsk.test.wordcount
  (:use [clojure.test])
  (:use [clojure.tools.logging])
  (:use [hsk.logging])
  (:use [hsk.wordcount]))
(import '[cascalog WriterOutputStream])
(import '[hsk.wordcount Tool])
(import '[org.apache.hadoop.fs FsShell])
(import '[org.apache.hadoop.conf Configuration])

(deftest wordcount
  (let [flat-files "file:///tmp/wordcount-in"
        wc-out "file:///tmp/wordcount-out"
        fs-shell (FsShell. (Configuration.))]

    ;; populate input directory with data.
    (info "removing input directory: " flat-files)
    (.run fs-shell (.split (str "-rmr " flat-files) " "))
    (.run fs-shell (.split (str "-mkdir " flat-files) " "))
    (.run fs-shell (.split (str "-copyFromLocal sample/flat/access_log " flat-files) " "))
    
    ;; remove existing output directory: will be re-created as part of job.
    (info "removing output directory: " wc-out)
    (.run fs-shell (.split (str "-rmr " wc-out) " "))
    (info "running job..")
    (tool-run (Tool.) (list flat-files wc-out))
    (is true)))
