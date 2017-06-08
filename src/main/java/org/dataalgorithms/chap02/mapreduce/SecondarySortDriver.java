package org.dataalgorithms.chap02.mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

/**
 * SecondarySortDriver is driver class for submitting secondary sort job to Hadoop.
 *
 * @author Mahmoud Parsian
 */
public class SecondarySortDriver extends Configured implements Tool {

    private static Logger THE_LOGGER = Logger.getLogger(SecondarySortDriver.class);

    public static void main(String[] args) throws Exception {
        // Make sure there are exactly 2 parameters
        if (args.length != 2) {
            THE_LOGGER.warn("Usage: SecondarySortDriver <input> <output>");
            System.exit(1);
        }

        THE_LOGGER.info("inputDir=" + args[0]);
        THE_LOGGER.info("outputDir=" + args[1]);
        int returnStatus = ToolRunner.run(new SecondarySortDriver(), args);
        System.exit(returnStatus);
    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = getConf();

        Job job = Job.getInstance(conf, "Secondary Sort");

        // add jars to distributed cache
        //HadoopUtil.addJarsToDistributedCache(conf, "/lib/");


        FileSystem.get(conf).delete(new Path(args[1]), true);

        job.setJarByClass(SecondarySortDriver.class);

        //job.setJar("/opt/data/ideaprojects/data-algorithms/target/data-algorithms-1.0-SNAPSHOT.jar");

        // set mapper and reducer
        job.setMapperClass(SecondarySortMapper.class);
        job.setReducerClass(SecondarySortReducer.class);

        // define mapper's output key-value
        job.setMapOutputKeyClass(CompositeKey.class);
        job.setMapOutputValueClass(NaturalValue.class);

        // define reducer's output key-value
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        // the following 3 setting are needed for "secondary sorting"
        // Partitioner decides which mapper output goes to which reducer
        // based on mapper output key. In general, different key is in
        // different group (Iterator at the reducer side). But sometimes,
        // we want different key in the same group. This is the time for
        // Output Value Grouping Comparator, which is used to group mapper
        // output (similar to group by condition in SQL).  The Output Key
        // Comparator is used during sort stage for the mapper output key.
        job.setPartitionerClass(NaturalKeyPartitioner.class);
        job.setGroupingComparatorClass(NaturalKeyGroupingComparator.class);
        job.setSortComparatorClass(CompositeKeyComparator.class);

        job.setNumReduceTasks(2);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        boolean status = job.waitForCompletion(true);
        THE_LOGGER.info("run(): status=" + status);
        return status ? 0 : 1;
    }
}
