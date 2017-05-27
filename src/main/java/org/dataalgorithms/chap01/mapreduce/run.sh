#/bin/bash
BOOK_HOME=/opt/data/ideaprojects/data-algorithms
APP_JAR=$BOOK_HOME/target/data-algorithms-1.0-SNAPSHOT.jar
INPUT=/data/secondary_sort/input/chap01/sample_input.txt
OUTPUT=/data/secondary_sort/output/chap01/mapreduce
$HADOOP_HOME/bin/hdfs dfs -rm -r $OUTPUT
PROG=org.dataalgorithms.chap01.mapreduce.SecondarySortDriver
$HADOOP_HOME/bin/hadoop jar $APP_JAR $PROG $INPUT $OUTPUT

