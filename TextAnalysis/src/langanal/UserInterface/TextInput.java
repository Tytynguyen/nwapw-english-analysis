package UserInterface;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 * Created by SteinJac.ao on 7/27/2016.
            */
    public class TextInput extends JPanel implements ActionListener{
            protected JTextField textField1;
            protected JTextField textField2;

            public TextInput(){
                super(new GridBagLayout());

                textField1 = new JTextField(20);
                textField1.addActionListener(this);

                textField2 = new JTextField(20);
                textField2 = new JTextField(20);

                //Add componets to this panel
                GridBagConstraints c = new GridBagConstraints();
                c.gridwidth = GridBagConstraints.REMAINDER;

                c.fill = GridBagConstraints.HORIZONTAL;
                add(textField1, c);
                add(textField2, c);

                c.fill = GridBagConstraints.BOTH;
                c.weightx = 1.0;
                c.weighty = 1.0;
            }

            //needs to send the text somewhere
            public void actionPerformed(ActionEvent evt){
                String text1 = textField1.getText();
                String text2 = textField2.getText();
            }

            /**
             * Create the GUI and show it. For thread safety,
             * this method shoudl be invoked form the
             * event dispatch thread.
             */
            private static void createAndShowGUI(){
                //Create and set up the window
                JFrame frame = new JFrame("TextInput");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                //Add contents to the window
                frame.add(new TextInput());

                //Display the window
                frame.pack();
                frame.setVisible(true);
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
