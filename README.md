# cs1660-final-project
Final project option 2 for CS1660

[Code walkthrough / demo](https://youtu.be/wAWOBUlBW6U)

## Assumptions / additional information
1. Input data is stored on the GCP storage bucket already. It was mentioned that this was okay to do during lecture. The input data itself is from the sample data provided, I just extracted everything into one folder.
2. The .jar files for the inverted index and top N alogrithms are already stored on the same GCP bucket as the input data
3. This project uses composite objects in place of the Hadoop -getmerge command. The results are the same, but you may have to enable/install crcmod on the master node of the   GCP cluster
4. In order to get GUI output from a Docker container, I used XMing which is only for Windows from my understanding. The container GUI is untested on macOS/Linux
5. The local application will not clean up after itself. This means that in order to run it multiple times, you have to manually delete the output folders on the GCP cluster. This is fine for the video demonstration but obviously is not ideal.
6. The search algorithm is executed on the local Docker image, not on HDFS. This was approved by the professor in class. The inverted index and top-N algorithms both run on HDFS.
7. Since I extracted the test data into one folder, the output of the search algorithm does not generate the folder or document IDs, only the document name and word counts.
8. My local application uses the GCP client libraries. You will have to install them as Java dependencies in order to run the local application.
9. Since my local application uses hard-coded GCP cluster information. You will have to edit the source java file in order to get it working for your own clusters. You will then have to download the dependencies or extract them from the provided .jar file in order to re-compile that source code. 
10. Basic errors are handled by the application, such as no results for a search or non-integer input for top-N. These are not demoed in the video(to save time) but should be noted just in case that is evaluated.
11. Apostrophes are not removed in my inverted index algorithm. This is to avoid splitting contractions, all other special characters are removed
12. Note: The errors in the code demo are because I had opened the code in the repository and not the code in the java projects I was using to test them
## Steps to run my container
1. Create a Dataproc cluster 
2. [Create a GCP service account for the cluster](https://console.cloud.google.com/projectselector/iam-admin/serviceaccounts/create?supportedpurview=project)
3. Create a credential JSON file for that service account and store it in the same folder as this repository
4. Edit the Dockerfile, replacing the name of my JSON credential file with yours
5. [Install XMing](https://sourceforge.net/projects/xming/). This will be used to send GUI information from the Docker container to your local machine.
6. Run the XLaunch application, ensure `No access control` is checked
7. Replace the IP address next to `ENV` in my Dockerfile with that of your local machine
8. Edit the `LocalApp.java` source code, replacing all cluster and authentication information with your own.
9. Download the dependencies for the `LocalApp.java` file and add them to the classpath. I did this using Maven, but feel free to do this however you wish. If you have the dependencies configured correctly, you should be able to run the application locally.
10. Re-compile the `LocalApp.java` file into a JAR, including the dependencies. This jar file will be executed in the Docker container. I used Maven for this as well, but you can use VSCode, Gradle, etc.
11. In your new Dataproc cluster, create a folder named `input`. Then, extract the sample data provided on Canvas to that folder. There should not be any subdirectories in the `input` folder
12. Upload the `InvertedIndices.java` and `TopN.java` files onto the GCP cluster
13. SSH into the master node of your GCP cluster
14. Transfer the `InvertedIndices.java` and `TopN.java` files from the GCP storage bucket onto the master node file system
15. Compile the java files using `hadoop com.sun.tools.javac.Main <fileName.java>` followed by `jar cf <outputName.jar> <fileName>*.class`. Make sure the output jar names are `InvertedIndices.jar` and `TopN.jar`
16. Transfer the compiled files back to the GCP storage bucket
17. Build the Docker container using `docker build -t <docker_username>/localapp>` where <docker_username> is your docker username. This will transfer the .jar file for the local application and your GCP credential file to the container and set some environment variables
18. From here, you can execute `docker run -it <docker_username>/localapp` where <docker_username> is your docker username. Just make sure to delete all output data folders on GCP after running the application.
## How it works
### GUI(Bonus)
The GUI data is sent from the docker container to my local machine using Xming. I first had to install and launch XMing, then I had to add my local machine's IP address to the Docker container via an environment variable.
### GCP authentication / communication
My local application authenticates and sends Hadoop jobs using the GCP Java client libraries. These are a better, more elegant, way as opposed to REST API requests. However, using these libraries caused me a lot of pain when trying to send jobs, compile the local application, and deploy it to Docker since there were so many dependencies. To authenticate with GCP, I use the client libraries along with the credentials JSON file before making any requests using the `GoogleCredentials.fromStream()` method. 
### Inverted Indices
My application uses the input data provided to construct an inverted index. This is done using the above mentioned GCP client libraries to submit a Hadoop job. The job itself uses the `InvertedIndices.jar` file on the cluster along with the `input` folder and outputs data back to the cluster in the following format `word1 file1:count1,file2:count2`. I used this format to make the top-N alogrithm easier to parse in the future. Once the InvertedIndex algorithm finishes, each output file produced is combined into a composite object using the client libraries again. Since I was only able to figure out how to send Hadoop jobs to GCP using data from the dataproc cluster, I didn't know how to execute a `-getmerge` command without running an additional mapreduce task. Composite objects allowed me to combine the output files of my mapreduce algorithms and download them to the local filesystem. The output of the inverted index algorithm was downloaded to the `IndexOutputFile` file using `compositeObject.downloadTo(Paths.get(System.getProperty("user.dir") + "/indexOutputFile"));`. Using `user.dir` allows for relative pathing, making porting the application to a Docker container much easier.
## Searching
My searching algorithm does not run on HDFS. This was allowed by the professor verbally in class. The search scans the `IndexOutputFile` line by line, looking for a word that matches. Once it finds one, it displays all files containing that word and their respective word counts to the user on the GUI.
## Top-N
My top-N algorithm runs as a separate mapreduce task on HDFS. My local application prompts the user for an N value, uses `IndexOutputFile` composite object on the GCP cluster and uses that as the input, the `TopN.jar` file on the cluster as the jar file, and outputs the results back to the cluster under `TopNOutputFile`. The mapreduce program itself uses only 1 reducer to ensure the top-N results are global. Each mapper generates the top N counts for its split, then sends that list to the reducer which calculates the global top N words. These results are then downloaded from the cluster and displayed to the user.  
