import java.util.Scanner;

public class PeerClient {
    public static void main(String[] args) {
        int clientServerPort = Integer.parseInt(args[0]);
        String serverIP = args[1];
        int serverPort = Integer.parseInt(args[2]);

        Thread clientThread = new Thread(new Runnable(){

            @Override
            public void run() {
                System.out.println("Client Running");

                ClientServer2 peerClient = new ClientServer2(serverIP, serverPort, clientServerPort);
                HostServer peerServer = new HostServer(clientServerPort);

                Scanner scanner = new Scanner(System.in);
                String input;

                while (true) {
                    System.out.println("Enter: UPLOAD, QUERY, or EXIT");
                    input = scanner.next();

                    if (input.toUpperCase().equals("UPLOAD")){
                        System.out.println("Enter File Name: ");
                        input = scanner.next(); //filename

                        int dhtServerID = 0;
                        for (int i = 0; i < input.length(); i++) {
                            dhtServerID += (int) input.charAt(i);
                        }
                        dhtServerID = dhtServerID % 4;

                        try {
                            peerClient.uploadData(dhtServerID, input);
                        } catch (Exception e) {
                            System.out.println("Couldn't connect to server : " + e);
                        }
                    } else if (input.toUpperCase().equals("QUERY")) {
                        System.out.println("Enter File Name: ");
                        input = scanner.next(); //filename

                        int dhtServerID = 0;
                        for (int i = 0; i < input.length(); i++) {
                            dhtServerID += (int) input.charAt(i);
                        }
                        dhtServerID = dhtServerID % 4;

                        try {
                            peerClient.query(dhtServerID, input);
                        } catch (Exception e) {
                            System.out.println("Couldn't connect to server : " + e);
                        }
                    } else if (input.toUpperCase().equals("EXIT")) {
                        try {
                            peerClient.exit();
                        } catch (Exception e) {
                            System.out.println("Couldn't connect to server: " + e);
                        }
                    }else{
                        System.out.println("Invalid");
                    }
                }
            }
        });
        System.out.println("Client Initializing");
        clientThread.start();
    }
}