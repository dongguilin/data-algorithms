#/bin/bash
export SPARK_HOME=/app/spark2.1
export SPARK_MASTER=spark://ubuntu:7077
BOOK_HOME=/opt/data/ideaprojects/data-algorithms
APP_JAR=$BOOK_HOME/target/data-algorithms-1.0-SNAPSHOT.jar
INPUT=/data/secondary_sort/input/chap01/timeseries.txt
OUTPUT=/data/secondary_sort/output/chap01/sparkwithlambda/combinebykey
$HADOOP_HOME/bin/hdfs dfs -rm -r $OUTPUT
#在Spark独立集群上运行
prog=org.dataalgorithms.chap01.sparkwithlambda.SecondarySortUsingGroupByKey
$SPARK_HOME/bin/spark-submit \
    --class $prog \
    --master $SPARK_MASTER \
    --executor-memory 2G \
    --total-executor-cores 20 \
    $APP_JAR \
    $INPUT $OUTPUT

