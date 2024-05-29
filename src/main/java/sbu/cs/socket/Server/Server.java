package sbu.cs.socket.Server;

import sbu.cs.socket.FileMessage;
import sbu.cs.socket.TextMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Server Class
public class Server {
    public static final int PORT = 1234;
    private static final ExecutorService pool = Executors.newFixedThreadPool(4);
    public static void main(String[] args) {
        initServerData();

        ServerSocket server = null;
        try {
            server = new ServerSocket(PORT);
            System.out.println("[SERVER] Server started. Waiting for client connections...");

            while (true){
                // Accept a client
                Socket client = server.accept();
                System.out.println("[SERVER] Client Connected: " + client.getInetAddress());

                // Give Service to the Client
                ClientHandler ch = new ClientHandler(client);
                pool.execute(ch);
            }

        } catch (IOException e) {
            System.out.println("Error ? : " + e.getMessage());
        } finally {
            try {
                if(server != null) server.close();
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
            pool.shutdown();
        }
    }

    public static void initServerData(){
        // Some Messages:
        ServerData.addTextMessage(new TextMessage("Hi Hossein!", "Reza"));

        // Some File Messages:
        ServerData.addFileMessage(new FileMessage("src/main/java/sbu/cs/socket/Server/data/a-man-without-love-ngelbert-Hmperdinck.txt", "a-man-without-love-ngelbert-Hmperdinck.txt", "Server"));
        ServerData.addFileMessage(new FileMessage("src/main/java/sbu/cs/socket/Server/data/all-of-me-john-legend.txt", "all-of-me-john-legend.txt", "Server"));
        ServerData.addFileMessage(new FileMessage("src/main/java/sbu/cs/socket/Server/data/birds-imagine-dragons.txt", "birds-imagine-dragons.txt", "Server"));
        ServerData.addFileMessage(new FileMessage("src/main/java/sbu/cs/socket/Server/data/blinding-lights-the-weekend.txt", "blinding-lights-the-weekend.txt", "Server"));
        ServerData.addFileMessage(new FileMessage("src/main/java/sbu/cs/socket/Server/data/dont-matter-to-me-drake.txt", "dont-matter-to-me-drake.txt", "Server"));
        ServerData.addFileMessage(new FileMessage("src/main/java/sbu/cs/socket/Server/data/feeling-in-my-body-elvis.txt", "feeling-in-my-body-elvis.txt", "Server"));
        ServerData.addFileMessage(new FileMessage("src/main/java/sbu/cs/socket/Server/data/out-of-time-the-weekend.txt", "out-of-time-the-weekend.txt", "Server"));
        ServerData.addFileMessage(new FileMessage("src/main/java/sbu/cs/socket/Server/data/something-in-the-way-nirvana.txt", "something-in-the-way-nirvana.txt", "Server"));
        ServerData.addFileMessage(new FileMessage("src/main/java/sbu/cs/socket/Server/data/why-you-wanna-trip-on-me-michael-jackson.txt", "why-you-wanna-trip-on-me-michael-jackson.txt", "Server"));
        ServerData.addFileMessage(new FileMessage("src/main/java/sbu/cs/socket/Server/data/you-put-a-spell-on-me-austin-giorgio.txt", "you-put-a-spell-on-me-austin-giorgio.txt", "Server"));
    }
}