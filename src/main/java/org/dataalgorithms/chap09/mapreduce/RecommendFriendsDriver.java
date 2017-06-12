package org.dataalgorithms.chap09.mapreduce;

import edu.umd.cloud9.io.pair.PairOfLongs;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guilin on 6/12/17.
 */
public class RecommendFriendsDriver extends Configured implements Tool {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecommendFriendsDriver.class);

    public static void main(String[] args) throws Exception {
        //Make sure there are exactly 2 parameters
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage: <inputPath> <outPath>");
        }
        int status = ToolRunner.run(new RecommendFriendsDriver(), args);
        System.exit(status);
    }

    @Override
    public int run(String[] args) throws Exception {
        String inputPath = args[0];
        String outputPath = args[1];
        LOGGER.info("inputPath:{}", inputPath);
        LOGGER.info("outputPath:{}", outputPath);


        FileSystem.get(getConf()).delete(new Path(outputPath), true);
        Job job = Job.getInstance(getConf());
        job.setJobName("RecommendFriends");
        job.setJarByClass(RecommendFriendsDriver.class);


        //input/output path
        FileInputFormat.setInputPaths(job, new Path(inputPath));
        FileOutputFormat.setOutputPath(job, new Path(outputPath));

        //Mapper K, V output
        job.setMapOutputKeyClass(LongWritable.class);
        job.setMapOutputValueClass(PairOfLongs.class);
        //output format
        job.setOutputFormatClass(TextOutputFormat.class);

        //Reducer K, V output
        job.setOutputKeyClass(LongWritable.class);
        job.setOutputValueClass(Text.class);

        // set mapper/reducer
        job.setMapperClass(RecommendFriendsMapper.class);
        job.setReducerClass(RecommendFriendsReducer.class);

        long startTime = System.currentTimeMillis();
        boolean status = job.waitForCompletion(true);
        LOGGER.info("job status=" + status);
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        LOGGER.info("Elapsed time: " + elapsedTime + " milliseconds");

        return status ? 0 : 1;
    }
}
