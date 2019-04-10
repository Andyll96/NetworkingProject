import java.util.*;
import java.io.*;
import java.net.*;
import java.text.*;


public class HostServer{
     int originPort = Panel.ServerPort;

     final int httpStatusCode200 = 200; //Ok 
     final int httpStatusCode301 = 301; //Moved Permanently
     final int httpStatusCode400 = 400; //Bad request
     final int httpStatusCode404 = 404; //Cannot be found
     final int httpStatusCode505 = 505; //not supported

     ServerSocket hostServerTCP;
     Thread hostThread;
     String TCPMessage;
     public static ArrayList<UniqueTCP> peerClientList = new ArrayList<UniqueTCP>();

    //method to initialize server
    public HostServer(int portNum, Panel panel)
    {
        //originPort = portNum;
        
        try {
            hostServerTCP = new ServerSocket(portNum);
            hostThread = new Thread();
            hostThread.start();
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println("Not a valid port number.");
        }
    }

    public int nextAvailablePort()
    {
        int currentPort = 0;
        
        boolean foundPort = False;
        
        while (foundPort == false)
        {
            try {
                ServerSocket testPort = new ServerSocket(currentPort);
                testPort.close();
                foundPort = true;
                break;
            } catch (Exception e) {
                //TODO: handle exception
                currentPort++;
            }
        }
        return currentPort;
    }

    Runnable TCPThread = new Runnable()
    {
        public void run()
        {
            while (true)
            {
                Socket clientSocket;
                DataInputStream inStream;
                DataOutputStream outStream;
                Scanner scan;
                int availablePort;
                try {
                    clientSocket = hostServerTCP.accept();
                    inStream = new DataInputStream(clientSocket.getInputStream());
                    scan = new Scanner(inStream.readUTF());
                    scan.next(); 

                    availablePort = nextAvailablePort();   
                    System.out.println(httpStatusCode200 + " " + availablePort);
                    outStream = new DataOutputStream(clientSocket.getOutputStream());
                    outStream.writeUTF(inStream.readUTF());
                    clientSocket.close();

                } catch (Exception e) {
                    //TODO: handle exception
                    System.out.println("Connection error!");
                }
            }
        }
    };

    public class UniqueTCP
    {
        final int httpStatusCode200 = 200;  //OK
        final int httpStatusCode301 = 301;  //Moved permanently
        final int httpStatusCode400 = 400;  //Bad request
        final int httpStatusCode404 = 404;  //HTTP not found
        final int httpStatusCode505 = 505;  // HTTP version not supported

        ServerSocket TCP;
        Thread TCPThread;

        public UniqueTCP(int portNum, Panel panel)
        {
            try
            {
                TCP = new ServerSocket(portNum);
                TCPThread = new Thread()
            } catch (Exception e){
                //TODO: handle exception
                System.out.println("Not a valid port number");
            }
        }

        Runnable TCPConnectRun = new Runnable()
        {
            public void run()
            {
                String message, fileName, request, httpV, response, connection, cType, timeString, temp, temp2;
                Socket tcpSocket;
                DataInputStream inStream;
                Scanner scan;
                File file, badFile, file2;
                try
                {
                    tcpSocket = TCP.accept();
                    inStream = new DataInputStream(tcpSocket.getInputStream());
                    message = inStream.readUTF();
                    scan = new Scanner(inStream.readUTF());
                    request = scan.next();

                    if (request.equals("GET"))
                    {
                        fileName = scan.next();
                        httpV = scan.next();
                        connection = "CLOSED";
                        cType = "image";

                        if (httpV.equals("HTTP/1.1"))
                        {
                            fileName = fileName.substring(1);
                            file = new File(fileName);
                            try
                            {
                                temp = fileName.substring(0, fileName.indexOf(".jpeg"));
                                temp2 = fileName.substring(0, fileName.indexOf(".jpeg"));
                                temp += "---Trial---.jpeg"; //??
                                badFile = new File(temp);
                                badFile.createNewFile();
                                badFile.delete();

                                file2 = new File(temp2 + ".jpg");

                                if (file2.exists())
                                {
                                    file = new File(temp2 + ".jpg");
                                }
                            }
                        }
                    }
                } catch (Exception e)
                {
                    System.out.println(e);

                }

            }
        }
    }

    

}