

/* handles the client connections */
public class ClientServer {

    static String serverIP;
    static int serverPort;
    static int clientServerPort;
    Thread clientThread; // main thread


    public ClientServer(String serverIP, String serverPort, String clientServerPort){
        this.serverIP = serverIP;
        this.serverPort = Integer.parseInt(serverPort);
        this.clientServerPort = Integer.parseInt(clientServerPort);

        clientThread = new Thread(mainRunnable);
        clientThread.start();
    }

    
    


}

