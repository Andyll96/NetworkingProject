import java.util.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.nio.charset.*;


public class HostServer{
     final int httpStatusCode200 = 200; //Ok

    ServerSocket hostServerTCP;
    Thread hostThread;
    String TCPMessage;
    int originPort;
    public static ArrayList<UniqueTCP> peerClientList = new ArrayList<UniqueTCP>();

    //method to initialize server
    public HostServer(int portNum)
    {
        originPort = portNum;
        
        try {
            hostServerTCP = new ServerSocket(portNum);
            hostThread = new Thread(TCPThread);
            hostThread.start();
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println("Not a valid port number.");
        }
    }

    public int nextAvailablePort()
    {
        int currentPort = 0;
        
        boolean foundPort = false;
        
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
                String message;
                try {
                    clientSocket = hostServerTCP.accept();
                    inStream = new DataInputStream(clientSocket.getInputStream());
                    message = inStream.readUTF();
                    scan = new Scanner(message);
                    scan.next(); 

                    availablePort = nextAvailablePort();
                    peerClientList.add(new UniqueTCP(originPort));
                    message = httpStatusCode200 + " " + availablePort;
                    outStream = new DataOutputStream(clientSocket.getOutputStream());
                    outStream.writeUTF(message);
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

        public UniqueTCP(int portNum)
        {
            try
            {
                TCP = new ServerSocket(portNum);
                TCPThread = new Thread(TCPConnectRun);
                TCPThread.start();
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
                byte[] finalBytes = null;
                double fileSize;
                String lastModified;
                byte[] fileBytes, httpBytes;
                FileInputStream fileInstream;
                OutputStream outStream;
                DataOutputStream dataOutstream;
                timeString = getTime();

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

                                //HTTP Status Code 200

                                if (file.exists())
                                {
                                    fileSize = file.length();
                                    lastModified = getFileModifiedTime(file);

                                    response = createResponse(httpStatusCode200, timeString, lastModified, "bytes", Integer.toString((int) fileSize), connection, cType);
                                    httpBytes = response.getBytes(Charset.forName("UTF-8"));
                                    fileBytes = new byte[(int) file.length()];

                                    fileInstream = new FileInputStream(file);
                                    fileInstream.read(fileBytes);
                                    fileInstream.close();

                                    finalBytes = new byte[httpBytes.length + fileBytes.length];
                                    System.arraycopy(httpBytes, 0, finalBytes, 0, httpBytes.length);
                                    System.arraycopy(fileBytes, 0, finalBytes, httpBytes.length, fileBytes.length);
                                }

                                //HTTP Status Code 404
                                else
                                {
                                    response = createResponse(httpStatusCode404, timeString, null, null, null, connection, null);
                                    finalBytes = response.getBytes(Charset.forName("UTF-8"));
                                }

                                //HTTP Status Code 400
                            }catch (Exception e)
                            {
                                response = createResponse(httpStatusCode400, timeString, null, null, null, connection, null);
                                finalBytes = response.getBytes(Charset.forName("UTF-8"));
                            }
                        }

                        //HTTP Status Codec 505
                        else
                        {
                            response = createResponse(httpStatusCode505, timeString, null, null, null, connection, null);
                            finalBytes = response.getBytes(Charset.forName("UTF-8"));
                        }
                    }

                    outStream = tcpSocket.getOutputStream();
                    dataOutstream = new DataOutputStream(outStream);
                    dataOutstream.writeInt(finalBytes.length);
                    dataOutstream.write(finalBytes, 0, finalBytes.length);
                    tcpSocket.close();
                    TCP.close();

                    for (int i = 0; i < HostServer.peerClientList.size(); i++)
                    {
                        if (HostServer.peerClientList.get(i).equals(this))
                        {
                            HostServer.peerClientList.remove(i);
                            TCPThread.stop();
                            break;
                        }
                    }

                } catch (Exception e)
                {
                    System.out.println(e);
                }

            }
        };

        public String getTime()
        {
            Date date = new Date();
            Date time = new Date();
            Scanner scan = new Scanner(date.toString());
            String dayName, month, dateNumber, timeString;
            DateFormat timeFormat = new SimpleDateFormat("yyyy HH:mm:ss");

            dayName = scan.next();
            month = scan.next();
            dateNumber = scan.next();
            timeString = dayName + ", " + dateNumber + " " + month + " " + timeFormat.format(time) + " GMT";
            return timeString;
        }

        public String getFileModifiedTime(File file)
        {
            Date date, time;
            Scanner scan;
            String dayName, month, dateNumber, timeString;
            DateFormat timeFormat = new SimpleDateFormat("yyyy HH:mm:ss");

            date = new Date(file.lastModified());
            scan = new Scanner(date.toString());
            time = new Date(file.lastModified());
            dayName = scan.next();
            month = scan.next();
            dateNumber = scan.next();

            timeString = dayName + ", " + dateNumber + " " + month + " " + timeFormat.format(time) + " GMT";
            return timeString;
        }

        public String createResponse(int statusCode, String currentDate, String modifiedDate, String acceptRange, String length, String connection, String cType)
        {
            String temp = "";

            switch (statusCode)
            {
                case 200:
                    temp += "HTTP/1.1 200 OK \r\n";
                    temp += "Connection: " + connection + "\r\n";
                    temp += "Date: " + currentDate + "\r\n";
                    temp += "Last Modified: " + modifiedDate + "\r\n";
                    temp += "Accept Ranges: " + acceptRange + "\r\n";
                    temp += "Content Length: " + length + "\r\n";
                    temp += "Content Type: " + cType + "\r\n";
                    break;
                case 400:
                    temp += "HTTP/1.1 400 Bad Request \r\n";
                    temp += connectionDate(connection, currentDate);
                    break;
                case 404:
                    temp += "HTTP/1.1 404 Not Found \r\n";
                    temp += connectionDate(connection, currentDate);
                    break;
                case 505:
                    temp += "HTTP/1.1 505 HTTP Version Not Supported \r\n";
                    temp += connectionDate(connection, currentDate);
                    break;
            }

            return temp;
        }

        public String connectionDate(String connection, String currentDate)
        {
            return "Connection: " + connection + "\r\n" + "Date: " + currentDate + "\r\n\r\n";
        }
    }

    

}