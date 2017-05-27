#/bin/bash
BOOK_HOME=/opt/data/ideaprojects/data-algorithms
APP_JAR=$BOOK_HOME/target/data-algorithms-1.0-SNAPSHOT.jar
#
# Generate unique(k,v) pairs
#
INPUT=/data/secondary_sort/input/chap03/top10data.txt
OUTPUT=/data/secondary_sort/input/chap03/se
hdfs dfs -rm -r $OUTPUT
AGGREGATOR=org.dataalgorithms.chap03.mapreduce.AggregateByKeyDriver
hadoop jar $APP_JAR $AGGREGATOR $INPUT $OUTPUT
#
# Find Top N
#
N=5
TOPN_INPUT=/data/secondary_sort/input/chap03/se
TOPN_OUTPUT=/data/secondary_sort/output/chap03/mapreduce
hdfs dfs -rmr $TOPN_OUTPUT
TopN=org.dataalgorithms.chap03.mapreduce.TopNDriver
hadoop jar $APP_JAR $TopN $N $TOPN_INPUT $TOPN_OUTPUT