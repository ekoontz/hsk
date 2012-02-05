# hsk: Hadoop Starter Kit for Clojure. 

Based on clojure-hadoop by Stuart Sierra (https://github.com/stuartsierra/clojure-hadoop).

Uses Cascalog by Nathan Marz for logging support (https://github.com/nathanmarz/cascalog).

## Example Usage

### Unit Tests

    lein clean, jar, test

There is also a Makefile : running "make test" runs the above plus
hadoop command-line shell tests. Your Hadoop install must be
configured validly for "make test" to succeed: see "Hadoop
Configuration" below.

__You must make sure to run "lein jar" before you run "lein test"
because the tests need the jar file so that they can run through the
Hadoop framework.__

In case of trouble, please try the "Hadoop Configuration" section
below which will hopefully give you a failsafe Hadoop local-only
testing environment.

### HDFS shell

    $ lein repl
    => (load "hsk/shell")
    => (ns hsk.shell)
    => (shell "ls file:///tmp")
    => (shell "mkdir hdfs://localhost:9000/foo")

### MapReduce jobs

#### Setup

    $ lein repl
    => (ns myns (:use [hsk.shell][hsk.logging][hsk.wordcount]))
    => (import '[hsk.wordcount Tool])

If running in emacs with <tt>M-x clojure-jack-in</tt>, run:

    => (enable-logging-in-emacs)

#### Run

Now to run the MapReduce job:

    => (shell "rmr file:///tmp/wordcount-out")
    => (tool-run (Tool.) (list "file:///tmp/wordcount-in" "file:///tmp/wordcount-out"))
    => (shell "ls file:///tmp/wordcount-out")
    => (shell "cat file:///tmp/wordcount-out/part-00000")

You may change from a local development URL like "file:///tmp" in the
above to a fully-distributed URL like "hdfs://mycluster.com" and
everything should work the same.

For example:

    => (tool-run (Tool.) (list "hdfs://localhost:9000/wordcount-in" "hdfs://localhost:9000/wordcount-out"))

## Building Clojure

This is only necessary so that hadoop can find the clojure 1.3.0
jar. If you have the clojure 1.3.0 jar in a known path, you can simply
configure your <tt>HADOOP_CLASSPATH</tt> (see below) to point to
it. Running <tt>lein deps</tt> in the current directory
(<tt>hsk/</tt>) will attempt to fetch the clojure jar remotely and
place it in <tt>lib/</tt>.

    $ git clone https://github.com/clojure/clojure.git
    $ cd clojure
    $ git checkout clojure-1.3.0
    $ mvn clean install

The last command installs the newly-built clojure jar in
<tt>$HOME/.m2/repository/org/clojure/clojure/1.3.0/clojure-1.3.0.jar</tt>,
which is used to configure Hadoop in the following section.

## Hadoop Configuration

    $ git clone http://github.com/apache/hadoop-common.git
    $ git checkout branch-1.0
    $ ant clean jar
    $ mvn install:install-file -DgroupId=org.apache -DartifactId=hadoop-core -Dversion=1.0.1 -Dpackaging=jar -Dfile=build/hadoop-core-1.0.1-SNAPSHOT.jar

The last command installs the newly-built Hadoop jar so that leiningen can find it.

Modify <tt>conf/hadoop-env.sh</tt> like so:

    export HADOOP_CLASSPATH=$HOME/.m2/repository/org/clojure/clojure/1.3.0/clojure-1.3.0.jar

Modify <tt>conf/core-site.xml</tt> like so:

    <configuration>
      <property>
          <name>fs.default.name</name>
          <value>hdfs://localhost:9000</value>
      </property>
    </configuration>

Modify <tt>conf/mapred-site.xml</tt> like so:
 
    <configuration>
      <property>
        <name>mapred.job.tracker</name>
        <value>localhost:9001</value>
      </property>
    </configuration>

And format your new HDFS filesystem:

    hadoop namenode -format

Now start all four Hadoop daemons:

    hadoop namenode &
    hadoop datanode &
    hadoop jobtracker &
    hadoop tasktracker &

## License

Copyright (C) 2011

Distributed under the Eclipse Public License, the same as Clojure.
