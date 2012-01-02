(ns hsk.wordcount
  (:gen-class))

(import '(java.util StringTokenizer))
(import '(org.apache.commons.logging Log LogFactory))
(import '(org.apache.hadoop.mapred FileInputFormat FileOutputFormat JobClient JobConf Mapper MapReduceBase
                                   OutputCollector Reducer TextInputFormat SequenceFileOutputFormat TextOutputFormat))
(import '(org.apache.hadoop.mapred.lib IdentityMapper IdentityReducer))
(import '(org.apache.hadoop.fs Path))
(import '(org.apache.hadoop.io Text LongWritable))
(import '(org.apache.log4j.spi RootLogger))
(import '(org.apache.log4j SimpleLayout WriterAppender))
(import '(org.codehaus.jackson.map JsonMappingException))
(use 'clojure.tools.logging)

(gen-class
 :name "hsk.wordcount.mapper"
 :extends "org.apache.hadoop.mapred.MapReduceBase"
 :implements ["org.apache.hadoop.mapred.Mapper"])

(defn -map
  "'This is our implementation of the Mapper.map method.  The key and
  value arguments are sub-classes of Hadoop's Writable interface, so
  we have to convert them to strings or some other type before we can
  use them.  Likewise, we have to call the OutputCollector.collect
  method with objects that are sub-classes of Writable.'
  - https://github.com/stuartsierra/clojure-hadoop/blob/master/src/examples/clojure/clojure_hadoop/examples/wordcount1.clj#L38 "
  [this key line ^OutputCollector output reporter]
  (doseq [word (enumeration-seq (StringTokenizer. (str line)))]
    (.collect output (Text. word) (LongWritable. 1))))

(gen-class
 :name "hsk.wordcount.reducer"
 :extends "org.apache.hadoop.mapred.MapReduceBase"
 :implements ["org.apache.hadoop.mapred.Reducer"])

(defn -reduce 
  "'This is our implementation of the Reducer.reduce method.  The key
  argument is a sub-class of Hadoop's Writable, but 'values' is a Java
  Iterator that returns successive values.  We have to use
  iterator-seq to get a Clojure sequence from the Iterator.  

  Beware, however, that Hadoop re-uses a single object for every
  object returned by the Iterator.  So when you get an object from the
  iterator, you must extract its value (as we do here with the 'get'
  method) immediately, before accepting the next value from the
  iterator.  That is, you cannot hang on to past values from the
  iterator.'
  - https://github.com/stuartsierra/clojure-hadoop/blob/master/src/examples/clojure/clojure_hadoop/examples/wordcount1.clj#L47 "
  [this key values #^OutputCollector output reporter]
  (let [sum (reduce + (map (fn [#^LongWritable v] (.get v)) (iterator-seq values)))]
    (.collect output key (LongWritable. sum))))

(gen-class
 :name "hsk.wordcount.Tool"
 :extends "org.apache.hadoop.conf.Configured"
 :prefix ""
 :implements ["org.apache.hadoop.util.Tool"]
 :constructors {[] []}
 :init create)

(defn create []
  (info "Constructed hadoop job tool."))

(defn tool-run [this ^String input-dir ^String output-dir]
  (info "Wordcount is running.")
  (doto (JobConf. (org.apache.hadoop.conf.Configuration.) (.getClass this))
    (.setJobName "Identity")
    ;; TODO: how to set version programmatically?
    (.setJar "hsk-1.0.0-SNAPSHOT.jar")
    (.setMapperClass (Class/forName "hsk.wordcount.mapper"))
    (.setReducerClass (Class/forName "hsk.wordcount.reducer"))
    (.setOutputKeyClass Text)
    (.setOutputValueClass LongWritable)
    (.setInputFormat TextInputFormat)
    (.setOutputFormat TextOutputFormat)
    (FileInputFormat/setInputPaths input-dir)
    (FileOutputFormat/setOutputPath (Path. output-dir))
    (JobClient/runJob)))

(gen-class
 :name "hsk.wordcount.ctool"
 :extends "org.apache.hadoop.conf.Configured"
 :implements ["org.apache.hadoop.util.Tool"]
 :main true)

(defn -run [^org.apache.hadoop.util.Tool this args]
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
      (. (Class/forName "hsk.wordcount.ctool") newInstance)
      (into-array String args)))))

