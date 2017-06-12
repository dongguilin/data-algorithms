package org.dataalgorithms.chap09.mapreduce;

import edu.umd.cloud9.io.pair.PairOfLongs;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by guilin on 6/12/17.
 */
public class RecommendFriendsReducer extends Reducer<LongWritable, PairOfLongs, LongWritable, Text> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecommendFriendsReducer.class);

    @Override
    protected void reduce(LongWritable key, Iterable<PairOfLongs> values, Context context) throws IOException, InterruptedException {
        Map<Long, List<Long>> mutualFriends = new HashMap<>();
        for (PairOfLongs pair : values) {
            long toUser = pair.getKey();
            long mutualFriend = pair.getValue();
            boolean alreadyFriend = (mutualFriend == -1);
            if (mutualFriends.containsKey(toUser)) {
                if (alreadyFriend) {
                    mutualFriends.put(toUser, null);
                } else if (mutualFriends.get(toUser) != null) {
                    mutualFriends.get(toUser).add(mutualFriend);
                }
            } else {
                if (alreadyFriend) {
                    mutualFriends.put(toUser, null);
                } else {
                    List<Long> list = new ArrayList<>();
                    list.add(mutualFriend);
                    mutualFriends.put(toUser, list);
                }
            }
        }
        String recommendations = buildRecommendations(mutualFriends);
        LOGGER.info(recommendations);
        context.write(key, new Text(recommendations));

    }

    private static String buildRecommendations(Map<Long, List<Long>> mutualFriends) {
        StringBuilder recommendations = new StringBuilder();
        for (Map.Entry<Long, List<Long>> mapEntry : mutualFriends.entrySet()) {
            if (mapEntry.getValue() == null) {
                continue;
            }
            recommendations.append(mapEntry.getKey());
            recommendations.append(" (");
            recommendations.append(mapEntry.getValue().size());
            recommendations.append(": ");
            recommendations.append(mapEntry.getValue());
            recommendations.append("),");
        }
        return recommendations.toString();
    }
}
