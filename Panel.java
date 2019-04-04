import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

public class Panel extends JPanel {
    
    private static final long serialVersionUID = 1L;

    private JPanel west;
    private JPanel center;
    private JPanel east;

    private JPanel dhtPanel;

    private JButton dhtButton;
    private JButton p2pButton;
    private JButton uploadButton;
    private JButton downloadButton;
    private JButton clientServerExitButton;

    JTextField serverPortField;
    JTextField serverIDField;
    JTextField successorPortField;
    JTextField successorServerIPField;

    JTextField peerServerPortField;
    JTextField serverOneIPField;
    JTextField serverOneMainPortField;

    JTextArea dhtOutput;
    JTextArea p2pOutput;

    private JMenuBar menuBar;

    private String serverPort;
    private String serverID;
    private String successorServerPort;
    private String successorServerIP;

    private String peerServerPort;
    private String serverOneIP;
    private String serverOneMainPort;
    
    public Panel() {
        //sets JPanel's layout to border layout(north, south, EAST, WEST, CENTER)
        setLayout(new BorderLayout());

        //creates, sets, and adds menubar
        menuBar = new JMenuBar();
        menuBar.add(createFileMenu(new JMenu("File")));
        menuBar.add(createDHTMenu(new JMenu("DHT")));
        menuBar.add(createP2PMenu(new JMenu("P2P")));

        //creates Panels
        createDHTPanel();
        createConsolePanel();
        createP2PPanel();

        //Sets Panels to the LEFT, CENTER, and RIGHT side of the Window
        add(west, BorderLayout.WEST);
        add(center, BorderLayout.CENTER);
        add(east, BorderLayout.EAST);

        dhtActionListener();
        p2pActionListener();

        uploadActionListener();
        downloadActionListener();

        exitActionListener();

    }

    private void exitActionListener() {
        clientServerExitButton.addActionListener(new ActionListener(){
        
            @Override
            public void actionPerformed(ActionEvent e) {
                P2PPrint("Client/Server Exit Network");
            }
        });
    }

