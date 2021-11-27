import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.dataproc.v1.HadoopJob;
import com.google.cloud.dataproc.v1.Job;
import com.google.cloud.dataproc.v1.JobControllerClient;
import com.google.cloud.dataproc.v1.JobControllerSettings;
import com.google.cloud.dataproc.v1.JobMetadata;
import com.google.cloud.dataproc.v1.JobPlacement;
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.util.ArrayList;
import java.util.Arrays;
import java.nio.file.Paths;
import com.google.auth.oauth2.GoogleCredentials;

public class LocalApp
{
   public static void main(String[] args) throws Exception 
    {
        //Create GUI window
        new MainWindow();
    }
}

class MainWindow implements ActionListener
{
    //UI components
    JButton indexButton;
    JButton searchButton;
    JButton topNButton;
    JButton searchPanelButton;
    JButton topNPanelButton;
    JButton searchResultsButton;
    JButton topNResultsButton;
    JFrame frame;
    JPanel containerPanel;
    JPanel searchPanel;
    JPanel topNPanel;
    JPanel indexPanel;
    JPanel searchResultsPanel;
    JPanel topNResultsPanel;
    JTextArea textArea;
    JTextField searchText;
    JTextField topNText; 

    public MainWindow()
    {
        //Create main frame
        frame = new JFrame("Upload to hadoop");

        //Panel for file choose button
        JPanel panel1 = new JPanel();
        indexButton = new JButton("Create indices");
        indexButton.addActionListener(this); 
        panel1.add(indexButton);

        // Container panel for 3 previous panels
        containerPanel = new JPanel();
        containerPanel.setLayout(new GridBagLayout());

        containerPanel.add(panel1);

        //Make UI exit on close
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500,300);

        frame.getContentPane().add(containerPanel);
        frame.setVisible(true);

        // Main menu panel
        indexPanel = new JPanel(new GridLayout(3,1));
        JPanel panel2 = new JPanel();
        searchButton = new JButton("Search");
        searchButton.addActionListener(this); 
        panel2.add(searchButton);
        JPanel panel3 = new JPanel();
        topNButton = new JButton("Top N"); // Button is a Component
        topNButton.addActionListener(this); 
        panel3.add(topNButton);
        JPanel panel4 = new JPanel();
        textArea = new JTextArea();
        Color color = panel4.getBackground ();
        textArea.setBackground(color);
        textArea.setEditable(false);
        textArea.setText("Please select an option below:");
        panel4.add(textArea);
        indexPanel.add(panel4);
        indexPanel.add(panel2);
        indexPanel.add(panel3);

        // Search input panel
        searchPanel = new JPanel(new GridLayout(3,1));
        JPanel panel5 = new JPanel();
        JTextArea search = new JTextArea();
        search.setBackground(color);
        search.setEditable(false);
        search.setText("Please enter a search term:");
        panel5.add(search);
        JPanel panel6 = new JPanel();
        searchText = new JTextField(20);
        panel6.add(searchText);
        JPanel panel7 = new JPanel();
        searchPanelButton = new JButton("Search"); 
        searchPanelButton.addActionListener(this); 
        panel7.add(searchPanelButton);
        searchPanel.add(panel5);
        searchPanel.add(panel6);
        searchPanel.add(panel7);

        // Top N search panel
        topNPanel = new JPanel(new GridLayout(3,1));
        JPanel panel8 = new JPanel();
        JTextArea topN = new JTextArea();
        topN.setBackground(color);
        topN.setEditable(false);
        topN.setText("Please enter an N value:");
        panel8.add(topN);
        JPanel panel9 = new JPanel();
        topNText = new JTextField(20);
        panel9.add(topNText);
        JPanel panel10 = new JPanel();
        topNPanelButton = new JButton("Calculate"); // Button is a Component
        topNPanelButton.addActionListener(this); 
        panel10.add(topNPanelButton);
        topNPanel.add(panel8);
        topNPanel.add(panel9);
        topNPanel.add(panel10);

