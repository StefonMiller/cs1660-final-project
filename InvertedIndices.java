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

            // Split document ID and text
            String DocId = value.toString().substring(0, value.toString().indexOf("\t"));
            String value_raw =  value.toString().substring(value.toString().indexOf("\t") + 1);
            
            // Tokenize input by hyphen and space
            StringTokenizer itr = new StringTokenizer(value_raw, " '-");
            
            // Loop through all words in the line
            while (itr.hasMoreTokens()) 
            {
                // Remove special characters
                word.set(itr.nextToken().toLowerCase());

                // Make sure string isn't empty or in the stop list before counting it
                if(word.toString() != "" && !word.toString().isEmpty() && !stopWords.contains(word.toString()))
                {
                    // Write word with its associated count to the reducer
                    context.write(word, new Text(DocId));
                }
            }
        }
    }

    public static class IntSumReducer extends Reducer<Text,Text,Text,Text> 
    {
        // Aggregate word counts sent by mapper
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException 
        {
            // Stores counts for each word
            HashMap<String,Integer> map = new HashMap<String,Integer>();

            for (Text val : values) 
            {
                // Increment dictionary value if it exists. If not add the word to the dict
                if (map.containsKey(val.toString())) 
                {
                    map.put(val.toString(), map.get(val.toString()) + 1);
                } 
                else 
                {
                    map.put(val.toString(), 1);
                }
            }

            //After getting all values for the word, write the results
            StringBuilder docValueList = new StringBuilder();
            for(String docID : map.keySet())
            {
                docValueList.append(docID + ":" + map.get(docID) + " ");
            }

            context.write(key, new Text(docValueList.toString()));
        }
    }

    public static void main(String[] args) throws Exception 
    {
        Configuration config = new Configuration();
        Job job = Job.getInstance(config, "inverted index");
        job.setJarByClass(InvertedIndex.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setReducerClass(IntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}