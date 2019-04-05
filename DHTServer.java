import java.io.DataInputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class DHTServer {
    
    int serverPort;
    int serverID;
    int successorServerPort;
    String successorServerIP;

    String serverIPAddress;
    int successorID;

    //HTTP Status Codes
    final int okStatus = 200;
    final int badRequestStatus = 400;
    final int notFoundStatus = 404;
    final int notSupportedStatus = 505;

    Thread tcpThread;
    Thread udpThread;

    ServerSocket tcpSocket;
    DatagramSocket udpSocket;

    Panel panel;

    public DHTServer(String serverPort, String serverID, String successorServerPort, String successorServerIP, Panel panel) {
        this.panel = panel;

        //Initializes servers variables
        this.serverPort = Integer.parseInt(serverPort);
        this.serverID = Integer.parseInt(serverID);
        try {
            this.serverIPAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            this.panel.DHTPrint("Can't determind your IP address");
        }

        //Initializes successor servers variables
        this.successorServerPort = Integer.parseInt(successorServerPort);
        this.successorServerIP = successorServerIP;
        switch (this.serverID) {
        case 4:
            this.successorID = 1;
            break;

        default:
            this.successorID = this.serverID + 1;
            break;
        }

        //Prints Variable Initialization 
        this.panel.DHTPrint("Initializing Server Variables...");
        this.panel.DHTPrint("Server Info:\n" + "\tServer Port Number : " + this.serverPort + "\n\tServer ID: " + this.serverID + "\n\tServer IP Address: " + serverIPAddress + "\n");
        this.panel.DHTPrint("Successor Server Info:\n" + "\tSuccessor Server Port Number: " + this.successorServerPort + "\n\tSuccessor Server ID: " + this.successorID + "\n\tSuccessor Server IP Address: " + this.successorServerIP + "\n");

        try {
            //essentially the welcome socket
            tcpSocket = new ServerSocket(this.serverPort);
            
            //TODO: init the udpSocket if the Server ID = 1
            initTcpThread();
            //initUdpThread();
        } catch (Exception e) {
            panel.DHTPrint("Port Number Not Available");
        }
        //Defines and starts Threads for TCP and UDP connections

    }
    
    private void initUdpThread() {
        udpThread = new Thread(new Runnable(){
        
            @Override
            public void run() {
                try {
                    panel.DHTPrint("UDP Thread Running\n");
                } catch (Exception e) {
                    panel.DHTPrint("Problem with the UDP Thread");
                }
            }
        });
        udpThread.start();
        panel.DHTPrint("Initializing UDP Thread...");
    }

    private void initTcpThread() {
        tcpThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) { //like Game Loop

                    String message;
                    try {
                        panel.DHTPrint("TCP Thread Running");
                        panel.DHTPrint("Waiting for message from predecessor on port " + serverPort);
                        
                        //listens for messages from the predecessor, creates and returns a socket that'll be used for communication 
                        Socket connectionToPredecessor = tcpSocket.accept();
                        panel.DHTPrint("Connected to " + connectionToPredecessor.getRemoteSocketAddress());

                        //gets the input from the socket, the message from the predecessor, and saves the message/data
                        DataInputStream data = new DataInputStream(connectionToPredecessor.getInputStream());
                        message = data.readUTF();
                        panel.DHTPrint("MESSAGE FROM PREDECESSOR SERVER: " + message);


                        connectionToPredecessor.close();
                    } catch (Exception e) {
                        panel.DHTPrint("Problem with the TCP Thread");
                    }
                }

                
            }
        });
        tcpThread.start();
        panel.DHTPrint("Initializing TCP Thread...");
    }
}