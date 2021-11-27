import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;


public class TopN
{
    public static class TopNMapper extends Mapper<Object, Text, Text, IntWritable>
    {
        // n objects to return for the mapper
        private int n;
        // List of words sorted by frequency
        private TreeMap<Integer, String> wordList;

        public void setup(Context context)
        {
            // Get value of n from context established in main method
            n = Integer.parseInt(context.getConfiguration().get("N"));
            // Initialize word list
            wordList = new TreeMap<Integer, String>();
        }

        public void map(Object key, Text value, Context context)
        {
            // Split line by whitespace
            String[] line = value.toString().split("\\s+");

            // Get the word, first value before whitespace
            String word = line[0];

            // Comma separated list of file:count is second string after whitespace
            String[] counts = line[1].split(",");

            // Temporary variable for total count of a given word
            int total = 0;

            // Iterate over all counts and add up all the counts for each file
            for(String count : counts)
            {
                String tempCount = count.split(":")[1];
                total += Integer.parseInt(tempCount);
            }

            // store values as wordcount, word so they can be sorted by key. That way we don't need secondary sort
            wordList.put(Integer.valueOf(total), word);

            // remove the smallest wordcount if the size of the list is too high
            if (wordList.size() > n)
            {
                wordList.remove(wordList.firstKey());
            }
        }

        public void cleanup(Context context) throws IOException, InterruptedException
        {
            // Swap structure of the wordlist before passing it to the reducer. Only the top N values sent to the mapper will be sent
            for (Map.Entry<Integer, String> entry : wordList.entrySet())
            {
                context.write(new Text(entry.getValue()), new IntWritable(entry.getKey()));
            }
        }
    }

    public static class TopNReducer extends Reducer<Text, IntWritable, IntWritable, Text>
    {
        // Number of values to return from reducer
        private int n;
        // Data structure holding results of reducer
        private TreeMap<Integer, String> wordList;

        public void setup(Context context)
        {
            // Get value of N from context created in main method
            n = Integer.parseInt(context.getConfiguration().get("N"));
            // Initialize word list
            wordList = new TreeMap<Integer, String>();
        }

        public void reduce(Text key, Iterable<IntWritable> values, Context context)
        {
            // Initialize a temporary word count
            int wordcount = 0;

            // Get the word count of each word
            for(IntWritable value : values)
            {
                wordcount = value.get();
            }

            // Insert items into the list by count and then word so they can be sorted
            wordList.put(wordcount, key.toString());

            // Trim the list if it gets too big
            if (word_list.size() > n)
            {
                word_list.remove(word_list.firstKey());
            }
        }

        public void cleanup(Context context) throws IOException, InterruptedException
        {
            // Write the top N words by wordcount so they get sorted
            for (Map.Entry<Integer, String> entry : word_list.entrySet())
            {
                context.write(new IntWritable(entry.getKey()), new Text(entry.getValue()));
            }
        }
    }


    public static void main(String[] args) throws Exception
    {
        Configuration config = new Configuration();
        // Set the value of N in the config to be the 3rd argument passed in
        config.set("N", args[2]); 
        Job job = Job.getInstance(config, "TopN");
        job.setJarByClass(TopN.class);
        job.setMapperClass(TopNMapper.class);
        job.setReducerClass(TopNReducer.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(Text.class);
        job.setNumReduceTasks(1);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);

    }
}