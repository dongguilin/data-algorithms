#!/bin/bash
SPARK_HOME=/app/spark2.1
BOOK_HOME=/opt/data/ideaprojects/data-algorithms
APP_JAR=$BOOK_HOME/target/data-algorithms-1.0-SNAPSHOT.jar
WINDOW=3
INPUT=/data/secondary_sort/input/chap06
OUTPUT=/data/secondary_sort/output/chap06
driver=org.dataalgorithms.chap06.scala.MovingAverageInMemory
hdfs dfs -rm -r $OUTPUT
# Run on a YARN cluster
$SPARK_HOME/bin/spark-submit \
    --class $driver \
    --conf spark.yarn.jars=hdfs://ubuntu:9000/spark-lib/spark-yarn_2.11-2.1.0.jar \
    --jars hdfs://ubuntu:9000/lib/data-algorithms.jar \
    --master yarn \
    --deploy-mode client \
    --executor-memory 600m \
    --total-executor-cores 1 \
    $APP_JAR $WINDOW $INPUT $OUTPUT