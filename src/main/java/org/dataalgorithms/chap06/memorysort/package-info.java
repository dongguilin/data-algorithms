/**
 * This package (org.dataalgorithms.chap06.memorysort) contains source code for
 * chapter 06 of the Data Algorithms book published by O'Reilly.
 * <p>
 * The org.dataalgorithms.chap06.memorysort package: sorts reducer values in memory/RAM
 *
 * @author Mahmoud Parsian
 */
package org.dataalgorithms.chap06.memorysort;

//rest of the file is empty

/**
 * 输入数据格式：
 * 股票代码,时间戳,当天已调整收盘价
 * <name-as-string><,><date-as-timestamp><,><value-as-double>
 * 输出数据格式：
 * <name-as-string><,><date-as-timestamp><,><moving-average-as-double>
 * <p>
 * hadoop@ubuntu:/app/spark2.1$ hdfs dfs -text /data/secondary_sort/output/chap06/*
 * AAPL	2013-10-04,483.22
 * AAPL	2013-10-07,485.39
 * AAPL	2013-10-08,484.345
 * AAPL	2013-10-09,483.765
 * GOOG	2004-11-03,193.26999999999998
 * GOOG	2004-11-04,188.18499999999997
 * GOOG	2013-07-17,551.625
 * GOOG	2013-07-18,914.615
 * GOOG	2013-07-19,903.6400000000001
 * IBM	2013-09-26,189.845
 * IBM	2013-09-27,188.57
 * IBM	2013-09-30,186.05
 */