(ns hsk.test.wordcount
  (:use [hsk.core])
  (:use [hsk.log])
  (:use [hsk.wordcount])
  (:use [hsk.sequencefile])
  (:use [clojure.test]))
(import '[cascalog WriterOutputStream])
(import '[hsk.wordcount Tool])
(import '[org.apache.hadoop.fs FsShell])
(import '[org.apache.hadoop.conf Configuration])
(use 'clojure.tools.logging)

(deftest wordcount
  (let [flat-files "file:///tmp/wordcount-in"
        wc-out "file:///tmp/wordcount-out"
        from-repl (Tool.)
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
    (tool-run from-repl flat-files wc-out)
    (is true)))
