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

    public String text1;
    public String text2;
    public float relevancy;

    public JLabel instructions;//how to operate the application
    public JTextArea inTA1;//input TextArea 1
    public JTextArea inTA2;//iput TextArea 2
    public JButton trigger;//initiates computations
    public JTextArea feedback;//an output TextArea for relativity feedback


    /**
     * @param Pane, JFrame frame will be taken in and given components
     *
     */
    public void addComponents(Container Pane){
        JPanel p = new JPanel();
        //p.setBackground(new Color(156, 110, 92));
        p.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10,10,10,10);

        Border border = BorderFactory.createLineBorder(Color.BLACK);
        Color smokeWhite = new Color(245,245,245);

        instructions = new JLabel("This application determines the relevancy of two texts. " +
                "Please enter two texts in the two fields below");
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        p.add(instructions,c);

        //definitions for inTA1
        inTA1 = new JTextArea(5,0);
        inTA1.setBorder(border);
        inTA1.setBackground(smokeWhite);
        c.gridy = 1;
        p.add(inTA1, c);

        //definitions for inTA2
        inTA2 = new JTextArea(5,0);
        inTA2.setBorder(border);
        inTA2.setBackground(smokeWhite);
        c.gridy = 2;
        p.add(inTA2, c);

        //definitions for trigger
        trigger = new JButton("Compute");
        c.gridy = 3;
        c.fill = GridBagConstraints.CENTER;
        trigger.setMnemonic(KeyEvent.VK_D);
        trigger.setActionCommand("disable");
        trigger.addActionListener(this);
        p.add(trigger,c);

        //definitions for
        feedback = new JTextArea(10,0);
        feedback.setBorder(border);
        feedback.setBackground(smokeWhite);
        feedback.setEditable(false);
        c.gridy = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        p.add(feedback,c);

        Pane.add(p);

    }

    /**
     * @param relevancy
     * @return feedback string is given
     */
    public String TextOut(float relevancy){
        return "" + relevancy;
    }

    /**
     * creates the GUI
     */
    public void createAndShowGUI(){
        JFrame frame = new JFrame("Text Relevancy Application");
        frame.setSize(440,480);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addComponents(frame);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * @param e listens for the button "trigger" to be clicked
     */
    public void actionPerformed(ActionEvent e){
        if(!inTA1.getText().equals("") && !inTA2.getText().equals("")){
            text1 = inTA1.getText();
            text2 = inTA2.getText();
            this.relevancy = SentenceProcessing.calcRelevancy(new Sentence(text1),new Sentence(text2));
            feedback.setText(TextOut(relevancy));
        }
    }

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