import java.util.*;
import java.io.*;
import java.net.*;
import java.text.*;


public class HostServer{
     int originPort;

     final int httpStatusCode200 = 200; //Ok 
     final int httpStatusCode400 = 400; //Bad request
     final int httpStatusCode404 = 404; //Cannot be found
     final int httpStatusCode505 = 505; //not supported

     ServerSocket hostServerTCP;
     Thread hostThread;
    //method to initialize server
    public HostServer(int portNum)
    {
        //originPort = portNum;
        hostServerTCP = new ServerSocket(portNum);
        hostThread = new Thread();
    }
}