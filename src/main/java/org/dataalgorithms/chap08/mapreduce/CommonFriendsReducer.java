package org.dataalgorithms.chap08.mapreduce;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * The CommonFriendsReducer class implements the reduce() function,
 * which just emits the common friends.
 *
 * @author Mahmoud Parsian
 */
public class CommonFriendsReducer extends Reducer<Text, Text, Text, Text> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonFriendsReducer.class);

    /**
     * The goal is to find common friends by intersecting all lists defined in values parameter.
     *
     * @param key    is a pair: <user_id_1><,><user_id_2>
     * @param values is a list of { <friend_1><,>...<,><friend_n> }
     */
    @Override
    public void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {
        LOGGER.info(key.toString());
        Map<String, Integer> map = new HashMap<String, Integer>();
        Iterator<Text> iterator = values.iterator();
//        int numOfValues = 0;
        while (iterator.hasNext()) {
            String friends = iterator.next().toString();
            LOGGER.info("[{}]", friends);
            if (friends.equals("")) {
                context.write(key, new Text("[]"));
                return;
            }
            addFriends(map, friends);
//            numOfValues++;
        }

        // now iterate the map to see how many have numOfValues
        List<String> commonFriends = new ArrayList<String>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            //System.out.println(entry.getKey() + "/" + entry.getValue());
//            if (entry.getValue() == numOfValues) {
            if (entry.getValue() == 2) {
                commonFriends.add(entry.getKey());
            }
        }

        // sen it to output
        context.write(key, new Text(commonFriends.toString()));
        LOGGER.info("({}),{}", key, commonFriends.toArray());
    }

    static void addFriends(Map<String, Integer> map, String friendsList) {
        String[] friends = StringUtils.split(friendsList, ",");
        for (String friend : friends) {
            Integer count = map.get(friend);
            if (count == null) {
                map.put(friend, 1);
            } else {
                map.put(friend, ++count);
            }
        }
    }

}
