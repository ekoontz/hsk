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
