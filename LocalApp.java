import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.nio.file.Files;

public class LocalApp
{
   public static void main(String[] args) 
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
    JFrame frame;
    JPanel containerPanel;
    JPanel searchPanel;
    JPanel topNPanel;
    JPanel indexPanel;
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
        textArea.setText("Indices created!");
        panel4.add(textArea);
        indexPanel.add(panel4);
        indexPanel.add(panel2);
        indexPanel.add(panel3);

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
        searchPanelButton = new JButton("Search"); // Button is a Component
        searchPanelButton.addActionListener(this); 
        panel7.add(searchPanelButton);
        searchPanel.add(panel5);
        searchPanel.add(panel6);
        searchPanel.add(panel7);

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

    }

    /**
     * Button event handler
     * @Param e: Event registered
     */
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(indexButton))
        {
            frame.remove(containerPanel);
            frame.add(indexPanel);
            frame.validate();
        }
        //If the user clicked the upload button, call uploadFiles()
        else if(e.getSource().equals(searchButton))
        {
            frame.remove(indexPanel);
            frame.add(searchPanel);
            frame.validate();
        }
        else if(e.getSource().equals(topNButton))
        {
            frame.remove(indexPanel);
            frame.add(topNPanel);
            frame.validate();
        }
        else if(e.getSource().equals(searchPanelButton))
        {
            search(searchText.getText());
            //TODO: Switch frame to results panel

        }
        else if(e.getSource().equals(topNPanelButton))
        {
            try
            {
                int n = Integer.parseInt(topNText.getText());
                topN(Integer.parseInt(topNText.getText()));
                //TODO: Switch frame to results panel
            }
            catch(NumberFormatException nfe)
            {
                JOptionPane.showMessageDialog(frame, "Please enter a number");
            }
        }
    }

    /**
     * Searches the results of inverted indices for the matching term
     */
    public void search(String term)
    {
        System.out.println("Searching for term " + term);
    }

    /**
     * Finds the top N words in the inverted indices
     */
    public void topN(int n)
    {
        System.out.println("Getting top " + Integer.toString(n) + " entries");
    }
}