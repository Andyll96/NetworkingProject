public class DHTServer {
    
    int serverPort;
    int serverID;
    int successorServerPort;
    String successorServerIP;

    final int okStatus = 200;
    final int badRequestStatus = 400;
    final int notFoundStatus = 404;
    final int notSupportedStatus = 505;

    public DHTServer(String serverPort, String serverID, String successorServerPort, String successorServerIP, Panel panel) {
        this.serverPort = Integer.parseInt(serverPort);
        this.serverID = Integer.parseInt(serverID);
        this.successorServerPort = Integer.parseInt(successorServerPort);
        this.successorServerIP = successorServerIP;
        panel.DHTPrint(this.serverPort + " " + this.serverID + " " + this.successorServerPort + " " + this.successorServerIP);
    }
}