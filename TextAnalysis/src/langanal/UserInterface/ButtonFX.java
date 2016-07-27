package UserInterface;

/**
 * Created by SteinJac.ao on 7/27/2016.
 */
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class ButtonFX extends JPanel implements ActionListener{
    protected JButton button;

    public ButtonFX(){
        button = new JButton("Compute");
        button.setVerticalTextPosition(AbstractButton.CENTER);
        button.setHorizontalTextPosition(AbstractButton.LEADING); //LEFT
        button.setMnemonic(KeyEvent.VK_D);
        button.setActionCommand("disable");


        //Listen for actions on buttons 1 and 3
        button.addActionListener(this);

        //Add components to this container, using the default FlowLayout
        add(button);
    }

    public void actionPerformed(ActionEvent e){

    }
}
