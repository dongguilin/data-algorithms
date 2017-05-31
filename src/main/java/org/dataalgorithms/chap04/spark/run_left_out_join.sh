#!/bin/bash
export SPARK_HOME=/app/spark2.1
export SPARK_MASTER=spark://ubuntu:7077
BOOK_HOME=/opt/data/ideaprojects/data-algorithms
APP_JAR=$BOOK_HOME/target/data-algorithms-1.0-SNAPSHOT.jar
USERS=/data/secondary_sort/input/chap04/users.tsv
TRANSACTIONS=/data/secondary_sort/input/chap04/transactions.tsv
#在一个Spark集群上运行
prog=org.dataalgorithms.chap04.spark.SparkLeftOuterJoin
$SPARK_HOME/bin/spark-submit \
    --class $prog \
    --master $SPARK_MASTER \
    --executor-memory 1G \
    --total-executor-cores 2 \
    $APP_JAR $USERS $TRANSACTIONS