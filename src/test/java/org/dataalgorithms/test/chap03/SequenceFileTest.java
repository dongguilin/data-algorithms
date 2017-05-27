package org.dataalgorithms.test.chap03;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * Created by guilin on 17-5-23.
 */
public class SequenceFileTest {


    public static Configuration conf = new Configuration();

    public static void write(File file, String sequenceFilepath) throws IOException {
        List<String> list = FileUtils.readLines(file);

        Path path = new Path(sequenceFilepath);

        FileSystem fs = FileSystem.get(URI.create(sequenceFilepath), conf);

        SequenceFile.Writer writer = SequenceFile.createWriter(fs, conf, path, Text.class, IntWritable.class);

        Text key = new Text();

        IntWritable value = new IntWritable();

        for (int i = 0; i < list.size(); i++) {
            String[] tokens = list.get(i).split(",");

            key.set(tokens[0]);

            value.set(Integer.parseInt(tokens[1]));

            writer.append(key, value);

        }

        writer.close();

    }

    public static void read(String pathStr) throws IOException {

        FileSystem fs = FileSystem.get(URI.create(pathStr), conf);

        SequenceFile.Reader reader = new SequenceFile.Reader(fs, new Path(pathStr), conf);

        Text num = new Text();

        IntWritable name = new IntWritable();

        while (reader.next(name, num)) {
            System.out.println(num + " " + name);
        }
        reader.close();

    }

    @Test
    public void testWrite() throws Exception {
        File file = new File("/opt/data/ideaprojects/data-algorithms/src/main/resources/chap03/top10data.txt");
        String outputPath = "/opt/data/ideaprojects/data-algorithms/src/main/resources/chap03/top10data.se";
        write(file, outputPath);
    }

    @Test
    public void testRead() throws Exception {
        read("/opt/data/output/chap03/mapreduce/part-r-00000");
    }


}
