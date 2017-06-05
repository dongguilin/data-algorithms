package org.dataalgorithms.chap06.secondarysort;

import org.apache.hadoop.mapreduce.Partitioner;
import org.dataalgorithms.chap06.TimeSeriesData;

/**
 * NaturalKeyPartitioner
 * <p>
 * This custom partitioner allow us to distribute how
 * outputs from the map stage are sent to the reducers.
 * The NaturalKeyPartitioner partitions the data output
 * from the map phase (SortByMRF_MovingAverageMapper)
 * before it is sent through the shuffle phase. Since
 * we want a single reducer to receive all time series
 * data for a single "name", we partition data output
 * of the map phase by only the natural key component
 * ("name").
 * 映射阶段的数据输出发送到洗牌阶段之前先分区
 *
 * @author Mahmoud Parsian
 */
public class NaturalKeyPartitioner extends Partitioner<CompositeKey, TimeSeriesData> {

    @Override
    public int getPartition(CompositeKey key,
                            TimeSeriesData value,
                            int numberOfPartitions) {
        return Math.abs((int) (hash(key.getName()) % numberOfPartitions));
    }


    /**
     * adapted from String.hashCode()
     */
    static long hash(String str) {
        long h = 1125899906842597L; // prime
        int length = str.length();
        for (int i = 0; i < length; i++) {
            h = 31 * h + str.charAt(i);
        }
        return h;
    }
}
