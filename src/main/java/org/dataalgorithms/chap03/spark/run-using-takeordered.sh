#/bin/bash
export JAVA_HOME=/app/jdk
export SPARK_HOME=/app/spark2.1
export SPARK_MASTER=spark://ubuntu:7077
export SCALA_HOME=/app/scala-2.11.11
BOOK_HOME=/opt/data/ideaprojects/data-algorithms
APP_JAR=$BOOK_HOME/target/data-algorithms-1.0-SNAPSHOT.jar
INPUT=/data/secondary_sort/input/chap03/file
TOPN=5
OUTPUT=/data/secondary_sort/output/chap03/file
$HADOOP_HOME/bin/hdfs dfs -rm -r $OUTPUT
prog=org.dataalgorithms.chap03.spark.Top10UsingTakeOrdered
#在Spark独立集群上运行
$SPARK_HOME/bin/spark-submit \
    --class $prog \
    --master $SPARK_MASTER \
    --executor-memory 2G \
    --total-executor-cores 20 \
    $APP_JAR $INPUT $TOPN $OUTPUT