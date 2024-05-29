package sbu.cs.socket.Server;

import sbu.cs.socket.FileMessage;
import sbu.cs.socket.TextMessage;

import java.util.ArrayList;

public class ServerData {
    private static final ArrayList<TextMessage> TEXT_MESSAGES = new ArrayList<>();
    private static final ArrayList<FileMessage> fileMessages = new ArrayList<>();

    public static void addTextMessage(TextMessage textMessage) {
        ServerData.TEXT_MESSAGES.add(textMessage);
    }
    public static void addFileMessage(FileMessage fileMessage) {
        ServerData.fileMessages.add(fileMessage);
    }

    public static ArrayList<TextMessage> getTextMessages() {
        return new ArrayList<>(TEXT_MESSAGES);
    }
    public static ArrayList<FileMessage> getFileMessages() {
        return new ArrayList<>(fileMessages);
    }
}
