#!/bin/bash
SPARK_HOME=/app/spark2.1
BOOK_HOME=/opt/data/ideaprojects/data-algorithms
APP_JAR=$BOOK_HOME/target/data-algorithms-1.0-SNAPSHOT.jar
SPARK_MASTER=spark://ubuntu:7077
WINDOW=3
INPUT=/data/secondary_sort/input/chap06
OUTPUT=/data/secondary_sort/output/chap06
driver=org.dataalgorithms.chap06.scala.MovingAverageInMemory
hdfs dfs -rm -r $OUTPUT
# Run on a Spark standalone cluster in client deploy mode
$SPARK_HOME/bin/spark-submit \
    --class $driver \
    --conf spark.sql.map.partitions=1 --conf spark.sql.shuffle.partitions=1 --conf spark.sql.reduce.partitions=1 \
    --master $SPARK_MASTER \
    --executor-memory 600m \
    --total-executor-cores 1 \
    $APP_JAR $WINDOW $INPUT $OUTPUT