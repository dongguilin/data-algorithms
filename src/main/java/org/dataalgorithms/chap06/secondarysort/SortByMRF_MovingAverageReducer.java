package org.dataalgorithms.chap06.secondarysort;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.dataalgorithms.chap06.TimeSeriesData;
import org.dataalgorithms.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

//
//

/**
 * SortByMRF_MovingAverageReducer implements the reduce() function.
 * <p>
 * Data arrive sorted to reducer (reducers values are sorted by
 * MapReduce framework  -- NOTE: values are not sorted in memory).
 *
 * @author Mahmoud Parsian
 */
public class SortByMRF_MovingAverageReducer extends Reducer<CompositeKey, TimeSeriesData, Text, Text> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SortByMRF_MovingAverageReducer.class);

    int windowSize = 5; // default window size

    /**
     * will be run only once
     * get parameters from Hadoop's configuration
     */
    public void setup(Context context)
            throws IOException, InterruptedException {
        this.windowSize = context.getConfiguration().getInt("moving.average.window.size", 5);
        System.out.println("setup(): key=" + windowSize);
    }

    @Override
    protected void reduce(CompositeKey key, Iterable<TimeSeriesData> values, Context context) throws IOException, InterruptedException {
        LOGGER.info("reduce:{},{}", key.getName(), key.getTimestamp());
        // note that values are sorted.
        // apply moving average algorithm to sorted timeseries
        Text outputKey = new Text();
        Text outputValue = new Text();
        MovingAverage ma = new MovingAverage(this.windowSize);
        for (TimeSeriesData data : values) {
            ma.addNewNumber(data.getValue());
            double movingAverage = ma.getMovingAverage();
            long timestamp = data.getTimestamp();
            String dateAsString = DateUtil.getDateAsString(timestamp);
            outputValue.set(dateAsString + "," + movingAverage);
            outputKey.set(key.getName());
            LOGGER.info("({},{})", key.getName(), dateAsString + "," + movingAverage);
            context.write(outputKey, outputValue);
        }
    }

}
