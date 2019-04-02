import javax.swing.JPanel;

import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Panel extends JPanel {
    
    private JMenuBar menuBar;

    public Panel() {
        //setLayout();

        menuBar = new JMenuBar();
        menuBar.add(createFileMenu(new JMenu("File")));
        menuBar.add(createFileMenu(new JMenu("Help")));
    }

    private JMenu createFileMenu(JMenu jMenu) {
        JMenu menu = jMenu;
        JMenuItem exit = new JMenuItem("Exit");

        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                System.exit(0);
            }
        });

        menu.add(exit);
        return menu;
        
    }

    public JMenuBar getMenuBar() {
        return menuBar;
    }
}