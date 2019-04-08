import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

public class DHTServer {

    int serverPort;
    int serverID;

    int successorServerPort;
    String successorServerIP;

    String serverIPAddress;
    int successorID;

    // HTTP Status Codes
    final int okStatus = 200;
    final int badRequestStatus = 400;
    final int notFoundStatus = 404;
    final int notSupportedStatus = 505;

    Thread tcpThread;
    Thread udpThread;

    ServerSocket tcpSocket;
    DatagramSocket udpSocket;

    // this server's list of UDP Sockets, each dedicated to a connected client
    ArrayList<UDPSocket> clientsUdpSockets = new ArrayList<UDPSocket>();
    //records(filename, IPAddress)
    Hashtable<String, String> records = new Hashtable<String, String>();

    Panel panel; // reference to the GUI Panel
    DHTServer server; // reference to this DHT server

    public DHTServer(String serverPort, String serverID, String successorServerPort, String successorServerIP, Panel panel) {
        this.panel = panel;
        this.server = this;

        // Initializes servers variables
        this.serverPort = Integer.parseInt(serverPort);
        this.serverID = Integer.parseInt(serverID);
        try {
            this.serverIPAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            this.panel.DHTPrint("Can't determine your IP address: " + badRequestStatus);
        }

        // Initializes successor servers variables
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

        // Prints Variable Initialization
        this.panel.DHTPrint("Initializing Server Variables...");
        this.panel.DHTPrint("Server Info:\n" + "\tServer Port Number : " + this.serverPort + "\n\tServer ID: " + this.serverID + "\n\tServer IP Address: " + serverIPAddress + "\n");
        this.panel.DHTPrint("Successor Server Info:\n" + "\tSuccessor Server Port Number: " + this.successorServerPort + "\n\tSuccessor Server ID: " + this.successorID + "\n\tSuccessor Server IP Address: " + this.successorServerIP + "\n");

        try {
            // essentially the welcome socket
            tcpSocket = new ServerSocket(this.serverPort);
            if (this.serverID == 1) {
                udpSocket = new DatagramSocket(this.serverPort);
            }

            // Defines and starts Threads for TCP and UDP connections
            initUdpThread();
            initTcpThread();
        } catch (Exception e) {
            panel.DHTPrint("Port Number Not Available: " + badRequestStatus);
        }

    }

