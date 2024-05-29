package sbu.cs.socket.Server;

import sbu.cs.socket.FileMessage;
import sbu.cs.socket.TextMessage;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;

public class ClientHandler implements Runnable{
    private static final HashMap<String, Socket> onlineClients = new HashMap<>();
    private final Socket client;
    private String username;
    private final PrintWriter outputStream; // Input stream to receive data from the client
    private final BufferedReader inputStream; // Output stream to send data to the client
    private final OutputStream out;

    public ClientHandler(Socket client) throws IOException {
        this.client = client;
        this.out = client.getOutputStream();
        this.username = "";
        this.outputStream = new PrintWriter(client.getOutputStream(), true);
        this.inputStream = new BufferedReader(new InputStreamReader(client.getInputStream()));
    }
    @Override
    public void run() {
        try {
            while (true) {
                boolean exitFlag = false;
                String request = this.inputStream.readLine();
                System.out.println("[SERVER] Request Received: " + request);
                switch (request){
                    case "-init":
                        String name = this.inputStream.readLine();
                        handleInit(name);
                        break;
                    case "-sendMessage":
                        String messageText = this.inputStream.readLine();
                        handleSendMassage(messageText);
                        break;
                    case "-getTextMessages":
                        handleGetTextMessages();
                        break;
                    case "-getFileMessages":
                        handleGetFileMessages();
                        break;
                    case "-receive":
                        String messageID = this.inputStream.readLine();
                        handleReceiveFile(messageID);
                        break;
                    case "-exit":
                        exitFlag = true;
                        break;
                    default:
                        System.out.println("[SERVER] Unknown Request");
                        break;
                }

                if(exitFlag)
                    break;
            }
        } catch (IOException e) {
            System.out.println("Error > : " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }

        }
    }

    private void handleInit(String name) throws IOException {
        this.username = name;
        ClientHandler.onlineClients.put(this.username, this.client);

        System.out.println("[SERVER] client Initialized Successfully. (Client: " + this.username + ")");

        // Send The Response
        outputStream.println("T");
    }

    private void handleSendMassage(String text) throws IOException {

        // First Save The New Message In The ServerData for the future Users.
        TextMessage newTextMessage = new TextMessage(text, this.username);
        ServerData.addTextMessage(newTextMessage);

        System.out.println("[SERVER] Added new message to Array successfully. (Client: " + this.username + ")");

        // Then Send The New Message For all the Online Users
        System.out.println("[SERVER] All Users: " + onlineClients.size());
        onlineClients.forEach((s, socket) -> {
            try{
                if(!s.equals(this.username)){
                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                    printWriter.println("-newMessage");
                    printWriter.println(newTextMessage.getText());
                    printWriter.println(newTextMessage.getAuthor());
                    printWriter.println(newTextMessage.getDate());
                    // Bug: Causes The Socket to close
                    // ATTENTION: Closing Input or Output Streams of a Socket causes it to close the entire socket. there is no need for closing them just close the socket when finished working.
                    // printWriter.close();
                    System.out.println("[SERVER] Send The new message to an online user successfully. (From Client: " + this.username + ")");
                }else{
                    System.out.println("[SERVER] The Same User is in the list");
                }
            }catch (IOException e){
                System.out.println("Error ^^: " + e.getMessage());
            }
        });

    }

    private void handleGetTextMessages(){
        // First Send the count of all messages
        outputStream.println(ServerData.getTextMessages().size());

        // Then Content of Each Message
        for (TextMessage m: ServerData.getTextMessages()){
            outputStream.println(m.getText());
            outputStream.println(m.getAuthor());
            outputStream.println(m.getDate());
        }

        // And finally a finished mark.
        outputStream.println("--finished");
    }

    private void handleGetFileMessages(){
        // First Send the count of all messages
        outputStream.println(ServerData.getFileMessages().size());

        // Then Content of Each Message
        for(FileMessage fm: ServerData.getFileMessages()){
            outputStream.println(fm.getText());
            outputStream.println(fm.getAuthor());
            outputStream.println(fm.getDate());
            outputStream.println(fm.getMessageID().toString());
        }

        // And finally a finished mark.
        outputStream.println("--finished");
    }

    private void handleReceiveFile(String messageID) {
        String url = "";
        String name = "";
        for(FileMessage fm: ServerData.getFileMessages()){
            if(fm.getMessageID().equals(UUID.fromString(messageID))){
                System.out.println("[SERVER] Found The File: " + fm.getText());
                url = fm.getUrl();
                name = fm.getText();
            }
        }

        // Send The Name of File
        outputStream.println(name);

        try{
            File file = new File(url);
            InputStream in = new FileInputStream(file);

            byte[] bytes = new byte[16 * 1024];
            int count;

            while((count = in.read(bytes)) != -1){
                out.write(bytes, 0, count);
            }
            out.flush();

            client.shutdownOutput();
            System.out.println("[SERVER] Sent The File.");
            in.close();
        } catch (IOException e) {
            System.out.println("Error [Receive File]: " + e.getMessage());
        }

    }

}
