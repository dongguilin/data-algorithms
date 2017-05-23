/**
 * This package (org.dataalgorithms.chap02.mapreduce) contains source code
 * for chapter 02 of the Data Algorithms book published by O'Reilly.
 * This package contains MapReduce/Hadoop solution for chapter 2.
 *
 * @author Mahmoud Parsian
 */
package org.dataalgorithms.chap02.mapreduce;

//rest of the file is empty

/**
 * 输入格式
 * 采用CSV格式：
 * Stock-Symbol,Date,Closed-Price
 * ILMN,2013-12-05,97.65
 * GOOG,2013-12-09,1078.14
 * IBM,2013-12-09,177.46
 * ILMN,2013-12-09,101.33
 * ILMN,2013-12-06,99.25
 * GOOG,2013-12-06,1069.87
 * IBM,2013-12-06,177.67
 * GOOG,2013-12-05,1057.34
 * 输出格式
 * 输出按收盘价日期排序，如下
 * hadoop@ubuntu:/app$ hdfs dfs -text /data/secondary_sort/output/chap02/mapreduce/*
 * GOOG	(2013-12-05,1057.34)(2013-12-06,1069.87)(2013-12-09,1078.14)
 * IBM	(2013-12-06,177.67)(2013-12-09,177.46)
 * ILMN	(2013-12-05,97.65)(2013-12-06,99.25)(2013-12-09,101.33)
 */