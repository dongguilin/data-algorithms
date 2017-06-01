#!/bin/bash
SPARK_HOME=/app/spark2.1
SPARK_MASTER=spark://ubuntu:7077
BOOK_HOME=/opt/data/ideaprojects/data-algorithms
APP_JAR=$BOOK_HOME/target/data-algorithms-1.0-SNAPSHOT.jar
WINDOW=2
INPUT=/data/secondary_sort/input/chap05
OUTPUT=/data/secondary_sort/output/chap05
hdfs dfs -rm -r $OUTPUT
prog=org.dataalgorithms.chap05.scala.SparkSQLRelativeFrequency
#在Spark集群上运行
$SPARK_HOME/bin/spark-submit \
    --class $prog \
    --master $SPARK_MASTER \
    --conf spark.sql.map.partitions=2 --conf spark.sql.shuffle.partitions=1 --conf spark.sql.reduce.partitions=1 \
    --executor-memory 600m \
    --driver-memory 600m \
    --total-executor-cores 4 \
    $APP_JAR $WINDOW $INPUT $OUTPUT
