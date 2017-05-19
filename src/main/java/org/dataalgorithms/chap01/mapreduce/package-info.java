/**
 * This package (org.dataalgorithms.chap01.mapreduce) contains source code
 * for chapter 01 of the Data Algorithms book published by O'Reilly.
 * MapReduce/Hadoop solutions are provided here.
 *
 * @author Mahmoud Parsian
 */
package org.dataalgorithms.chap01.mapreduce;


/**
 * 输入是一组文件，其中每个记录（行）的格式如下：
 * <year><,><month><,><day><,><temperature>
 * 示例：
 * 2012,01,01,35
 * 2011,12,23,-4
 * <p>
 * 期望输出
 * 格式：
 * <year><month>　<temperature><,><temperature><,>...
 * 201301	90,80,70,-10
 * 201212	70,60,30,10,-20
 * <p>
 * HDFS输入
 * # hdfs dfs -mkdir -p /data/secondary_sort/input
 * # hdfs dfs -put sample_input.txt /data/secondary_sort/input/
 * # hdfs dfs -ls /data/secondary_sort/input
 * <p>
 * 本地运行
 * Program arguments ：
 * /opt/data/ideaprojects/data-algorithms/src/main/resources/chap01/sample_input.txt
 * /opt/data/output/mapreduce
 * 输出　：
 * hadoop@ubuntu:~$ ls /opt/data/output/mapreduce/
 * part-r-00000  _SUCCESS
 * hadoop@ubuntu:~$ cat /opt/data/output/mapreduce/*
 * 201301	90,80,70,-10
 * 201212	70,60,30,10,-20
 * 200012	10,-20
 * 200011	30,20,-40
 * <p>
 * <p>
 * hdfs运行：
 * run.sh
 */
