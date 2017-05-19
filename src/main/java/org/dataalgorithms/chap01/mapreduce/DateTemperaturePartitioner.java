package org.dataalgorithms.chap01.mapreduce;


import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The DateTemperaturePartitioner is a custom partitioner class,
 * whcih partitions data by the natural key only (using the yearMonth).
 * Without custom partitioner, Hadoop will partition your mapped data
 * based on a hash code.
 * <p>
 * In Hadoop, the partitioning phase takes place after the map() phase
 * and before the reduce() phase
 * <p>
 * 分区器
 * 分区器会根据映射器的输出键来决定哪个映射器的输出发送到哪个归约器
 *
 * @author Mahmoud Parsian
 */
public class DateTemperaturePartitioner
        extends Partitioner<DateTemperaturePair, Text> {

    private static final Logger logger = LoggerFactory.getLogger(DateTemperaturePartitioner.class);

    /**
     * 确保有相同键（自然键yearMonth）的所有数据都将发送给同一个归约器
     *
     * @param pair
     * @param text
     * @param numberOfPartitions
     * @return
     */
    @Override
    public int getPartition(DateTemperaturePair pair,
                            Text text,
                            int numberOfPartitions) {
        logger.info(text.toString());
        // make sure that partitions are non-negative
        int p = Math.abs(pair.getYearMonth().hashCode() % numberOfPartitions);
        logger.info("partition:{}", p);
        return p;
    }
}
