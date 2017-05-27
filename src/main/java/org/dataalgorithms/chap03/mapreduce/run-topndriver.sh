#/bin/bash
BOOK_HOME=/opt/data/ideaprojects/data-algorithms
APP_JAR=$BOOK_HOME/target/data-algorithms-1.0-SNAPSHOT.jar
INPUT=/data/secondary_sort/input/chap03/top10data.se
OUTPUT=/data/secondary_sort/output/chap03/mapreduce
$HADOOP_HOME/bin/hdfs dfs -rm -r $OUTPUT
PROG=org.dataalgorithms.chap03.mapreduce.TopNDriver
$HADOOP_HOME/bin/hadoop jar $APP_JAR $PROG 10 $INPUT $OUTPUT