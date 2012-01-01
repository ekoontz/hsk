(ns hsk.test.core
  (:use [hsk.core])
  (:use [hsk.log])
  (:use [hsk.flat2seq])
  (:use [hsk.sequencefile])
  (:use [clojure.test]))
(import '[cascalog WriterOutputStream])
(import '[hsk.flat2seq FromRepl])

(enable-logging)

(deftest seq-file
  (let [filename "/mytest.seq"]
    (is (= 0 (write-to-file filename)))
    (is (= 0 (read-from-file filename)))))

(deftest flat2seq
  (let [flat-files "hdfs://localhost:9000/hd-in/"
        seq-files "hdfs://localhost:9000/hd-out/"
        from-repl (FromRepl.)]
    (-run-from-repl from-repl flat-files seq-files)
    (is true)))