    private void downloadActionListener() {
        downloadButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                P2PPrint("DOWNLOAD BUTTON PRESSED");
            }
        });
    }

    private void uploadActionListener() {
        uploadButton.addActionListener(new ActionListener(){
        
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = chooser.getSelectedFile();
                    P2PPrint(selectedFile.getName() + selectedFile.getPath());
                }
            }
        });
    }

    private void p2pActionListener() {
        p2pButton.addActionListener(new ActionListener(){
        
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("P2P Press");
                peerServerPort = peerServerPortField.getText();
                serverOneIP = serverOneIPField.getText();
                serverOneMainPort = serverOneMainPortField.getText();
                P2PPrint(peerServerPort + " " + serverOneIP + " " + serverOneMainPort);
            }
        });
    }

    private void dhtActionListener(){
        dhtButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("DHT Press");
                serverPort = serverPortField.getText();
                serverID = serverIDField.getText();
                successorServerPort = successorPortField.getText();
                successorServerIP = successorServerIPField.getText();
                DHTPrint(serverPort + " " + serverID + " " + successorServerPort + " " + successorServerIP);
            }
        });
    }

    private void createConsolePanel() {

        JLabel dhtLabel = new JLabel("DHT Output");
        dhtLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel p2pLabel = new JLabel("P2P Output");
        p2pLabel.setHorizontalAlignment(SwingConstants.CENTER);

        dhtOutput = new JTextArea();
        dhtOutput.setLineWrap(true);
        dhtOutput.setEditable(false);
        p2pOutput = new JTextArea();
        p2pOutput.setLineWrap(true);
        p2pOutput.setEditable(false);

        JScrollPane dhtScroll = new JScrollPane(dhtOutput);
        JScrollPane p2pScroll = new JScrollPane(p2pOutput);
        
        center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        center.add(dhtLabel);
        center.add(dhtScroll);
        center.add(p2pLabel);
        center.add(p2pScroll);        
    }

    private void createP2PPanel() {

        JLabel peerServerPortLabel = new JLabel("Peer Server Port Number: ");
        JLabel serverOneIPLabel = new JLabel("Server One IP: ");
        JLabel serverOneMainPortLabel = new JLabel("Server One Main Port Number: ");

        peerServerPortField = new JTextField();
        peerServerPortField.setColumns(10);
        serverOneIPField = new JTextField();
        serverOneIPField.setColumns(10);
        serverOneMainPortField = new JTextField();
        serverOneMainPortField.setColumns(10);

        p2pButton = new JButton("P2P");
        uploadButton = new JButton("Upload");
        downloadButton = new JButton("Download");
        clientServerExitButton = new JButton("Exit");

        //TODO: MUST REPLACE THIS ARRAY WITH CONTENT NAMES FORM THE DHT
        String[] petStrings = { "Bird", "Cat", "Dog", "Rabbit", "Pig" };
        JComboBox downloadSelection = new JComboBox(petStrings);

        east = new JPanel();
        east.setLayout(new BoxLayout(east, BoxLayout.Y_AXIS));

        JPanel settings = new JPanel();
        settings.setLayout(new FlowLayout());

        JPanel grid = new JPanel();
        grid.setLayout(new GridLayout(4,1,0,20));

        JPanel clientServer = new JPanel();
        clientServer.setLayout(new GridLayout(3,1,0,20));

        JPanel line1 = new JPanel();
        line1.setLayout(new FlowLayout(4,4,4));
        line1.add(peerServerPortLabel);
        line1.add(peerServerPortField);
        grid.add(line1);


        JPanel line2 = new JPanel();
        line2.setLayout(new FlowLayout(4,4,4));
        line2.add(serverOneIPLabel);
        line2.add(serverOneIPField);
        JPanel line3 = new JPanel();
        grid.add(line2);


        line3.setLayout(new FlowLayout(4,4,4));
        line3.add(serverOneMainPortLabel);
        line3.add(serverOneMainPortField);
        grid.add(line3);


        JPanel line4 = new JPanel();
        line4.setLayout(new FlowLayout(4,4,4));
        line4.add(p2pButton);
        grid.add(line4);

        JPanel uploadLine = new JPanel();
        uploadLine.setLayout(new FlowLayout(4,4,4));
        uploadLine.add(uploadButton);
        clientServer.add(uploadLine);

        JPanel downloadLine = new JPanel();
        downloadLine.setLayout(new FlowLayout(4,4,4));
        downloadLine.add(downloadSelection);
        downloadLine.add(downloadButton);
        clientServer.add(downloadLine);

        JPanel exitLine = new JPanel();
        exitLine.setLayout(new FlowLayout(4,4,4));
        exitLine.add(clientServerExitButton);
        clientServer.add(exitLine);

        settings.add(grid);

        east.add(settings);
        east.add(clientServer);
    }

    private void createDHTPanel() {
        //Labels
        JLabel serverPortLabel = new JLabel("Server Port Number: ");
        JLabel serverIDLabel = new JLabel("Server ID: ");
        JLabel successorPortLabel = new JLabel("Successor Server Port Number: ");
        JLabel successorServerIPLabel = new JLabel("Sccuessor Server IP: ");
        
        //TextFields
        serverPortField = new JTextField();
        serverPortField.setColumns(10);

        serverIDField = new JTextField();
        serverIDField.setColumns(10);

        successorPortField = new JTextField();
        successorPortField.setColumns(10);

        successorServerIPField = new JTextField();
        successorServerIPField.setColumns(10);

        dhtButton = new JButton("DHT");

        //creates Jpanels w/ FlowLayout for east and west
        west = new JPanel();
        west.setLayout(new FlowLayout(4,10,10));

        //create DHT panel
        dhtPanel = new JPanel();
        dhtPanel.setLayout(new GridLayout(5,1,0,20));

        JPanel line1 = new JPanel();
        line1.setLayout(new FlowLayout(4,4,4));
        line1.add(serverPortLabel);
        line1.add(serverPortField);
        dhtPanel.add(line1);

        JPanel line2 = new JPanel();
        line2.setLayout(new FlowLayout(4,4,4));
        line2.add(serverIDLabel);
        line2.add(serverIDField);
        dhtPanel.add(line2);

        JPanel line3 = new JPanel();
        line3.setLayout(new FlowLayout(4,4,4));
        line3.add(successorPortLabel);
        line3.add(successorPortField);
        dhtPanel.add(line3);

        JPanel line4 = new JPanel();
        line4.setLayout(new FlowLayout(4,4,4));
        line4.add(successorServerIPLabel);
        line4.add(successorServerIPField);
        dhtPanel.add(line4);

        JPanel line5 = new JPanel();
        line5.setLayout(new FlowLayout(4,4,4));
        line5.add(dhtButton);
        dhtPanel.add(line5);

        west.add(dhtPanel);
    }

    private JMenu createP2PMenu(JMenu jMenu) {
        JMenu menu = jMenu;
        //TODO: ADD P2P MENUITEMS
        return menu;
    }

    private JMenu createDHTMenu(JMenu jMenu) {
        JMenu menu = jMenu;
        //TODO: ADD DHT MENUITEMS
        return menu;
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

    public void DHTPrint(String message) {
        dhtOutput.append(message + "\n");
    }

    public void P2PPrint(String message) {
        p2pOutput.append(message + "\n");
    }

    public JMenuBar getMenuBar() {
        return menuBar;
    }

    public String getDHTInfo() {
        return serverPort + "," + serverID + "," + successorServerPort + "," + successorServerIP;
    }

    public String getP2PInfo() {
        return peerServerPort + "," + serverOneIP + "," + serverOneMainPort;
    }
}