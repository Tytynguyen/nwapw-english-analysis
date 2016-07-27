package UserInterface;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 * Created by SteinJac.ao on 7/27/2016.
            */
class TextInput extends JPanel implements ActionListener {

    protected JTextField textField1;
    protected JTextField textField2;

    protected JButton button;

    public String text1;
    public String text2;

    public TextInput(){
        super(new GridBagLayout());

        textField1 = new JTextField(20);
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

        button = new JButton("Compute");
        button.setVerticalTextPosition(AbstractButton.CENTER);
        button.setHorizontalTextPosition(AbstractButton.LEADING); //LEFT
        button.setMnemonic(KeyEvent.VK_D);
        button.setActionCommand("disable");

        //Listen for actions on button
        button.addActionListener(this);

        //Add components to this container, using the default FlowLayout
        add(button);
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

    public void actionPerformed(ActionEvent e){
        if(textField1.getText().equals("")||textField2.getText().equals("")){
        }else{
            text1 = textField1.getText();
            text2 = textField2.getText();
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
