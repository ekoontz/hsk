# hsk: Hadoop Starter Kit for Clojure. 

Based on clojure-hadoop by Stuart Sierra (https://github.com/stuartsierra/clojure-hadoop).

## Example Usage

### Unit Tests

    lein clean, jar, test

There is also a Makefile : running "make test" runs the above plus
hadoop command-line shell tests.

You must make sure to run "lein jar" before you run "lein test"
because the tests need the jar file so that they can run through the
Hadoop framework.

### HDFS Shell integration

    $ lein repl
    => (load "shell")
    => (ns hsk.shell)
    => (shell "ls file:///tmp")
    => (shell "mkdir hdfs://localhost:9000/foo")

### Mapreduce integration

    $ lein repl
    => (ns myns (:use [hsk.shell][hsk.logging][hsk.wordcount]))
    => (import '[hsk.wordcount Tool])
    => (shell "rmr file:///tmp/wordcount-out")
    => (tool-run (Tool.) (list "file:///tmp/wordcount-in" "file:///tmp/wordcount-out"))
    => (shell "ls file:///tmp/wordcount-out")
    => (shell "cat file:///tmp/wordcount-out/part-00000")

You may change from a local development URL like "file:///tmp" in the
above to a fully-distributed URL like "hdfs://mycluster.com" and
everything should work the same.

For example:

    => (tool-run (hsk.wordcount.Tool.) (list "hdfs://localhost:9000/wordcount-in" "hdfs://localhost:9000/wordcount-out"))

## License

Copyright (C) 2011

Distributed under the Eclipse Public License, the same as Clojure.
