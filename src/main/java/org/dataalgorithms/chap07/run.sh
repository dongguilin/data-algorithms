#!/bin/bash
BOOK_HOME=/opt/data/ideaprojects/data-algorithms
APP_JAR=$BOOK_HOME/target/data-algorithms-1.0-SNAPSHOT.jar
PAIR_SIZE=2
INPUT=/data/secondary_sort/input/chap07
OUTPUT=/data/secondary_sort/output/chap07
driver=org.dataalgorithms.chap07.mapreduce.MBADriver
hdfs dfs -put -f $APP_JAR /lib/
hadoop jar $APP_JAR $driver \
    $INPUT $OUTPUT $PAIR_SIZE