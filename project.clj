(defproject hsk "1.0.0-SNAPSHOT"
  :description "hsk: the Hadoop Starter Kit for Clojure"
  :license {:name "Eclipse Public License"}
  ;; "These namespaces will be AOT-compiled. Needed for gen-class and
  ;; other Java interop functionality. :namespaces is an alias for this.
  ;; Put a regex here to compile all namespaces whose names match."
  ;; -https://github.com/technomancy/leiningen/blob/master/sample.project.clj#L91
  :aot [ #"hsk.*" ]
  :dependencies [[org.clojure/clojure "1.2.1"]
                 [log4j/log4j "1.2.16"]
                 [cascalog/cascalog "1.8.4"]
                 [commons-cli "1.2"]
                 [commons-configuration "1.6"]
                 [commons-logging "1.1.1"]
                 [org.clojure/tools.logging "0.2.3"]
                 [log4j "1.2.16"]
                 [junit/junit "4.7"]
                 [org.apache.hadoop/hadoop-core "1.0.1-SNAPSHOT"]
                 [org.apache.hbase/hbase "0.93-SNAPSHOT"]
                 [org.mortbay.jetty/servlet-api-2.5 "6.1.14"]
                 [org.codehaus.jackson/jackson-core-asl "1.8.2"]
                 [org.codehaus.jackson/jackson-mapper-asl "1.8.2"]])

