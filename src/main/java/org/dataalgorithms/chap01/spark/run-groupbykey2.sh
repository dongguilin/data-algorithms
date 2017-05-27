#/bin/bash
export SPARK_HOME=/app/spark2.1
export SPARK_MASTER=spark://ubuntu:7077
BOOK_HOME=/opt/data/ideaprojects/data-algorithms
APP_JAR=$BOOK_HOME/target/data-algorithms-1.0-SNAPSHOT.jar
INPUT=/data/secondary_sort/input/chap01/timeseries.txt
OUTPUT=/data/secondary_sort/output/chap01/spark/groupbykey2
$HADOOP_HOME/bin/hdfs dfs -rm -r $OUTPUT
#在Spark独立集群上运行
prog=org.dataalgorithms.chap01.spark.SecondarySortUsingGroupByKey2
$SPARK_HOME/bin/spark-submit \
    --class $prog \
    --master $SPARK_MASTER \
    --num-executors 3 \
    --driver-memory 1g \
    --executor-memory 1g --executor-cores 1 \
    $APP_JAR \
    $INPUT $OUTPUT

