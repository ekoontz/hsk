(ns hsk.flat2seq)
(import '(org.apache.hadoop.conf Configuration))
(import '(org.apache.hadoop.mapred FileInputFormat FileOutputFormat JobClient JobConf
                                   TextInputFormat SequenceFileOutputFormat))
(import '(org.apache.hadoop.mapred.lib IdentityMapper IdentityReducer))
(import '(org.apache.hadoop.fs Path FsShell))
(import '(org.apache.hadoop.io Text LongWritable))
(use 'clojure.tools.logging)

(gen-class
 :name "hsk.flat2seq.Tool"
 :extends "org.apache.hadoop.conf.Configured"
 :implements ["org.apache.hadoop.util.Tool"]
 :prefix ""
 :main true)

(defn run [^hsk.flat2seq.Tool this args]
  (info "Conversion of flat to sequence files has begun.")
  (doto (JobConf. (org.apache.hadoop.conf.Configuration.) (.getClass this))
    (.setJobName "Flat to Sequence File Converter")
    ;; TODO: how to set version programmatically?
    (.setJar "hsk-1.0.0-SNAPSHOT.jar")
    (.setMapperClass (Class/forName "org.apache.hadoop.mapred.lib.IdentityMapper"))
    (.setReducerClass (Class/forName "org.apache.hadoop.mapred.lib.IdentityReducer"))
    (.setNumReduceTasks 0)
    (.setOutputKeyClass LongWritable)
    (.setOutputValueClass Text)
    (.setInputFormat TextInputFormat)
    (.setOutputFormat SequenceFileOutputFormat)
    (FileInputFormat/setInputPaths (first args))
    (FileOutputFormat/setOutputPath (Path. (second args)))
    (JobClient/runJob))
  (info "Conversion of flat to sequence files has finished.")
  0)

(defn tool-run [^hsk.flat2seq.Tool this args]
  "public wrapper for (run), beccause the latter is not public."
  (run this args))

(defn main [& args]
  (do
    (System/exit
     (org.apache.hadoop.util.ToolRunner/run 
      (org.apache.hadoop.conf.Configuration.)
      (. (Class/forName "hsk.flat2seq.Tool") newInstance)
      (into-array String args)))))

