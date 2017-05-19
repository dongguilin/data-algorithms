#/bin/bash
export SPARK_HOME=/app/spark1.5
export SPARK_MASTER=spark://ubuntu:7077
BOOK_HOME=/opt/data/ideaprojects/data-algorithms
APP_JAR=$BOOK_HOME/target/data-algorithms-1.0-SNAPSHOT.jar
INPUT=/data/secondary_sort/input
OUTPUT=/data/secondary_sort/output
#在Spark独立集群上运行
prog=org.dataalgorithms.chap01.scala.SecondarySort
$SPARK_HOME/bin/spark-submit \
    --class $prog \
    --master $SPARK_MASTER \
    --executor-memory 2G \
    --total-executor-cores 20 \
    $APP_JAR \
    1 $INPUT $OUTPUT

