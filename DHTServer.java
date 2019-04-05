import java.io.DataInputStream;
import java.net.DatagramPacket;
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

    public DHTServer(String serverPort, String serverID, String successorServerPort, String successorServerIP,
            Panel panel) {
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
        this.panel.DHTPrint("Server Info:\n" + "\tServer Port Number : " + this.serverPort + "\n\tServer ID: "
                + this.serverID + "\n\tServer IP Address: " + serverIPAddress + "\n");
        this.panel.DHTPrint("Successor Server Info:\n" + "\tSuccessor Server Port Number: " + this.successorServerPort
                + "\n\tSuccessor Server ID: " + this.successorID + "\n\tSuccessor Server IP Address: "
                + this.successorServerIP + "\n");

        try {
            //essentially the welcome socket
            tcpSocket = new ServerSocket(this.serverPort);
            if (this.serverID == 1) {
                udpSocket = new DatagramSocket(this.serverPort);
            }

            //TODO: init the udpSocket if the Server ID = 1
            //Defines and starts Threads for TCP and UDP connections
            initUdpThread();
            initTcpThread();
        } catch (Exception e) {
            panel.DHTPrint("Port Number Not Available");
        }

    }
    
    //Creates a UDP Socket for communication with the client
    private void initUdpThread() {
        udpThread = new Thread(new Runnable(){
        
            @Override
            public void run() {
                while (true) {

                    String clientServerMessage; //message from the client/Server
                    byte[] clientDataBuffer = new byte[1024];

                    try {
                        panel.DHTPrint("UDP Thread Running\n");
                        
                        //Declares packet variable to store receieved Packet 
                        DatagramPacket receivedPacket = new DatagramPacket(clientDataBuffer, clientDataBuffer.length);
                        udpSocket.receive(receivedPacket); //receives and stores data in receivedPacket Variable

                        clientServerMessage = new String(receivedPacket.getData()); //extracts message from UDP packet
                        panel.DHTPrint("MESSAGE FROM CLIENT: " + clientServerMessage);
                        
                        if (clientServerMessage.contains("GET ALL IP")) {
                            int uniquePortNumber = reserveUniqueUDPPort();
                        }
                        
                    } catch (Exception e) {
                        panel.DHTPrint("Error in the UDP Thread");
                    }
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
                        panel.DHTPrint("RECEIVED MESSAGE FROM PREDECESSOR: " + message);

                        if (message.contains("GET ALL IP") && serverID == 1) { //where server sends IP Addresses of all servers to the p2p client
                            String listOfIPMessage = okStatus + message;
                            panel.DHTPrint("MESSAGE TO CLIENT: " + listOfIPMessage);
                            //sendToClient();

                        } else if (message.contains("GET ALL IP")) { //you get a message to add your IP Address to the received message and send it to your successor.

                        } else if (condition) {

                        } else if (condition) {

                        }

                        connectionToPredecessor.close();
                    } catch (Exception e) {
                        panel.DHTPrint("Error in the TCP Thread");
                    }
                }

            }
        });
        tcpThread.start();
        panel.DHTPrint("Initializing TCP Thread...");
    }

    //reserves an open UDP port for the client's reference
    protected int reserveUniqueUDPPort() {
        int testPortNum = serverPort + 1; //try the port after welcome port
        boolean searching = true;
        while (searching == true) {
            try {
                //We only want to test for availability, we don't want to keep open it YET
                DatagramSocket newPort = new DatagramSocket(testPortNum);
                searching = false;
                newPort.close();
                break;
            } catch (Exception e) {
                testPortNum++;
            }
        }
		return testPortNum;
	}
    //used when you have all the server's IP addresses and you want to send them to the client
	protected void sendToClient(String clientIP, int clientPort, String message) {
	}
}