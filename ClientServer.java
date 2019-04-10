    
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

    static Runnable mainRunnable = new Runnable() {
            public void run() {

                HostServer hostPeer = new HostServer(hostServerPort);

                while (true) {
                    if (uploadButton.isPressed()) {
                        this.panel.DHTprint("Enter File Name: ");
					userInput = scannerIn.next();
					
					// Calculate the ID of the server to which the file will be uploaded.
					int calculatedServerID = 0;
					for (int i = 0; i < userInput.length(); i++) {
						calculatedServerID += (int) userInput.charAt(i);
					}
					calculatedServerID = calculatedServerID % 4;
					
					try {
						// Upload the file to the server.
						peerClient.uploadData(calculatedServerID, userInput);
					} 
					catch (Exception e) {
						this.panel.DHTprint("Could not connect to server.");
                    }
                }
                
                    else if (downloadButton.isPressed()) {
                        this.panel.DHTprint("Enter File Name: ");
                        userInput = scannerIn.next();
    
                        // Calcuate the ID of the server where the file is stored.
                        int calculatedServerID = 0;
                        for (int i = 0; i < userInput.length(); i++) {
                            calculatedServerID += (int) userInput.charAt(i);
                        }
                        calculatedServerID = calculatedServerID % 4;
                        
                        try {
                            // Query the appropriate server.
                            peerClient.query(calculatedServerID, userInput);
                        } 
                        catch (Exception e) {
                            this.panel.DHTprint("Could not connect to server.");
                        }
                    } 
                    
                    // The the user enters "E" (EXIT), then exit the client.
                    else if (exitButton.isPressed()) {
                        try {
                            // Close the current client.
                            peerClient.exit();
                        } 
                        catch (Exception e) {
                            this.panel.DHTprint("Could not connect to server.");
                        }
                    } 
            }
        }
    }
}

   

    


