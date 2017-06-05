#!/bin/bash
BOOK_HOME=/opt/data/ideaprojects/data-algorithms
APP_JAR=$BOOK_HOME/target/data-algorithms-1.0-SNAPSHOT.jar
WINDOW_SIZE=2
INPUT=/data/secondary_sort/input/chap06
OUTPUT=/data/secondary_sort/output/chap06
driver=org.dataalgorithms.chap06.secondarysort.SortByMRF_MovingAverageDriver
hdfs dfs -put -f $APP_JAR /lib/
hadoop jar $APP_JAR $driver \
    $WINDOW_SIZE $INPUT $OUTPUT