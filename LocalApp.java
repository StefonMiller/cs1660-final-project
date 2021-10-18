import java.util.*;
import java.io.File;
class LocalApp 
{
    public static void main(String args[]) 
    {
        ArrayList<String> fileNames = new ArrayList<String>(10);
        Scanner keyboard = new Scanner(System.in);
        boolean indicesCreated = false;
        
        while(true)
        {
            if(indicesCreated)
            {
                int choice = 0;
                System.out.println("Please select an option:\n" +
                                    "\t1. Search for term\n" +
                                    "\t2. Top-N\n" +  
                                    "\t3. Quit");
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

                System.out.println("Currently chosen files:");
                System.out.println(fileNames + "\n");
                System.out.println("Please select an option:\n" +
                                    "\t1. Add files\n" + 
                                    "\t2. Create indices\n" +
                                    "\t3. Quit");

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
                    case 1:
                        fileNames = addFiles(fileNames, keyboard);
                        break;
                    case 2:
                        if(fileNames.size() < 1)
                        {
                            System.out.println("Please add a file before creating indices");
                        }
                        else
                        {
                            System.out.println("Creating indices with " + fileNames.toString() + "\n");
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

    public static void topN(String term)
    {
        System.out.println("Doing top N with " + term);
    }

    public static void search(String term)
    {
        System.out.println("Doing search with term " + term);
    }

    public static ArrayList<String> addFiles(ArrayList<String> fNames, Scanner kb)
    {
        
        File folder = new File(System.getProperty("user.dir"));
        File[] listOfFiles = folder.listFiles();
        int choice = 0;
        while(choice != -1)
        {
            System.out.println("\nCurrently chosen files: " + fNames);
            System.out.println("Please select a file(s) to add. Input -1 to confirm:\n");
            int i = 0;
            for(File f : listOfFiles)
            {
                i++;
                System.out.println(i + ".\t" + f);
            }

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
                if(!fNames.contains(listOfFiles[choice-1].getName()))
                {
                    fNames.add(listOfFiles[choice-1].getName());
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