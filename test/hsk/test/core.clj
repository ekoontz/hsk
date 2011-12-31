(ns hsk.test.core
  (:use [hsk.core])
  (:use [hsk.log])
  (:use [hsk.sequencefile])
  (:use [clojure.test]))

(import '[cascalog WriterOutputStream])

(enable-logging)


(deftest seq-file
  (let [filename "/mytest.seq"]
    (is (= 0 (write-to-file filename)))
    (is (= 0 (read-from-file filename)))))
