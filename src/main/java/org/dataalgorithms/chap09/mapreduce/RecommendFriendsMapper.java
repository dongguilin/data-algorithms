package org.dataalgorithms.chap09.mapreduce;

import edu.umd.cloud9.io.pair.PairOfLongs;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by guilin on 6/12/17.
 */
public class RecommendFriendsMapper extends Mapper<LongWritable, Text, LongWritable, PairOfLongs> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecommendFriendsMapper.class);

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        LOGGER.info("line:{}", value.toString());
        String[] tokens = StringUtils.split(value.toString(), " ");

        long person = Long.parseLong(tokens[0]);
        String friendsAsString = tokens[1];
        List<String> friends = Arrays.asList(StringUtils.split(friendsAsString, ","));

        LOGGER.info("发出所有直接好友关系");
        for (String friend : friends) {
            PairOfLongs outValue = new PairOfLongs(Long.parseLong(friend), -1);//-1表示直接好友
            context.write(new LongWritable(person), outValue);
            LOGGER.info("({},{})", new LongWritable(person), outValue);
        }

        LOGGER.info("发出所有可能的好友关系");
        for (int i = 0; i < friends.size(); i++) {
            for (int j = i + 1; j < friends.size(); j++) {

                long f1 = Long.parseLong(friends.get(j));
                long f2 = Long.parseLong(friends.get(i));

                //可能的好友1
                //person为共同好友
                context.write(new LongWritable(f2), new PairOfLongs(f1, person));
                //可能的好友2
                context.write(new LongWritable(f1), new PairOfLongs(f2, person));
                LOGGER.info("({},{})", new LongWritable(f2), new PairOfLongs(f1, person));
                LOGGER.info("({},{})", new LongWritable(f1), new PairOfLongs(f2, person));
            }
        }

    }
}
