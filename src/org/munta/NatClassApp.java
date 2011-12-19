package org.munta;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class NatClassApp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        final JFrame frame = new JFrame("Show Message Dialog");
        JButton button = new JButton("Click Me");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                JOptionPane.showMessageDialog(frame, "The NatClass Next Generation 2.0 GT-Ex Turbo Limited Edition");
            }
        });
        frame.add(button);
        frame.setSize(400, 400);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
