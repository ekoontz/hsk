(ns hsk.test.flat2seq
  (:use [clojure.test])
  (:use [clojure.tools.logging])
  (:use [hsk.logging])
  (:use [hsk.flat2seq])
  (:use [hsk.shell]))
(import '[cascalog WriterOutputStream])
(import '[hsk.flat2seq Tool])
(import '[org.apache.hadoop.fs FsShell])
(import '[org.apache.hadoop.conf Configuration])

(deftest flat2seq
  (let [flat-files "file:///tmp/flat2seq-in/"
        seq-files "file:///tmp/flat2seq-out/"
        fs-shell (FsShell. (Configuration.))]

    ;; populate input directory with data.
    (info "removing input directory: " flat-files)
    (.run fs-shell (.split (str "-rmr " flat-files) " "))
    (.run fs-shell (.split (str "-mkdir " flat-files) " "))
    (.run fs-shell (.split (str "-copyFromLocal sample/flat/access_log " flat-files) " "))
    
    ;; remove existing output directory: will be re-created as part of job.
    (info "removing output directory: " seq-files)
    (.run fs-shell (.split (str "-rmr " seq-files) " "))
    (info "running job..")
    (tool-run (Tool.) (list flat-files seq-files))
    (is true)))

