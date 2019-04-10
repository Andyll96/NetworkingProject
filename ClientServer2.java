import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientServer2 {

    int clientServerPort;
    int[] serverPorts = new int[4];
    String[] serverIPs = new String[4];

    DatagramSocket peerToPoolConnection;

    // HTTP Status Codes
    final int okStatus = 200;
    final int badRequestStatus = 400;
    final int notFoundStatus = 404;
    final int notSupportedStatus = 505;

    public ClientServer2(String serverIP, int serverPort, int peerServerPort) {
        this.clientServerPort = peerServerPort;
        this.serverPorts[0] = serverPort;
        this.serverIPs[0] = serverIP;

        try {
            peerToPoolConnection = new DatagramSocket();
            clientInit();
        } catch (Exception e) {
            System.out.println("UDP Connection Can't be Established");
        }
    }

    private void clientInit() throws IOException {
        String header;
        sendToServer("GET ALL IP", serverIPs[0], serverPorts[0]);
        header = receiveServerData();

        Scanner scan = new Scanner(header);
        String status = scan.next();

        scan.next();
        scan.next();
        scan.next();
        scan.next();
        scan.next(); // Get All, IP my Port, MyIP

        if (status.equals("200")) {
            System.out.println("Client Initialized");
        }

        for (int i = 0; i < 4; i++) {
            serverIPs[i] = scan.next();
            serverPorts[i] = Integer.parseInt(scan.next());
        }
    }

    private String receiveServerData() throws IOException {
        byte[] receiveData = new byte[1024];
        DatagramPacket recievePacket = new DatagramPacket(receiveData, receiveData.length);
        peerToPoolConnection.receive(recievePacket);
        return new String(recievePacket.getData());
    }

    private void sendToServer(String message, String serverIP, int serverPort) throws IOException {
        byte[] sendData = new byte[1024];
        sendData = message.getBytes();
        InetAddress internetAddress = InetAddress.getByName(serverIP); // Get the Inet address of the server.
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, internetAddress, serverPort); // The packet to be sent to the server.
        peerToPoolConnection.send(sendPacket); // Send the packet using UDP.
    }

    public void uploadData(int dhtServerID, String input) {
    }

    public void query(int dhtServerID, String fileName) throws UnknownHostException, IOException {
        String contentIP;
        String statusCode;
        String message = "Query" + fileName;
        sendToServer(message, serverIPs[dhtServerID], serverPorts[dhtServerID]);
        message = receiveServerData();
        Scanner scan = new Scanner(message);
        statusCode = scan.next();

        if (statusCode.equals("404")) {
            System.out.println("Content Not Found");
        }

        else if (statusCode.equals("200")) {
            System.out.println("Content Found, IP given");
            scan = new Scanner(message);
            scan.next(); // status code
            contentIP = scan.next(); // The IP of the client who has the file.

            // Create the HTTP GET request.
            String HTTPRequest = createHTTPRequest("GET", fileName, "Close",
                    InetAddress.getByName(contentIP).getHostName(), "image/jpeg", "en-us");
            message = connectToPeerServer("OPEN " + fileName, contentIP, clientServerPort); // Connect to the server of
                                                                                            // the client who has the
                                                                                            // file.
            scan = new Scanner(message);
            statusCode = scan.next();
            int newPort = scan.nextInt();

            // If the status code is 200 OK, then the request was sent successfully.
            if (statusCode.equals("200")) {
                System.out.println("FROM PEER SERVER -> New Connection Open On Port " + newPort);
                System.out.println("--HTTP Request Sent to Server-- START\n" + HTTPRequest
                        + "--HTTP Request Sent to Server--END\n");
                connectToUniqueServer(fileName, HTTPRequest, contentIP, newPort);
            }
        } // Server has sent the ip
    }

    public void connectToUniqueServer(String fileName, String httpRequest, String ip, int port) throws UnknownHostException, IOException {
        Socket connectToUniqueServer = new Socket(ip,port);

        OutputStream outToServer = connectToUniqueServer.getOutputStream();
        DataOutputStream out = new DataOutputStream(outToServer);
        out.writeUTF(httpRequest);

        InputStream in = connectToUniqueServer.getInputStream();
        DataInputStream dis = new DataInputStream(in);
        int length = dis.readInt();
        byte[] data = new byte[length];
        if (length>0) {
            dis.readFully(data);
        }
        connectToUniqueServer.close();

        String head = new String(data);
        Scanner scan = new Scanner(head);
        String repStat = scan.nextLine() + "\n";
        String temp;

        if (repStat.contains("HTTP/1.1 200 OK")) {
            repStat = getHTTPResponse(scan, repStat);
            File output = new File(fileName + ".jpeg");
            int fileSize = data.length - repStat.getBytes().length;
            byte[] backToBytes = new byte[fileSize];

            for (int i = repStat.getBytes().length; i < data.length; i++) {
                backToBytes[i - repStat.getBytes().length] = data[i];
            }

            FileOutputStream fos = new FileOutputStream(output);
            fos.write(backToBytes);
            fos.close(); // Close the fileoutput stream.
        } 
        else if (repStat.contains("HTTP/1.1 400 Bad Request")) {
            repStat = getHTTPResponse(scan, repStat);
        } 
        else if (repStat.contains("HTTP/1.1 404 Not Found")) {
            repStat = getHTTPResponse(scan, repStat);
        } 
        else if (repStat.contains("HTTP/1.1 505 HTTP Verson Not Supported")) {
            repStat = getHTTPResponse(scan, repStat);
        }

        System.out.println("--HTTP Responce Got From Server-- START\n" + repStat + "--HTTP Responce Got From Server--END\n");
    }

    public String getHTTPResponse(Scanner scan, String rep) {
        String temp;
        while (scan.hasNext()) {
            temp = scan.nextLine() + "\r\n";
            rep += temp;
            if (temp.equals("\r\n")) {
                break;
            }
        }
        return rep;
    }

    private String connectToPeerServer(String message, String contentIP, int clientServerPort)
            throws UnknownHostException, IOException {
        Socket connectToPeerServer = new Socket(contentIP, clientServerPort); // connect to peer server.
			
        OutputStream outToServer = connectToPeerServer.getOutputStream();
        DataOutputStream out = new DataOutputStream(outToServer);
        out.writeUTF(message);
        DataInputStream in = new DataInputStream(connectToPeerServer.getInputStream());
        message = in.readUTF();
        connectToPeerServer.close(); // Close the connection to the peer server.
        return message;
    }

    private String createHTTPRequest(String request, String object, String connection, String host, String acceptType,
            String acceptLan) {
                String req = "";
                req += request + " /" + object + ".jpeg" + " HTTP/1.1\r\n";
                req += "Host: " + host + "\r\n";
                req += "Connection: " + connection + "\r\n";
                req += "Accept: " + acceptType + "\r\n";
                req += "Accept-Language: " + acceptLan + "\r\n\r\n";
                return req;    }

    public void exit() throws IOException {
        byte[] recieveData = new byte[1024];
        String statusCode;
        String message = "Exit " + serverPorts[0] + " " + serverPorts[1] + " " + serverPorts[2] + " " + serverPorts[3];
        sendToServer(message, serverIPs[0], serverPorts[0]);
        message = receiveServerData();
        peerToPoolConnection.close();
        Scanner scan = new Scanner(message);
        statusCode = scan.next();

        if (statusCode.equals("200")) {
            System.out.println("All contents removed");
        }
        System.exit(0);
	}
}