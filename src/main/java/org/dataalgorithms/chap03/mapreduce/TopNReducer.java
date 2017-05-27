package org.dataalgorithms.chap03.mapreduce;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Reducer's input are local top N from all mappers.
 * We have a single reducer, which creates the final top N.
 *
 * @author Mahmoud Parsian
 */
public class TopNReducer extends
        Reducer<NullWritable, Text, IntWritable, Text> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopNReducer.class);

    private int N = 10; // default
    private SortedMap<Integer, String> top = new TreeMap<Integer, String>();

    @Override
    public void reduce(NullWritable key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {
        for (Text value : values) {
            String valueAsString = value.toString().trim();
            LOGGER.info("value:{}", valueAsString);
            String[] tokens = valueAsString.split(",");
            String url = tokens[0];
            int frequency = Integer.parseInt(tokens[1]);
            top.put(frequency, url);
            // keep only top N
            if (top.size() > N) {
                top.remove(top.firstKey());
            }
        }

        // emit final top N
        List<Integer> keys = new ArrayList<Integer>(top.keySet());
        for (int i = keys.size() - 1; i >= 0; i--) {
            context.write(new IntWritable(keys.get(i)), new Text(top.get(keys.get(i))));
        }
    }

    @Override
    protected void setup(Context context)
            throws IOException, InterruptedException {
        this.N = context.getConfiguration().getInt("N", 10); // default is top 10
    }


}
