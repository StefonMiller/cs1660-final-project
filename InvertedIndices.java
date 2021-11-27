import java.util.*;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class InvertedIndices 
{
    public static class TokenizerMapper extends Mapper<Object, Text, Text, Text>
    {
        private Text word = new Text();

        /*
         List of stop words. Stored in a hash set for fast lookups. I could have read these in via a text file but thought it would be
         better to have everything in one java file. List taken from https://www.ranks.nl/stopwords
        */ 
        private HashSet<String> stopWords = new HashSet<String>(Arrays.asList("a", "about", "above", "after",
        "again", "against", "all", "am", "an", "and", "any", "are", "aren't", "as", "at", "be", "because",
        "been", "before", "being", "below", "between", "both", "but", "by", "can't", "cannot", "could",
        "couldn't", "did", "didn't", "do", "does", "doesn't", "doing", "don't", "down", "during", "each",
        "few", "for", "from", "further", "had", "hadn't", "has", "hasn't", "have", "haven't", "having", "he",
        "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself", "him", "himself", "his", "how",
        "how's", "i", "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is", "isn't", "it", "it's", "its",
        "itself", "let's", "me", "more", "most", "mustn't", "my", "myself", "no", "nor", "not", "of", "off",
        "on", "once", "only", "or", "other", "ought", "our", "ours", "ourselves", "out", "over", "own", "same", 
        "shan't", "she", "she'd", "she'll", "she's", "should", "shouldn't", "so", "some", "such", "than",
        "that", "that's", "the", "their", "theirs", "them", "themselves", "then", "there", "there's", "these",
        "they", "they'd", "they'll", "they're", "they've", "this", "those", "through", "to", "too", "under",
        "until", "up", "very", "was", "wasn't", "we", "we'd", "we'll", "we're", "we've", "were", "weren't",
        "what", "what's", "when", "when's", "where", "where's", "which", "while", "who", "who's", "whom",
        "why", "why's", "with", "won't", "would", "wouldn't", "you", "you'd", "you'll", "you're",
        "you've", "your", "yours", "yourself", "yourselves"));

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException 
        {

            // Get file where the current split is from
            String fileName = ((FileSplit) context.getInputSplit()).getPath().getName();
            // Get the string value of the current split
            String valueString = value.toString();
            
            // Tokenize input by hyphen and space
            StringTokenizer itr = new StringTokenizer(valueString, " -");
            
            // Loop through all words in the line
            while (itr.hasMoreTokens()) 
            {
                // Remove special characters, tabs, and newlines, and make everyting lowercase
                word.set(itr.nextToken().replaceAll("[^a-zA-Z ']", "").replaceAll("[\\n\\t ]", "").toLowerCase());

                // Make sure string isn't empty or in the stop list before counting it
                if(word.toString() != "" && !word.toString().isEmpty() && !stopWords.contains(word.toString()))
                {
                    // Write word with its associated file to the reducer
                    context.write(word, new Text(fileName));
                }
            }
        }
    }

    public static class IntSumReducer extends Reducer<Text,Text,Text,Text> 
    {
        // Aggregate word counts sent by mapper
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException 
        {
            // Stores counts for each word + file pair
            HashMap<String,Integer> counts = new HashMap<String,Integer>();

            for (Text val : values) 
            {
                // Increment dictionary value if it exists. If not add the word to the dict
                if (counts.containsKey(val.toString())) 
                {
                    counts.put(val.toString(), counts.get(val.toString()) + 1);
                } 
                else 
                {
                    counts.put(val.toString(), 1);
                }
            }

            //After getting all values for the word, write the results
            ArrayList<String> fileCounts = new ArrayList<String>();
            for(String word : counts.keySet())
            {
                fileCounts.add(word + ":" + counts.get(word));
            }

            String outputString = String.join(",", fileCounts);

            context.write(key, new Text(outputString));
        }
    }

    public static void main(String[] args) throws Exception 
    {
        Configuration config = new Configuration();
        Job job = Job.getInstance(config, "inverted index");
        job.setJarByClass(InvertedIndices.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setReducerClass(IntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}