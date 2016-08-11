package langanal.UserInterface;

import langanal.sentence.base.Sentence;
import langanal.sentence.processing.SentenceProcessing;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * Created by SteinJac.ao on 8/1/2016.
 */
public class GUI extends JPanel implements ActionListener {

    //youtube vid for multithreading: https://www.youtube.com/watch?v=X5Q-Mecu_64


    private Sentence sentence1;
    private Sentence sentence2;

    public JFrame errMsg;//frame for error mesage dialogue
    public JLabel instructions;//how to operate the application
    public JTextArea inTA1;//input TextArea 1
    public JTextArea inTA2;//iput TextArea 2
    public JButton trigger;//initiates computations
    public JTextArea feedback;//an output TextArea for relativity feedback

    public JProgressBar progress;


    /**
     * @param Pane, JFrame frame will be taken in and given components
     *
     */
    private void addComponents(Container Pane){
        JPanel p = new JPanel();
        p.setBackground(new Color(198, 210, 181));
        p.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10,10,10,10);

        Border border = BorderFactory.createLineBorder(Color.BLACK);
        Color smokeWhite = new Color(245,245,245);

        instructions = new JLabel("This application determines the relevancy of two texts. " +
                "Please enter two English texts in the two fields below");
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        p.add(instructions,c);

        //definitions for inTA1
        inTA1 = new JTextArea(5,0);
        inTA1.setBorder(border);
        inTA1.setBackground(smokeWhite);
        c.gridy = 1;
        JScrollPane scrollInTA1 = new JScrollPane(inTA1);
        scrollInTA1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        p.add(scrollInTA1, c);

        //definitions for inTA2
        inTA2 = new JTextArea(5,0);
        inTA2.setBorder(border);
        inTA2.setBackground(smokeWhite);
        c.gridy = 2;
        JScrollPane scrollInTA2 = new JScrollPane(inTA2);
        scrollInTA2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        p.add(scrollInTA2, c);

        //definitions for trigger
        trigger = new JButton("Compute");
        c.gridy = 3;
        c.fill = GridBagConstraints.CENTER;
        trigger.setMnemonic(KeyEvent.VK_D);
        trigger.setActionCommand("disable");
        trigger.addActionListener(this);
        p.add(trigger,c);

        //definitions for progress
        progress = new JProgressBar();
        progress.setIndeterminate(false);
        progress.setForeground(Color.red);
        progress.setBackground(Color.BLACK);
        c.gridy = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        p.add(progress,c);

        //definitions for feedback
        feedback = new JTextArea(10,0);
        feedback.setBorder(border);
        feedback.setBackground(smokeWhite);
        feedback.setEditable(false);
        c.gridy = 5;
        p.add(feedback,c);

        Pane.add(p);
    }


    /**
     * @param relevancy
     * @return feedback string is given
     */
    private String TextOut(float relevancy){
        return " The two texts scored a " + relevancy + "% relevancy rating" +
                "\n Relevancy is determined by how much in common the words in the sentences possess" +
                "\n English aspects such as definitions, synonyms, antonyms, and part of speech" +
                "\n are weighted in the process" +
                "\n The higher the relevancy rating, the more the two will have in common";
    }

    /**
     * creates the GUI
     */
    private void createAndShowGUI(){
        JFrame frame = new JFrame("English Sentence Relevancy Analysis");
        frame.setSize(440,480);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addComponents(frame);
        frame.pack();
        frame.setVisible(true);
    }

    //defimes the error dialogue to pop up when the following error occurs
    public void errorDialogue(){
        JOptionPane.showMessageDialog(errMsg, "Please check your spelling. \n" +
                "       If error reoccurs, " +
                "then a word you are using is not" +
                "        in our dictionary", "Dictionary Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * @param e listens for the button "trigger" to be clicked
     */
    public void actionPerformed(ActionEvent e){
        if(!inTA1.getText().equals("") && !inTA2.getText().equals("")){
            progress.setIndeterminate(true);
            feedback.setText("");
            process.start();
        }else{
            feedback.setText(" Please enter text in the two text fields above before pressing \"Compute\"");
        }
    }

    /**
     * this thread runs seperate from the GUI to do all the processing so the GUI does not freeze up
     */
    Thread process = new Thread(){
        public void run(){
            try{
                sentence1 = new Sentence(inTA1.getText());
                sentence2 = new Sentence(inTA2.getText());
                float relevancy = SentenceProcessing.calcRelevancy(sentence1, sentence2);
                feedback.setText(TextOut(relevancy));
                progress.setIndeterminate(false);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };

    /**
     * runs the applet
     */
    public static void main (String[] args){
        GUI gui = new GUI();
        javax.swing.SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                gui.createAndShowGUI();
            }
        });
    }

}
