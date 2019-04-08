import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPSocket {

    String clientIP;
    int clientPortNum;

    DHTServer server;

    DatagramSocket udpSocket;

    Thread udpThread;    

    public UDPSocket(String clientIP, int clientPortNum, DHTServer server) {
        this.clientIP = clientIP;
        this.clientPortNum = clientPortNum; //this is the unique UDP Port Number, will be used to open unique UDP Port
        this.server = server;

        try {
            udpSocket = new DatagramSocket(clientPortNum); //dedicated UDPSocket
        } catch (Exception e) {
            this.server.panel.DHTPrint("UDP Port Not Available For Client: " + server.notFoundStatus);
        }

        initUdpThread();
    }

    private void initUdpThread() {
        udpThread = new Thread(new Runnable(){
        
            @Override
            public void run() {
                String clientMessage;
                byte[] clientDataBuffer = new byte[1024];
                while (true) {
                    try {
                        server.panel.DHTPrint("Client's Dedicated UDP Thread Running");
                        server.panel.DHTPrint("Waiting for commands from client on port " + clientPortNum);

                        DatagramPacket receivedPacket = new DatagramPacket(clientDataBuffer, clientDataBuffer.length);
                        udpSocket.receive(receivedPacket);
                        clientMessage = new String(receivedPacket.getData());
                        server.panel.DHTPrint("MESSAGE FROM CLIENT: " + clientMessage);
                        String[] messageComponents = clientMessage.split(" ");

                        if (messageComponents[0] == "QUERY") {
                            String filename = messageComponents[1];
                            String filesIPAddress = server.records.get(filename);
                            if(filesIPAddress == null){ //if you can't find the file
                                server.panel.DHTPrint("Error: " + server.notFoundStatus);
                                server.sendToClient(receivedPacket.getAddress().getHostAddress(), receivedPacket.getPort(), server.notFoundStatus + server.serverIPAddress);
                            } else {
                                server.panel.DHTPrint("MESSAGE TO CLIENT: " + server.okStatus + server.serverIPAddress);
                                server.sendToClient(clientIP, receivedPacket.getPort(), server.okStatus + " " + filesIPAddress);
                            }
                        } else if (messageComponents[0] == "UPLOAD") {
                            String filename = messageComponents[1];
                            server.records.put(filename, clientIP);
                            server.panel.DHTPrint("MESSAGE TO CLIENT: " + server.okStatus);
                            server.sendToClient(clientIP, receivedPacket.getPort(), server.okStatus + " ");
                            server.records.put(filename, clientIP);
                        } else if (messageComponents[0] == "EXIT") {
                            clientMessage = messageComponents[0] + " " + clientIP + " " + receivedPacket.getPort();
                            for (int i = 1; i <= 4; i++) {
                                clientMessage += messageComponents[i];
                            }
                            server.panel.DHTPrint("MESSAGE TO SUCCESSOR SERVER: ");
                            server.sendToSuccessor(clientMessage);
                        }
                    } catch (Exception e) {
                        server.panel.DHTPrint("Error in Client's Dedicated UDP Thread");
                    }
                }
            }
        });
        udpThread.start();
        server.panel.DHTPrint("Initializing Clients Dedicated UDP Thread...");
    }

    public void kill() {
        udpThread.stop();
        udpSocket.close();
	}
}