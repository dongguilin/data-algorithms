package org.dataalgorithms.chap01.spark;


// STEP-0: import required Java/Spark classes.

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.dataalgorithms.chap01.util.SparkTupleComparator;
import org.dataalgorithms.util.SparkUtil;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//

/**
 * SecondarySortUsingGroupByKey class implements the secondary sort design pattern
 * by sorting reducer values in memory/RAM.
 * <p>
 * <p>
 * Input:
 * <p>
 * name, time, value
 * x,2,9
 * y,2,5
 * x,1,3
 * y,1,7
 * y,3,1
 * x,3,6
 * z,1,4
 * z,2,8
 * z,3,7
 * z,4,0
 * <p>
 * Output: generate a time-series looking like this:
 * <p>
 * t1 t2 t3 t4
 * x => [3, 9, 6]
 * y => [7, 5, 1]
 * z => [4, 8, 7, 0]
 * <p>
 * x => [(1,3), (2,9), (3,6)]
 * y => [(1,7), (2,5), (3,1)]
 * z => [(1,4), (2,8), (3,7), (4,0)]
 *
 * @author Mahmoud Parsian
 */
public class SecondarySortUsingGroupByKey2 {
    public static void main(String[] args) throws Exception {

        // STEP-1: read input parameters and validate them
        if (args.length < 2) {
            System.err.println("Usage: SecondarySortUsingGroupByKey <input> <output>");
            System.exit(1);
        }
        String inputPath = args[0];
        System.out.println("inputPath=" + inputPath);
        String outputPath = args[1];
        System.out.println("outputPath=" + outputPath);

        // STEP-2: Connect to the Spark master by creating JavaSparkContext object
        final JavaSparkContext ctx = SparkUtil.createJavaSparkContext("SecondarySorting");


        //STEP-3: Use ctx to create JavaRDD<String>
        ctx.textFile(inputPath, 1)
                //STEP-4: create (key, value) pairs from JavaRDD<String> where key is the {name} and value is a pair of (time, value).
                .mapToPair(new PairFunction<String, String, Tuple2<Integer, Integer>>() {
                    @Override
                    public Tuple2<String, Tuple2<Integer, Integer>> call(String s) throws Exception {
                        String[] tokens = s.split(",");//x,2,5
                        Tuple2<Integer, Integer> timevalue = new Tuple2<>(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
                        return new Tuple2<>(tokens[0], timevalue);
                    }
                })
                //STEP-5: We group JavaPairRDD<> elements by the key ({name}).
                .groupByKey()
                //STEP-6: Sort the reducer's values and this will give us the final output.
                .mapValues(new Function<Iterable<Tuple2<Integer, Integer>>, Iterable<Tuple2<Integer, Integer>>>() {
                    @Override
                    public Iterable<Tuple2<Integer, Integer>> call(Iterable<Tuple2<Integer, Integer>> v1) throws Exception {
                        List<Tuple2<Integer, Integer>> newList = new ArrayList<>(iterableToList(v1));
                        //在内存中对归约器值排序，这种方案不具有可伸缩性
                        Collections.sort(newList, SparkTupleComparator.INSTANCE);
                        return newList;
                    }
                })
                .sortByKey()
                .saveAsTextFile(outputPath);


        System.exit(0);
    }


    static List<Tuple2<Integer, Integer>> iterableToList(Iterable<Tuple2<Integer, Integer>> iterable) {
        List<Tuple2<Integer, Integer>> list = new ArrayList<Tuple2<Integer, Integer>>();
        for (Tuple2<Integer, Integer> item : iterable) {
            list.add(item);
        }
        return list;
    }
}
/**
 * 本地模式运行
 * VM options ：　-Dspark.master=local
 * Program arguments ：
 * /opt/data/ideaprojects/data-algorithms/src/main/resources/chap01/timeseries.txt
 * /opt/data/output/spark
 * 输出：
 * hadoop@ubuntu:~$ ls /opt/data/output/spark/
 * part-00000  _SUCCESS
 * hadoop@ubuntu:~$ cat /opt/data/output/spark/*
 * (p,[(1,9), (2,6), (4,7), (6,0), (7,3)])
 * (x,[(1,3), (2,9), (3,6)])
 * (y,[(1,7), (2,5), (3,1)])
 * (z,[(1,4), (2,8), (3,7), (4,0)])
 */
