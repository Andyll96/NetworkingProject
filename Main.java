import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;

/*
@author Andy S. Llactahuamani, 500640181
@author Akash Rai, 
@author Danri Chen, 500765982
*/

public class Main {

    public final static int WIDTH = 900, HEIGHT = WIDTH / 12 * 9;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Content Distribution Network");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(WIDTH, HEIGHT));
        frame.requestFocus();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        Panel panel = new Panel();
        panel.setBackground(Color.lightGray);
        frame.add(panel);

        frame.setJMenuBar(panel.getMenuBar());

        frame.setVisible(true);
    }

}