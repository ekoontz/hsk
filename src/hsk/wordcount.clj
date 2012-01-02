(ns hsk.wordcount
  (:gen-class))

(import '(java.util StringTokenizer))
(import '(org.apache.commons.logging Log LogFactory))
(import '(org.apache.hadoop.mapred FileInputFormat FileOutputFormat JobClient JobConf Mapper
                                   OutputCollector Reducer TextInputFormat SequenceFileOutputFormat TextOutputFormat))
(import '(org.apache.hadoop.mapred.lib IdentityMapper IdentityReducer))
(import '(org.apache.hadoop.fs Path))
(import '(org.apache.hadoop.io Text LongWritable))
(import '(org.apache.hadoop.util Tool))
(import '(org.apache.log4j.spi RootLogger))
(import '(org.apache.log4j SimpleLayout WriterAppender))
(import '(org.codehaus.jackson.map JsonMappingException))
(use 'clojure.tools.logging)

(gen-class
 :name "hsk.wordcount.FromRepl"
 :extends "org.apache.hadoop.conf.Configured"
 :implements ["org.apache.hadoop.util.Tool"]
 :constructors {[] []}
 :init from-repl-init)

(defn -from-repl-init []
  (info "Constructed hadoop job tool."))

(defn -run-from-repl [^Tool this ^String input-dir ^String output-dir]
  (info "Conversion of flat to sequence files is running.")
  (doto (JobConf. (org.apache.hadoop.conf.Configuration.) (.getClass this))
    (.setJobName "Identity")
    ;; TODO: how to set version programmatically?
    (.setJar "hsk-1.0.0-SNAPSHOT.jar")
    (.setMapperClass (Class/forName "org.apache.hadoop.mapred.lib.IdentityMapper"))
    (.setReducerClass (Class/forName "org.apache.hadoop.mapred.lib.IdentityReducer"))
    (.setNumReduceTasks 0)
    (.setOutputKeyClass LongWritable)
    (.setOutputValueClass Text)
    (.setInputFormat TextInputFormat)
    (.setOutputFormat SequenceFileOutputFormat)
    (FileInputFormat/setInputPaths input-dir)
    (FileOutputFormat/setOutputPath (Path. output-dir))
    (JobClient/runJob)))

(gen-class
 :name "hsk.wordcount.tool"
 :extends "org.apache.hadoop.conf.Configured"
 :implements ["org.apache.hadoop.util.Tool"]
 :main true)

(defn -run [^Tool this args]
  (info "Conversion of flat to sequence files has begun.")
  (doto (JobConf. (.getConf this) (.getClass this))
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
    (JobClient/runJob)
    )
  (println "Converted files to sequence files.")
  0)

(defn -main [& args]
  (do
    (System/exit
     (org.apache.hadoop.util.ToolRunner/run 
      (org.apache.hadoop.conf.Configuration.)
      (. (Class/forName "hsk.wordcount.tool") newInstance)
      (into-array String args)))))
