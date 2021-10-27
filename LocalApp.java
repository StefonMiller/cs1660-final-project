import java.util.*;
import java.io.File;
import java.io.IOException;
class LocalApp 
{
    public static void main(String args[]) throws IOException
    {
        //Files to index
        ArrayList<String> fileNames = new ArrayList<String>(10);
        Scanner keyboard = new Scanner(System.in);
        boolean indicesCreated = false;
        
        while(true)
        {
            //Display different menu if indices were created
            if(indicesCreated)
            {
                //Prompt user to choose what to do
                int choice = 0;
                System.out.println("Please select an option:\n" +
                                    "\t1. Search for term\n" +
                                    "\t2. Top-N\n" +  
                                    "\t3. Quit");
                //Validate input
                try
                {
                    choice = keyboard.nextInt();
                    keyboard.nextLine();
                }
                catch(InputMismatchException ime)
                {
                    System.out.println("Invalid input");
                    continue;
                }
                
                switch(choice)
                {
                    //Search, topN, or quit depending on input
                    case 1:
                        System.out.println("Please enter your search term:");
                        String searchTerm = keyboard.nextLine();
                        search(searchTerm);
                        break;
                    case 2:
                        System.out.println("Please enter your search term:");
                        String topNSearchTerm = keyboard.nextLine();
                        topN(topNSearchTerm);
                        break;
                    case 3:
                        System.exit(0);
                    default:
                        System.out.println("Invalid input");
                        break;
                }
            }
            else
            {
                int choice = 0;
                //Display main menu if indices not created
                System.out.println("Currently chosen files:");
                System.out.println(fileNames + "\n");
                System.out.println("Please select an option:\n" +
                                    "\t1. Add files\n" + 
                                    "\t2. Create indices\n" +
                                    "\t3. Quit");
                //Validate input
                try
                {
                    choice = keyboard.nextInt();
                    keyboard.nextLine();
                }
                catch(InputMismatchException ime)
                {
                    System.out.println("Invalid input");
                    continue;
                }
                //Add files, create indices, or quit depending on choice
                switch(choice)
                {
                    case 1:
                        fileNames = addFiles(fileNames, keyboard);
                        break;
                    case 2:
                        //Ensure files were selected before creating indices
                        if(fileNames.size() < 1)
                        {
                            System.out.println("Please add a file before creating indices");
                        }
                        else
                        {
                            System.out.println("Creating indices with " + fileNames.toString() + "\n");
                            //TODO: Upload selected files to GCP using gcloud commands
                            indicesCreated = true;
                        }
                        break;
                    case 3:
                        System.exit(0);
                    default:
                        System.out.println("Invalid input");
                        break;
                }
            }
            

        }
    }

    /**
     * Returns top-N query for a given search term
     * @param term
     */
    public static void topN(String term) throws IOException
    {
        System.out.println("Doing top N with " + term);
        String[] args = new String[] {"/bin/bash", "-c", "ls"};
        Process proc = new ProcessBuilder(args).start();
    }

    /**
     * Searches for a given term in the indices
     * @param term
     */
    public static void search(String term) throws IOException
    {
        System.out.println("Doing search with term " + term);
        String[] args = new String[] {"/bin/bash", "-c", "ls"};
        Process proc = new ProcessBuilder(args).start();
    }

    /**
     * Prompts user to add files to the indices
     * @param fNames list of all files to add
     * @param kb scanner object
     * @return new list of files to add to indices
     */
    public static ArrayList<String> addFiles(ArrayList<String> fNames, Scanner kb)
    {
        //Get all files in current directory and convert to arraylist
        File folder = new File(System.getProperty("user.dir"));
        ArrayList<File> listOfFiles = new ArrayList<>(Arrays.asList(folder.listFiles()));
        int choice = 0;
        //Loop until arraylist is empty or user manually exits
        while(choice != -1 && !listOfFiles.isEmpty())
        {
            System.out.println("\nCurrently chosen files: " + fNames);
            System.out.println("Please select a file(s) to add. Input -1 to confirm:\n");
            int i = 0;
            //List all files not yet selected
            for(File f : listOfFiles)
            {  
                if(!fNames.contains(f.getName()))
                {
                    i++;
                    System.out.println(i + ".\t" + f);
                }
            }

            //If i=0, then no files are valid to select
            if(i == 0)
            {
                System.out.println("No files to add");
                return fNames;
            }

            System.out.println();
            //Validate input
            try
            {
                choice = kb.nextInt();
            }
            catch(InputMismatchException ime)
            {
                System.out.println("Invalid input");
                continue;
            }

            if(choice >= 1 && choice <= i)
            {
                //Check if arraylist already has the given name
                if(!fNames.contains(listOfFiles.get(choice-1).getName()))
                {
                    fNames.add(listOfFiles.remove(choice-1).getName());
                }
                else
                {
                    System.out.println("File name already in list");
                }
            }
            else if(choice != -1)
            {
                System.out.println("Invalid input");
                continue;
            }
        }
        return fNames;        
    }
}