    // Creates a UDP Socket for communication with the client
    private void initUdpThread() {
        udpThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {

                    String clientMessage; // message from the client/Server
                    byte[] clientDataBuffer = new byte[1024];

                    try {
                        // Declares packet variable to store receieved Packet
                        DatagramPacket receivedPacket = new DatagramPacket(clientDataBuffer, clientDataBuffer.length);
                        udpSocket.receive(receivedPacket); // receives data from the udpSocket, store in received Packet

                        clientMessage = new String(receivedPacket.getData()); // extracts message from UDP packet
                        panel.DHTPrint("MESSAGE FROM CLIENT: " + clientMessage);

                        if (clientMessage.contains("GET ALL IP")) { //when a client wants to join, they initialize by sending GET ALL IP message
                            int uniquePortNumber = reserveUniqueUDPPort(); //search for an available port number 
                            clientsUdpSockets.add(new UDPSocket(receivedPacket.getAddress().getHostAddress(), uniquePortNumber, server)); // creates unique UDP Port for initializing client w/ the reserved port number. This UDP Socket will listen for client Commands (QUERY, UPLOAD, KILL)
                            clientMessage = "GET ALL IP" + receivedPacket.getPort() + receivedPacket.getAddress().getHostAddress() + " " + serverIPAddress + " " + uniquePortNumber; //the successor servers will append their IP Addresses and the unique UDP Port Number they will create
                            panel.DHTPrint("MESSAGE SENT TO SUCCESSOR SERVER: " + clientMessage);
                            sendToSuccessor(clientMessage); //message's appended with server details
                        }

                    } catch (Exception e) {
                        panel.DHTPrint("Error in the UDP Thread");
                    }
                }
            }
        });
        panel.DHTPrint("Initializing UDP Thread...");
        panel.DHTPrint("UDP Thread Running");
        panel.DHTPrint("Waiting for message from client on port " + serverPort + "\n");
        udpThread.start();
    }

    // Creates a TCP Socket to listen for communication from the predecessor server
    private void initTcpThread() {
        tcpThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) { //like Game Loop

                    String message;
                    try {
                        //listens for messages from the predecessor, creates and returns a socket that'll be used for communication 
                        Socket connectionToPredecessor = tcpSocket.accept();
                        panel.DHTPrint("Connected to " + connectionToPredecessor.getRemoteSocketAddress());

                        //gets the input from the socket, the message from the predecessor, and saves the message/data
                        DataInputStream data = new DataInputStream(connectionToPredecessor.getInputStream());
                        message = data.readUTF();
                        panel.DHTPrint("RECEIVED MESSAGE FROM PREDECESSOR: " + message);

                        if (message.contains("GET ALL IP") && serverID == 1) { //message has made it's rounds through the server pool, send info w/ all IP Addresses to client
                            String[] messageComponents = message.split(" ");
                            String completeListOfIps = okStatus + " " + message;
                            panel.DHTPrint("MESSAGE TO CLIENT: " + completeListOfIps);
                            sendToClient(messageComponents[1], Integer.parseInt(messageComponents[2]), completeListOfIps);
                        } 
                        else if (message.contains("GET ALL IP")) { //you get a message to add your IP Address to the received message and send it to your successor.
                            String[] messageComponents = message.split(" ");
                            int uniquePortNumber = reserveUniqueUDPPort();
                            clientsUdpSockets.add(new UDPSocket(messageComponents[2], uniquePortNumber, server));
                            message += serverIPAddress + " " + uniquePortNumber;
                            panel.DHTPrint("MESSAGE TO SUCCESSOR: " + message);
                            sendToSuccessor(message);
                        } 
                        else if (message.contains("EXIT") && serverID == 1) { 
                            exitClient(message);
                            String[] messageComponents = message.split(" ");
                            message = okStatus + "";
                            panel.DHTPrint(message);
                            sendToClient(messageComponents[1], Integer.parseInt(messageComponents[2]), message);
                        }
                        else if (message.contains("EXIT")) { //removes the client from the server
                            exitClient(message); //kills the dedicated udp port for the client and removes records
                            panel.DHTPrint("MESSAGE TO SUCCESSOR: " + message);
                            sendToSuccessor(message);
                        }

                        connectionToPredecessor.close();
                    } catch (Exception e) {
                        panel.DHTPrint("Error in the TCP Thread");
                    }
                }

            }
        });
        panel.DHTPrint("Initializing TCP Thread...");
        panel.DHTPrint("TCP Thread Running");
        panel.DHTPrint("Waiting for message from predecessor on port " + serverPort);
        tcpThread.start();
    }

    protected void exitClient(String message) {
        String[] messageComponents = message.split(" ");
        String clientIP = messageComponents[1];
        String[] serverPortList = Arrays.copyOfRange(messageComponents, 3, 7);
        int portNum = 0;

        //gets port number for client in current server
        for (int i = 0; i < serverID; i++) {
            portNum = Integer.parseInt(serverPortList[i]);
        }

        //kills dedicated thread and removes udp socket from list
        for (int i = 0; i < clientsUdpSockets.size(); i++) {
            if (clientsUdpSockets.get(i).clientIP.equals(clientIP)
                    && clientsUdpSockets.get(i).clientPortNum == portNum) {
                panel.DHTPrint("Socket " + clientsUdpSockets.get(i) + " Terminated");
                clientsUdpSockets.get(i).kill();
                panel.DHTPrint("Socket removed from clientsUdpSockets List");
                clientsUdpSockets.remove(i);
                break;
            }
        }

        //removes records associated with client
        Set<String> keys = records.keySet();
        Iterator<String> itr = keys.iterator();

        while (itr.hasNext()) {
            String key = (String) itr.next();
            if (records.get(key).equals(clientIP)) {
                records.remove(key);
            }
        }
    }

    // reserves an open UDP port for the client's reference
    protected int reserveUniqueUDPPort() {
        int testPortNum = serverPort + 1; // try the port after welcome port
        boolean searching = true;
        while (searching == true) {
            try {
                // We only want to test for availability, we don't want to keep open it YET
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

    // used when you have all the server's IP addresses and you want to send them to the client
    protected void sendToClient(String clientIP, int clientPort, String message) throws UnknownHostException, IOException {
        byte[] data = new byte[1024];
        data = message.getBytes();
        InetAddress ipAddress = InetAddress.getByName(clientIP);
        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, clientPort);
        udpSocket.send(packet);
    }

    protected void sendToSuccessor(String message) throws UnknownHostException, IOException {
        Socket successorSocket = new Socket(successorServerIP, successorServerPort);
        DataOutputStream output = new DataOutputStream(successorSocket.getOutputStream());
        output.writeUTF(message);
        successorSocket.close();
    }
}