package org.dataalgorithms.chap01.mapreduce;


import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * SecondarySortMapper implements the map() function for
 * the secondary sort design pattern.
 * map()函数对输入完成解析和词法分析，然后将值（temperature）注入到归约器
 *
 * @author Mahmoud Parsian
 */
public class SecondarySortMapper
        extends Mapper<LongWritable, Text, DateTemperaturePair, Text> {

    private final Text theTemperature = new Text();
    private final DateTemperaturePair pair = new DateTemperaturePair();

    @Override
    /**
     * @param key is generated by Hadoop (ignored here)
     * @param value has this format: "YYYY,MM,DD,temperature"
     */
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {
        String line = value.toString();
        String[] tokens = line.split(",");
        // YYYY = tokens[0]
        // MM = tokens[1]
        // DD = tokens[2]
        // temperature = tokens[3]
        String yearMonth = tokens[0] + tokens[1];
        String day = tokens[2];
        int temperature = Integer.parseInt(tokens[3]);

        pair.setYearMonth(yearMonth);
        pair.setDay(day);
        pair.setTemperature(temperature);//将值注入到键
        theTemperature.set(tokens[3]);

        //发送到归约器
        context.write(pair, theTemperature);
    }
}
