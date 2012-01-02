;; c.f. Hadoop, The Definitive Guide, p 117.
(ns hsk.sequencefile)

(import '(org.apache.hadoop.fs FileSystem Path))
(import '(org.apache.hadoop.io LongWritable))
(import '(org.apache.hadoop.mapred OutputCollector))
(import '(org.apache.hadoop.io SequenceFile Text IntWritable IOUtils))
(import '(org.apache.log4j.spi RootLogger))
(import '(org.apache.log4j SimpleLayout WriterAppender))
(import '(org.apache.hadoop.conf Configuration))

(use 'clojure.tools.logging)

(defn get-writer [filename]
  (let [conf (Configuration.)
        fs (FileSystem/get conf)]
    (let [writer (SequenceFile/createWriter fs conf (Path. filename)
                                            (class (IntWritable.))
                                            (class (Text.)))]
      writer)))

(defn finish-writer [writer]
  (IOUtils/closeStream writer))

;; TODO: move this fn from hsk.sequencefile to hsk.test.sequencefile.
(defn write-to-file [filename]
  (info "writing to sequence file..")
  (let [writer (get-writer filename)]
    (try (let [data ["One, two, buckle my shoe",
                     "Three, four, shut the door",
                     "Five, six, pick up sticks",
                     "Seven, eight, lay them straight",
                     "Nine, ten, a big fat hen"]]
           (doseq [i (range 0 100)]
             (let [my-key (IntWritable. (- 100 i))
                   my-val (Text. (nth data (mod i (.length data))))]
;               (info (str "i: " i "; key: " my-key "; val: " my-val))
               (.append writer my-key my-val)))
           (info "done writing file.")
           (IOUtils/closeStream writer)
           0)
         (catch Exception e 
           (do
             (error (str "caught exception while writing sequence file: " e))
             (IOUtils/closeStream writer)
             1)))))

(defn read-from-file [filename]
  (info "start reading from file..")
  0)


  

