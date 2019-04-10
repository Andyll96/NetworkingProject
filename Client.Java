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

public static class Client {

    int clientServerPort;
    String[] filename;
    int[] serverPortNumbers = new int[4];
    String[] serverIPs = new String[4];
    DatagramSocket clientUDPSocket;

    public Client(String severOneIP, int serverOnePort, int peerServerPort, Panel panel) {
    
        this.peerSeverPort  = peerServerPort;
        this.serverIPs[0] = serverOneIP;
        this.severPortNumbers[0] = serverOnePort;
        this.panel = panel;

        try {
            // Create the client's UDP socket and init
            clientUDPSocket = new DatagramSocket();
            init();
        }
        catch (Exception e) {
        }

    }

    public void init() throws Exception {

        String header;
        String statusCode;
        data2Server("GET ALL IP ADRESSES", serverIPs[0], serverPortNumbers[0]);
        header = receiveDataFromServer();
        Scanner scan = new Scanner(header);
        statusCode = scan.next();
        scan.next();
        scan.next();
        scan.next();
        scan.next();
        scan.next(); // Get All, IP my Port, MyIP

        if(statusCode.equals("200")) {
            this.panel.DHTprint("Client Initialized");
        } 

        for (int i=0; i<4; i++) {
            serverIPs[i] = scan.next();
            serverPortNumbers[i] = Integer.ParseInt(scan.next());
        }

    }

    public void upload(int id, String fileName) throws Exception {
        String statusCode;
        String message = "Upload" + fileName + " " + InetAddress.getLocalHost().getHostAddresss();
        data2Server(message, serverIPs[id], severPortNumbers[id]);
        message = receiveDataFromServer();
        Scanner scan = new Scanner(mesaage);
        statusCode = scan.next();

        if (statusCode.equals("200")) {
            this.panel.DHTprint("File added to DHT");
        }
    }

    public void exit() throws Exception {
        byte recieveData = new byte[1024];
        String statusCode;
        String message = "Exit " + serverPortNumbers[0] + " " + serverPortNumbers[1] + " " + serverPortNumbers[2] + " " + serverPortNumbers[3];
        data2Server(message, serverIPs[0], serverPortNumber[0]);
        message = receiveDataFromServer();
        clientUDPSocket.close();
        Scanner scan = new Scanner(message);
        statusCode = scan.next();

        if (statusCode.equals("200")) {
            this.panel.DHTprint("All contents removed");
        }
        system.exit(0);
    }

    public void query(int id, String fileName) throws Exception {
        String client2IP;
        String statusCode;
        String message = "Query" + filename;
        data2Server(message, serverIPs[id], serverPortNumber[id]);
        message = receiveDataFromServer();
        Scanner scan = new Scanner(message);
        statusCode = scan.next();

        if (statusCode.equals("404")) {
            this.panel.DHTprint("Content Not Found");
        }

        else if(statusCode.equals("200")) {
            this.panel.DHTprint("Content Found, IP given");
            scan = new Scanner(message);
            scan.next(); // status code
			client2IP = scan.next(); // The IP of the client who has the file.
				
			// Create the HTTP GET request.
			String HTTPRequest = createHTTPRequest("GET", fileName, "Close", InetAddress.getByName(clientToContactIP).getHostName(), "image/jpeg", "en-us");
			message = connectToPeerServer("OPEN " + fileName, clientToContactIP, peerServerPort); // Connect to the server of the client who has the file.
			scan = new Scanner(message);
			statusCode = scan.next();
			int newPort = scan.nextInt();
				
			// If the status code is 200 OK, then the request was sent successfully.
			if (statusCode.equals("200")) {
				System.out.println("FROM PEER SERVER -> New Connection Open On Port " + newPort);
				System.out.println("--HTTP Request Sent to Server-- START\n" + HTTPRequest + "--HTTP Request Sent to Server--END\n");
				connectToUniqueServer(fileName, HTTPRequest, clientToContactIP, newPort);
			}
		} // Server has sent the ip
    }
    
    public void data2Server(String message, String serverIP, int serverPort) throws Exception {
        byte[] sendData = newbyte[1024];
        sendData = message.getBytes();
        InetAddress interAdd = InetAddress.getByName(serverIP);
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length(), interAdd, serverPort);
        clientUDPSocket.send(sendPacket);
    }

    public String receiveDataFromServer() throws IOException {
        byte[] receiveData = new byte[1024];
        DatagramPacket recievePacket = new DatagramPacket(receiveData, receiveData.length);
        clientUDPSocket.receive(recievePacket);
        return new String(recievePacket.getData());
    }

    public String connectToPeerServer(String message, String ip, int port) throws UnknownHostException, IOException {
        Socket connectToPeerServer = new Socket(ip, port); // connect to peer server.                                                 
        OutputStream outToServer = connectToPeerServer.getOutputStream();
        DataOutputStream out = new DataOutputStream(outToServer);
        out.writeUTF(message);
        DataInputStream in = new DataInputStream(connectToPeerServer.getInputStream());
        message = in.readUTF();
        connectToPeerServer.close(); // Close the connection to the peer server.
        return message;

    }
    
    public void connectToUniqueServer(String Filename, String httpRequest, String ip, int port) throws UnknownHostException, IOException {
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

            for (int i = repStat.getBytes().length; i < data.length; i++) {
                backToBytes[i - responceStaus.getBytes().length] = data[i];
            }

            FileOutputStream fos = new FileOutputStream(outputfile);
            fos.write(backToBytes);
            fos.close(); // Close the fileoutput stream.
        } 
        else if (repStat.contains("HTTP/1.1 400 Bad Request")) {
            responceStaus = getHTTPResponse(scan, responceStaus);
        } 
        else if (repStat.contains("HTTP/1.1 404 Not Found")) {
            responceStaus = getHTTPResponse(scan, responceStaus);
        } 
        else if (repStat.contains("HTTP/1.1 505 HTTP Verson Not Supported")) {
            responceStaus = getHTTPResponse(scan, responceStaus);
        }

        this.panel.DHTprint("--HTTP Responce Got From Server-- START\n" + repStat + "--HTTP Responce Got From Server--END\n");
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

    public String createHTTPRequest(String request, String object, String connection, String host, String acceptType, String acceptLan) {
        String req = "";
        req += request + " /" + object + ".jpeg" + " HTTP/1.1\r\n";
        req += "Host: " + host + "\r\n";
        req += "Connection: " + connection + "\r\n";
        req += "Accept: " + acceptType + "\r\n";
        req += "Accept-Language: " + acceptLan + "\r\n\r\n";
        return req;
    }        
}
