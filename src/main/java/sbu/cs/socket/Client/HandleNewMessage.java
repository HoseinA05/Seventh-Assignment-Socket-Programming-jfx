package sbu.cs.socket.Client;

import javafx.application.Platform;
import sbu.cs.socket.Message;
import sbu.cs.socket.TextMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.function.BiConsumer;

public class HandleNewMessage implements Runnable{
    private final BufferedReader inputStream;
    private final BiConsumer<Message, Boolean> dataConsumer;
    private boolean isClosed = false;

    public HandleNewMessage(BufferedReader in, BiConsumer<Message, Boolean> dataConsumer){
        inputStream = in;
        this.dataConsumer = dataConsumer;
    }

    @Override
    public void run() {
        while (!isClosed){
            try {
                String inp = inputStream.readLine();
                System.out.println("[LOG] [New Message] Request " + inp);

                if(inp.equals("-newMessage")) {
                    String text = inputStream.readLine();
                    String author = inputStream.readLine();
                    LocalDateTime t = LocalDateTime.parse(inputStream.readLine());

                    TextMessage newTextMessage = new TextMessage(text, author, t);
                    // Platform.runLater(() -> data(newTextMessage, false));
                    Platform.runLater(() -> dataConsumer.accept(newTextMessage, false));
                    // System.out.println(text + " Received");
                }
            } catch (IOException e) {
                System.out.println("Error During Getting New Message");
            }
        }
        System.out.println("[CLIENT] Handle New Message Thread Stopped!");
    }

    public void close(){
        this.isClosed = true;
    }
}
