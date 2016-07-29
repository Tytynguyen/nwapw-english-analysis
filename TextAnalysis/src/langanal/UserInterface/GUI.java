package langanal.UserInterface;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import langanal.word.processing.WordProcessing;
/**
 * Created by SteinJac.ao on 7/27/2016.
            */
@SuppressWarnings("serial")
class GUI extends JPanel implements ActionListener {

    public String text1;
    public String text2;
    public float relevancy;

    protected JTextField textField1;
    protected JTextField textField2;

    protected JTextArea textArea;
    protected JTextArea appInfo; //breif description of what to do and what the application does

    protected JButton button;

    public GUI(){
        super(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;

        //text area for description
        String description = "   Hello, this is an application designed to determine \n   the relativity of two words. " +
                "Please enter one word \n   each in the areas below and press \"Compute\" ";
        appInfo = new JTextArea(description,3,15);
        appInfo.setEditable(false);
        JScrollPane scrollPane2 = new JScrollPane(appInfo);
        add(scrollPane2, c);

        //text input fields
        textField1 = new JTextField(20);
        textField2 = new JTextField(20);
        add(textField1, c);
        add(textField2, c);

        //button
        button = new JButton("Compute");
        button.setVerticalTextPosition(AbstractButton.CENTER);
        button.setHorizontalTextPosition(AbstractButton.LEADING); //LEFT
        button.setMnemonic(KeyEvent.VK_D);
        button.setActionCommand("disable");
        button.addActionListener(this);
        add(button);

        //text area for output
        textArea = new JTextArea(TextOutput(relevancy),5,21);
        textArea.setEditable(false);
        JScrollPane scrollPane1 = new JScrollPane(textArea);
        add(scrollPane1, c);
    }


    public static String TextOutput(float relevancyIn){
        return " Percent Relevant: " + relevancyIn + "%" +
                "\n Note: The higher the percentage, \n the more the two words have in common" +
                "\n The lower the percentage, \n the less the two words have in common";
    }


    /**
     * Create the GUI and show it. For thread safety,
     * this method shoudl be invoked form the
     * event dispatch thread.
     */
    private static void createAndShowGUI(){
        //Create and set up the window
        JFrame frame = new JFrame("GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(new GUI());

        //Display the window
        frame.pack();
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e){
        if(textField1.getText().equals("")||textField2.getText().equals("")){
        }else {
            text1 = textField1.getText();
            text2 = textField2.getText();
            this.relevancy = WordProcessing.compareWords(text1, text2);
            textArea.setText(TextOutput(relevancy));
        }
    }

    public static void main(String[] args){
        //Schedule a job for the event dispatch thread:
        //creating and shwoing theis application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                        createAndShowGUI();
                    }
        });
    }
}
