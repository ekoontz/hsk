(ns hsk.test.sequencefile
  (:use [clojure.test])
  (:use [clojure.tools.logging])
  (:use [hsk.sequencefile])
  (:use [hsk.logging]))
(import '[cascalog WriterOutputStream])
(import '[org.apache.hadoop.fs FsShell])
(import '[org.apache.hadoop.conf Configuration])

(deftest seq-file
  (let [filename "/mytest.seq"]
    (is (= 0 (write-to-file filename)))
    (is (= 0 (read-from-file filename)))))
