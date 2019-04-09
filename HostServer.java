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

a

    //method to initialize server
    public HostServer(int portNum, Panel panel)
    {
        //originPort = portNum;
        
        try {
            hostServerTCP = new ServerSocket(portNum);
            hostThread = new Thread();
            hostThread.Start(TCPThread);
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
                    scan = new Scanner(in.readUTF());
                    scan.next(); 

                    availablePort = nextAvailablePort();   
                    System.out.println(statusCode200 + " " + availablePort);
                    outStream = new DataOutputStream(clientSocket.getOutputStream());
                    clientSocket.close();

                } catch (Exception e) {
                    //TODO: handle exception
                    System.out.println("Connection error!");
                }
            }
        }
    }

    

}