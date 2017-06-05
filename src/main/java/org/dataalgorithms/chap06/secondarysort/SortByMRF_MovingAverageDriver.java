package org.dataalgorithms.chap06.secondarysort;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.dataalgorithms.chap06.TimeSeriesData;
import org.dataalgorithms.util.HadoopUtil;

//

/**
 * SortByMRF_MovingAverageDriver is the driver class.
 * MapReduce job for moving averages of time series data
 * by using MapReduce's "secondary sort" (sort by shuffle function).
 *
 * @author Mahmoud Parsian
 */
public class SortByMRF_MovingAverageDriver {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 3) {
            System.err.println("Usage: SortByMRF_MovingAverageDriver <window_size> <input> <output>");
            System.exit(1);
        }
        int windowSize = Integer.parseInt(otherArgs[0]);
        Path inputPath = new Path(otherArgs[1]);
        Path outputPath = new Path(otherArgs[2]);
        System.out.println("args[0]: <window_size>=" + windowSize);
        System.out.println("args[1]: <input>=" + inputPath);
        System.out.println("args[2]: <output>=" + outputPath);

        FileSystem.get(conf).delete(outputPath, true);

        Job job = Job.getInstance(conf, "SortByMRF_MovingAverageDriver");

        // add jars to distributed cache
        HadoopUtil.addJarsToDistributedCache(job, "/lib/");

        // set mapper/reducer
        job.setMapperClass(SortByMRF_MovingAverageMapper.class);
        job.setReducerClass(SortByMRF_MovingAverageReducer.class);

        // define mapper's output key-value
        job.setMapOutputKeyClass(CompositeKey.class);
        job.setMapOutputValueClass(TimeSeriesData.class);

        // define reducer's output key-value
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        // set window size for moving average calculation
        job.getConfiguration().setInt("moving.average.window.size", windowSize);

        // define I/O
        FileInputFormat.setInputPaths(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        // the following 3 setting are needed for "secondary sorting"
        // Partitioner decides which mapper output goes to which reducer
        // based on mapper output key. In general, different key is in
        // different group (Iterator at the reducer side). But sometimes,
        // we want different key in the same group. This is the time for
        // Output Value Grouping Comparator, which is used to group mapper
        // output (similar to group by condition in SQL).  The Output Key
        // Comparator is used during sort stage for the mapper output key.
        job.setPartitionerClass(NaturalKeyPartitioner.class);
        job.setSortComparatorClass(CompositeKeyComparator.class);
        job.setGroupingComparatorClass(NaturalKeyGroupingComparator.class);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}





