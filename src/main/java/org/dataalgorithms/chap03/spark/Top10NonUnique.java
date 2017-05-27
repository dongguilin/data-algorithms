package org.dataalgorithms.chap03.spark;

// STEP-0: import required classes and interfaces

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.broadcast.Broadcast;
import org.dataalgorithms.util.SparkUtil;
import scala.Tuple2;

import java.util.*;

/**
 * Assumption: for all input (K, V), K's are non-unique.
 * This class implements Top-N design pattern for N > 0.
 * The main assumption is that for all input (K, V)'s, K's
 * are non-unique. It means that you will find entries like
 * (A, 2), ..., (A, 5),...
 * <p>
 * This is a general top-N algorithm which will work unique
 * and non-unique keys.
 * <p>
 * This class may be used to find bottom-N as well (by
 * just keeping N-smallest elements in the set.
 * <p>
 * Top-10 Design Pattern: “Top Ten” Structure
 * <p>
 * 1. map(input) => (K, V)
 * <p>
 * 2. reduce(K, List<V1, V2, ..., Vn>) => (K, V),
 * where V = V1+V2+...+Vn
 * now all K's are unique
 * <p>
 * 3. partition (K,V)'s into P partitions
 * <p>
 * 4. Find top-N for each partition (we call this a local Top-N)
 * <p>
 * 5. Find Top-N from all local Top-N's
 *
 * @author Mahmoud Parsian
 */
public class Top10NonUnique {

    public static void main(String[] args) throws Exception {
        // STEP-1: handle input parameters
        if (args.length < 3) {
            System.err.println("Usage: Top10 <input-path> <topN> <output-path>");
            System.exit(1);
        }
        System.out.println("args[0]: <input-path>=" + args[0]);
        System.out.println("args[1]: <topN>=" + args[1]);
        System.out.println("args[2]: <output-path>=" + args[2]);
        final int N = Integer.parseInt(args[1]);

        // STEP-2: create a Java Spark Context object
        JavaSparkContext ctx = SparkUtil.createJavaSparkContext("Top10NonUnique");

        // STEP-3: broadcast the topN to all cluster nodes
        final Broadcast<Integer> topN = ctx.broadcast(N);
        // now topN is available to be read from all cluster nodes

        // STEP-4: create an RDD from input
        //    input record format:
        //        <string-key><,><integer-value-count>
        JavaRDD<String> lines = ctx.textFile(args[0], 1);
        lines.saveAsTextFile(args[2] + "/1");

        // STEP-5: partition RDD
        // public JavaRDD<T> coalesce(int numPartitions)
        // Return a new RDD that is reduced into numPartitions partitions.
        JavaRDD<String> rdd = lines.coalesce(9);

        // STEP-6: map input(T) into (K,V) pair
        // PairFunction<T, K, V>
        // T => Tuple2<K, V>
        JavaPairRDD<String, Integer> kv = rdd.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String s) {
                String[] tokens = s.split(","); // url,789
                return new Tuple2<String, Integer>(tokens[0], Integer.parseInt(tokens[1]));
            }
        });
        kv.saveAsTextFile(args[2] + "/2");

        // STEP-7: reduce frequent K's
        JavaPairRDD<String, Integer> uniqueKeys = kv.reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer i1, Integer i2) {
                return i1 + i2;
            }
        });
        uniqueKeys.saveAsTextFile(args[2] + "/3");

        // STEP-8: create a local top-N
        JavaRDD<SortedMap<Integer, String>> partitions = uniqueKeys.mapPartitions(
                new FlatMapFunction<Iterator<Tuple2<String, Integer>>, SortedMap<Integer, String>>() {
                    @Override
                    public Iterator<SortedMap<Integer, String>> call(Iterator<Tuple2<String, Integer>> iter) {
                        final int N = topN.value();
                        SortedMap<Integer, String> localTopN = new TreeMap<Integer, String>();
                        while (iter.hasNext()) {
                            Tuple2<String, Integer> tuple = iter.next();
                            localTopN.put(tuple._2, tuple._1);
                            // keep only top N
                            if (localTopN.size() > N) {
                                localTopN.remove(localTopN.firstKey());
                            }
                        }
                        return Collections.singletonList(localTopN).iterator();
                    }
                });
        partitions.saveAsTextFile(args[2] + "/4");

        // STEP-9: find a final top-N
        SortedMap<Integer, String> finalTopN = new TreeMap<Integer, String>();
        List<SortedMap<Integer, String>> allTopN = partitions.collect();
        for (SortedMap<Integer, String> localTopN : allTopN) {
            for (Map.Entry<Integer, String> entry : localTopN.entrySet()) {
                // count = entry.getKey()
                // url = entry.getValue()
                finalTopN.put(entry.getKey(), entry.getValue());
                // keep only top N
                if (finalTopN.size() > N) {
                    finalTopN.remove(finalTopN.firstKey());
                }
            }
        }

        // STEP-10: emit final top-N
        for (Map.Entry<Integer, String> entry : finalTopN.entrySet()) {
            System.out.println(entry.getKey() + "--" + entry.getValue());
        }

        System.exit(0);
    }
}
