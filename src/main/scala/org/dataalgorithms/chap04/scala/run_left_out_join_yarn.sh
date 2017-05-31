#!/bin/bash
export SPARK_HOME=/app/spark2.1
BOOK_HOME=/opt/data/ideaprojects/data-algorithms
APP_JAR=$BOOK_HOME/target/data-algorithms-1.0-SNAPSHOT.jar
USERS=/data/secondary_sort/input/chap04/users.tsv
TRANSACTIONS=/data/secondary_sort/input/chap04/transactions.tsv
OUTPUT=/data/secondary_sort/output/chap04
#将Spark的ApplicationMaster提交到YARN的ResourceManager.
prog=org.dataalgorithms.chap04.spark.SparkLeftOuterJoin
$SPARK_HOME/bin/spark-submit \
    --class $prog \
    --master yarn \
    --deploy-mode cluster \
    --num-executors 1 \
    --driver-memory 1g \
    --executor-memory 1G \
    --executor-cores 2 \
    $APP_JAR $USERS $TRANSACTIONS $OUTPUT