        // Buttons for returning to the main menu
        searchResultsButton = new JButton("Main menu");
        searchResultsButton.addActionListener(this);
        topNResultsButton = new JButton("Main menu");
        topNResultsButton.addActionListener(this);
    }

    /**
     * Button event handler
     * @Param e: Event registered
     */
    public void actionPerformed(ActionEvent e) 
    {
        if(e.getSource().equals(indexButton))
        {
            try
            {
                // Create inverted indices on hadoop
                SubmitInvertedIndexJob job = new SubmitInvertedIndexJob();
                job.submitInvertedIndexJob();
            }
            catch(Exception exc)
            {
                System.out.println(exc.toString());
            }
            // Remove first panel and go to the main menu after indices created
            frame.remove(containerPanel);
            frame.add(indexPanel);
            frame.pack();
            frame.revalidate();
            frame.repaint();
        }
        else if(e.getSource().equals(searchButton))
        {
            // Go to search screen from main menu
            frame.remove(indexPanel);
            frame.add(searchPanel);
            frame.pack();
            frame.revalidate();
            frame.repaint();
        }
        else if(e.getSource().equals(topNButton))
        {
            // Go to top N screen from main menu
            frame.remove(indexPanel);
            frame.add(topNPanel);
            frame.pack();
            frame.revalidate();
            frame.repaint();
        }
        else if(e.getSource().equals(searchPanelButton))
        {
            // Begin timing
            long startTime = System.nanoTime();
            // Call search method with text in text box
            String[] results = search(searchText.getText());
            if(results != null)
            {
                // End timing after search and convert to ms
                long endTime = System.nanoTime();
                long duration = ((endTime - startTime) / 1000000);
                // Create UI for results panel
                searchResultsPanel = new JPanel(new GridLayout(0,2));
                searchResultsPanel.add(new JLabel("File"));
                searchResultsPanel.add(new JLabel("Count"));
                // Add all search results to the UI panel
                for(int i = 0; i < results.length; i++)
                {
                    String[] result = results[i].split(":");
                    String file = result[0];
                    String count =  result[1];
                    searchResultsPanel.add(new JLabel(file));
                    searchResultsPanel.add(new JLabel(count));
                }
                // Add timing calculation to panel
                searchResultsPanel.add(new JLabel("Operation completed in " + duration + "ms"));
                searchResultsPanel.add(searchResultsButton);
                // Reset search text
                searchText.setText("");
                // Remove search entry panel and replace it with the results panel
                frame.remove(searchPanel);
                frame.add(searchResultsPanel);
                frame.pack();
                frame.revalidate();
                frame.repaint();
            }
            else
            {
                // If there were no results, inform the user and go back to the main menu
                frame.remove(searchPanel);
                frame.add(indexPanel);
                frame.revalidate();
                frame.repaint();
                JOptionPane.showMessageDialog(frame, "No results found for " + searchText.getText());
                searchText.setText("");
                
            }
        }
        else if(e.getSource().equals(topNPanelButton))
        {
            try
            {
                // Convert text input into an int
                int n = Integer.parseInt(topNText.getText());
                // Begin timing
                long startTime = System.nanoTime();
                // Call topN method to get results
                String[] results = topN(n);
                if(results != null)
                {
                    // End timing and convert to ms
                    long endTime = System.nanoTime();
                    long duration = ((endTime - startTime) / 1000000);
                    // Create UI for results panel
                    topNResultsPanel = new JPanel(new GridLayout(0,2));
                    topNResultsPanel.add(new JLabel("Word"));
                    topNResultsPanel.add(new JLabel("Count"));
                    // Add top n results to the panel
                    for(int i = 0; i < results.length; i++)
                    {
                        String[] result = results[i].split(":");
                        String file = result[1];
                        String count =  result[0];
                        topNResultsPanel.add(new JLabel(file));
                        topNResultsPanel.add(new JLabel(count));
                    }
                    // Add time data and button to go back to the menu
                    topNResultsPanel.add(new JLabel("Operation completed in " + duration + "ms"));
                    topNResultsPanel.add(topNResultsButton);
                    topNText.setText("");
                    // Switch to results panel
                    frame.remove(topNPanel);
                    frame.add(topNResultsPanel);
                    frame.pack();
                    frame.revalidate();
                    frame.repaint();
                }
                else
                {
                    // If no results, display an error popup and go back to the main menu
                    frame.remove(topNPanel);
                    frame.add(indexPanel);
                    frame.revalidate();
                    frame.repaint();
                    JOptionPane.showMessageDialog(frame, "Operation Failed");
                    topNText.setText("");
                    
                }
            }
            catch(NumberFormatException nfe)
            {
                // If the user didn't enter a number, inform them
                JOptionPane.showMessageDialog(frame, "Please enter a number");
            }
        }
        else if(e.getSource().equals(searchResultsButton))
        {
            // Switch to the main menu from search results page
            frame.remove(searchResultsPanel);
            frame.add(indexPanel);
            frame.pack();
            frame.revalidate();
            frame.repaint();
        }
        else if(e.getSource().equals(topNResultsButton))
        {
            // Switch to the main menu from top N results page
            frame.remove(topNResultsPanel);
            frame.add(indexPanel);
            frame.pack();
            frame.revalidate();
            frame.repaint();
        }
    }

    /**
     * Searches the results of inverted indices for the matching term
     */
    public String[] search(String term)
    {
        // Initialize return array
        String[] counts = null;
        //Create reader for output file of inverted indices downloaded from GCP
        try (BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/indexOutputFile"))) 
        {
            String line;
            // Read each line and store file and count data in a string array
            while ((line = br.readLine()) != null) 
            {
                String[] splitLine = line.split("\\s+");
                String word = splitLine[0];
                if(word.equalsIgnoreCase(term))
                {
                    counts = splitLine[1].split(",");
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Search failed");
        }

        return counts;

    }

    /**
     * Finds the top N words in the inverted indices
     */
    public String[] topN(int n)
    {
        try
        {
            // Search for top N word counts in the indices
            SubmitTopNJob job = new SubmitTopNJob();
            job.submitTopNJob(n);
        }
        catch(Exception exc)
        {
            System.out.println(exc.toString());
        }
        // Initialize counts array
        String[] counts = null;
        // Loop through top n file from GCP and read it into an array
        try (BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/topNOutputFile"))) 
        {
            // Size array to number to n
            counts = new String[n];
            String line;
            // Temp index variable
            int ind = counts.length;
            // Add counts to array in reverse order
            while ((line = br.readLine()) != null) 
            {
                String[] splitLine = line.split("\\s+");
                String word = splitLine[0];
                String count = splitLine[1];
                counts[--ind] = word + ":" + count;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Search failed");
        }

        return counts;
    }
}

class SubmitInvertedIndexJob 
{

    public static ArrayList<String> stringToList(String s) {
        return new ArrayList<>(Arrays.asList(s.split(" ")));
    }

    public static void submitInvertedIndexJob() throws Exception
    {
        //GCP info
        String projectId = "cs1660-final-project-330314";
        String region = "us-east1";
        String clusterName = "cs1660-final-project";
        submitInvertedIndexJob(projectId, region, clusterName);
    }

    public static void submitInvertedIndexJob(String projectId, String region, String clusterName) throws Exception 
    {
        String myEndpoint = String.format("%s-dataproc.googleapis.com:443", region);

        // Configure the settings for the job controller client.
        JobControllerSettings jobControllerSettings =
            JobControllerSettings.newBuilder().setEndpoint(myEndpoint).build();

        // Create a job controller client with the configured settings
        try (JobControllerClient jobControllerClient = JobControllerClient.create(jobControllerSettings)) 
        {
            // Set path to credentials file and authorize with GCP
            String path = System.getProperty("user.dir") + "/cs1660-final-project-330314-9fdcd0afe32b.json";
            System.out.println(path);
            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(path));
            Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

            // Configure cluster placement for the job.
            JobPlacement jobPlacement = JobPlacement.newBuilder().setClusterName(clusterName).build();

            // Configure hadoop job using input, .jar file, and output on GCP storage bucket
            HadoopJob hadoopJob =
            HadoopJob.newBuilder()
                .setMainClass("InvertedIndices")
                .addJarFileUris("gs://dataproc-staging-us-east1-712605025747-ymgttbsv/InvertedIndices.jar")
                .addArgs("gs://dataproc-staging-us-east1-712605025747-ymgttbsv/input/")
                .addArgs("gs://dataproc-staging-us-east1-712605025747-ymgttbsv/indexOutput/")
                .build();            
            Job job = Job.newBuilder().setPlacement(jobPlacement).setHadoopJob(hadoopJob).build();

            // Submit an asynchronous request to execute the job.
            OperationFuture<Job, JobMetadata> submitJobAsOperationAsyncRequest = jobControllerClient.submitJobAsOperationAsync(projectId, region, job);

            // Wait for job to finish
            Job response = submitJobAsOperationAsyncRequest.get();

            // Create a composite object to combine all hadoop output files into one. I did this because I couldn't find out how to 
            // submit a hadoop job and automatically execute -getmerge after the job completed
            Storage.ComposeRequest.Builder composeRequest = Storage.ComposeRequest.newBuilder();
            // Get all objects in the bucket
            Bucket bucket = storage.get("dataproc-staging-us-east1-712605025747-ymgttbsv");
            Page<Blob> blobs = bucket.list();
            for(Blob b: blobs.iterateAll()) 
            {   
                // If the current blob matches the output regex of a hdfs output file, append it to the compositeObject
                if(b.getName().matches("indexOutput/part-r-(.*)"))
                {
                    composeRequest.addSource(b.getName());
                }
            }

            // Build the composite object and download it to the local filesystem
            composeRequest.setTarget(BlobInfo.newBuilder("dataproc-staging-us-east1-712605025747-ymgttbsv", "indexOutputFile").build());
            Storage.ComposeRequest req = composeRequest.build();
            Blob compositeObject = storage.compose(req);
            compositeObject.downloadTo(Paths.get(System.getProperty("user.dir") + "/indexOutputFile"));

            JOptionPane.showMessageDialog(null, "Indices created!");


        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

class SubmitTopNJob 
{

    public static ArrayList<String> stringToList(String s) {
        return new ArrayList<>(Arrays.asList(s.split(" ")));
    }

    public static void submitTopNJob(int num) throws Exception
    {
        //GCP info
        String projectId = "cs1660-final-project-330314";
        String region = "us-east1";
        String clusterName = "cs1660-final-project";
        int n = num;
        submitTopNJob(projectId, region, clusterName, n);
    }

    public static void submitTopNJob(String projectId, String region, String clusterName, int n) throws Exception 
    {
        String myEndpoint = String.format("%s-dataproc.googleapis.com:443", region);

        // Configure the settings for the job controller client.
        JobControllerSettings jobControllerSettings =
            JobControllerSettings.newBuilder().setEndpoint(myEndpoint).build();

        // Create a job controller client with the configured settings. Using a try-with-resources
        // closes the client,
        // but this can also be done manually with the .close() method.
        try (JobControllerClient jobControllerClient = JobControllerClient.create(jobControllerSettings)) 
        {
            String path = System.getProperty("user.dir") + "/cs1660-final-project-330314-9fdcd0afe32b.json";
            System.out.println(path);
            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(path));
            Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

            // Configure cluster placement for the job.
            JobPlacement jobPlacement = JobPlacement.newBuilder().setClusterName(clusterName).build();

            // Create a job with input, .jar file, and output on GCP storage bucket. Also set a third argument
            // to be the N values to calculate
            HadoopJob hadoopJob =
            HadoopJob.newBuilder()
                .setMainClass("TopN")
                .addJarFileUris("gs://dataproc-staging-us-east1-712605025747-ymgttbsv/TopN.jar")
                .addArgs("gs://dataproc-staging-us-east1-712605025747-ymgttbsv/indexOutputFile")
                .addArgs("gs://dataproc-staging-us-east1-712605025747-ymgttbsv/topNOutput/")
                .addArgs(Integer.toString(n))
                .build();            
            Job job = Job.newBuilder().setPlacement(jobPlacement).setHadoopJob(hadoopJob).build();

            // Submit an asynchronous request to execute the job.
            OperationFuture<Job, JobMetadata> submitJobAsOperationAsyncRequest = jobControllerClient.submitJobAsOperationAsync(projectId, region, job);

            // Wait for job to finish
            Job response = submitJobAsOperationAsyncRequest.get();

            // Create a composite object to combine all hadoop output files into one. I did this because I couldn't find out how to 
            // submit a hadoop job and automatically execute -getmerge after the job completed
            Storage.ComposeRequest.Builder composeRequest = Storage.ComposeRequest.newBuilder();
            // Get all objects in the bucket
            Bucket bucket = storage.get("dataproc-staging-us-east1-712605025747-ymgttbsv");
            Page<Blob> blobs = bucket.list();
            for(Blob b: blobs.iterateAll()) 
            {   
                // If the current blob matches the output regex of a hdfs output file, append it to the compositeObject
                if(b.getName().matches("topNOutput/part-r-(.*)"))
                {
                    composeRequest.addSource(b.getName());
                }
            }

            // Build the composite object and download it to the local filesystem
            composeRequest.setTarget(BlobInfo.newBuilder("dataproc-staging-us-east1-712605025747-ymgttbsv", "topNOutputFile").build());
            Storage.ComposeRequest req = composeRequest.build();
            Blob compositeObject = storage.compose(req);
            compositeObject.downloadTo(Paths.get(System.getProperty("user.dir") + "/topNOutputFile"));


        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}