#/bin/bash
export JAVA_HOME=/app/jdk
export SPARK_HOME=/app/spark2.1
export SCALA_HOME=/app/scala-2.11.11
export THE_SPARK_JAR=/app/spark2.1/lib/spark-assembly-1.5.0-hadoop2.6.0.jar
BOOK_HOME=/opt/data/ideaprojects/data-algorithms
APP_JAR=$BOOK_HOME/target/data-algorithms-1.0-SNAPSHOT.jar
INPUT=/data/secondary_sort/input/chap03/file
TOPN=5
OUTPUT=/data/secondary_sort/output/chap03/file
DRIVER=org.dataalgorithms.chap03.spark.Top10UsingTakeOrdered
$HADOOP_HOME/bin/hdfs dfs -rm -r $OUTPUT
#在Yarn集群上运行
$SPARK_HOME/bin/spark-submit \
    --class $DRIVER \
    --master yarn --deploy-mode cluster \
    $APP_JAR $INPUT $TOPN $OUTPUT