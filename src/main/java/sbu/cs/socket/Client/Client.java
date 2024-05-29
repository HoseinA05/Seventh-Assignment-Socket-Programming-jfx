package sbu.cs.socket.Client;


import sbu.cs.socket.FileMessage;
import sbu.cs.socket.Message;
import sbu.cs.socket.TextMessage;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.BiConsumer;

public class Client {
    private static final String SERVER_IP = "127.0.0.1"; // localhost
    private static final int SERVER_PORT = 1234;
    private Socket clientsSocket;
    private PrintWriter outStream; // Output stream to send data to the server
    private BufferedReader inpStream; // Input stream to receive data from the server
    private final String username;
    private HandleNewMessage hnm;


    public Client (String username){
        this.username = username;
        initialize();
    }

    public void initialize(){
        try {
            // Connect to server
            clientsSocket = new Socket(SERVER_IP, SERVER_PORT);

            outStream = new PrintWriter(Client.this.clientsSocket.getOutputStream(), true);
            inpStream = new BufferedReader(new InputStreamReader(Client.this.clientsSocket.getInputStream()));

            System.out.println("[CLIENT: " + this.username + "] connected to server.");

            // Make an -init request to Initialize The Username
            outStream.println("-init");
            outStream.println(this.username);

            // Get the Response
            if (inpStream.readLine().equals("T")) System.out.println("[CLIENT: " + this.username + "] Initialized Successfully.");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public ArrayList<TextMessage> getAllTextMessages() {
        // Requesting for Messages
        outStream.println("-getTextMessages");

        // Getting Sent Data By The Server
        try {
            int size = Integer.parseInt(inpStream.readLine());

            ArrayList<TextMessage> TextMessages = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                String text = inpStream.readLine();
                String author = inpStream.readLine();
                LocalDateTime t = LocalDateTime.parse(inpStream.readLine());

                TextMessage m = new TextMessage(text, author, t);
                TextMessages.add(m);
            }
            if (inpStream.readLine().equals("--finished")) System.out.println("[CLIENT: " + this.username + "] Read All Text Messages Successfully.");

            return TextMessages;
        } catch (IOException e) {
            System.out.println("Problem While Reading All Text Messages From The Server.");
        }

        return null;
    }

    public ArrayList<FileMessage> getAllFileMessages() {
        // Requesting for Messages
        outStream.println("-getFileMessages");

        // Getting Sent Data By The Server
        try {
            int size = Integer.parseInt(inpStream.readLine());

            ArrayList<FileMessage> fileMessages = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                String text = inpStream.readLine();
                String author = inpStream.readLine();
                LocalDateTime t = LocalDateTime.parse(inpStream.readLine());
                UUID messageID = UUID.fromString(inpStream.readLine());

                FileMessage m = new FileMessage(text, author, t, messageID);
                fileMessages.add(m);
            }
            if (inpStream.readLine().equals("--finished")) System.out.println("[CLIENT: " + this.username + "] Read All File Messages Successfully.");

            return fileMessages;
        } catch (IOException e) {
            System.out.println("Problem While Reading All File Messages From The Server.");
        }

        return null;
    }

    public Boolean sendMessage(String text){
        // Requesting for Sending a Message
        outStream.println("-sendMessage");
        outStream.println(text);

        // Todo: Handle The Server Response Somehow! to not conflict with checking the new messages

        return true;
    }

    public void receiveFile(String messageID) {
        new Thread(() -> {
            try {
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                InputStream in = socket.getInputStream();
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

                output.println("-receive");
                output.println(messageID);

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String fileName = reader.readLine();

                FileOutputStream out = new FileOutputStream("Received_" + fileName + ".txt");

                byte[] bytes = new byte[16 * 1024];
                int count;

                while ( (count = in.read(bytes)) != -1 ){
                    System.out.println("[CLIENT] Inside While...");
                    out.write(bytes, 0, count);
                }
                out.flush();

                // Close The File Input Stream.
                out.close();
                System.out.println("File Received");

                // Close The Socket.
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void handleNewMessages(BiConsumer<Message, Boolean> dataConsumer){
        hnm = new HandleNewMessage(inpStream, dataConsumer);
        Thread newMessageThread = new Thread(hnm);
        newMessageThread.start();
    }

    public void shutdown(){
        try {
            hnm.close();
            clientsSocket.close();
        } catch (IOException e) {
            System.out.println("[CLIENT] Error During Closing Socket...");
        }
    }

    public String getUsername() {
        return this.username;
    }
}