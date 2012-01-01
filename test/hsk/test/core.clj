(ns hsk.test.core
  (:use [hsk.core])
  (:use [hsk.log])
  (:use [hsk.flat2seq])
  (:use [hsk.sequencefile])
  (:use [clojure.test]))
(import '[cascalog WriterOutputStream])
(import '[hsk.flat2seq FromRepl])
(import '[org.apache.hadoop.fs FsShell])
(import '[org.apache.hadoop.conf Configuration])
(use 'clojure.tools.logging)

(enable-logging)

(deftest seq-file
  (let [filename "/mytest.seq"]
    (is (= 0 (write-to-file filename)))
    (is (= 0 (read-from-file filename)))))

(deftest flat2seq
  (let [flat-files "hdfs://localhost:9000/hd-in/"
        seq-files "hdfs://localhost:9000/hd-out/"
        from-repl (FromRepl.)
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
    (-run-from-repl from-repl flat-files seq-files)
    (is true)))
