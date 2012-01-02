.PHONY: test clean clean-output

# change following to e.g. hdfs://localhost:9000 to test an actual HDFS instance.
ROOT=file:///tmp

clean: clean-output
	lein clean

clean-output:
	-hadoop fs -rmr $(ROOT)/hd-out/

test: clean
# 1. run lein tests.
	lein jar, test
# 2. run command-line tests using the 'hadoop' command.
	-hadoop fs -rmr $(ROOT)/hd-in/
	make clean-output
	hadoop fs -mkdir $(ROOT)/hd-in/
	find sample/flat -not -type d -exec hadoop fs -copyFromLocal '{}' $(ROOT)/hd-in/ ';'
	hadoop fs -ls $(ROOT)/hd-in/
	hadoop jar hsk-1.0.0-SNAPSHOT.jar hsk.flat2seq.Tool $(ROOT)/hd-in $(ROOT)/hd-out/
	hadoop fs -ls $(ROOT)/hd-out/
	make clean-output
	hadoop jar hsk-1.0.0-SNAPSHOT.jar hsk.wordcount.Tool $(ROOT)/hd-in $(ROOT)/hd-out/
