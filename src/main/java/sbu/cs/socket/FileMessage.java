package sbu.cs.socket;

import java.time.LocalDateTime;
import java.util.UUID;

public class FileMessage implements Message{
    private final UUID fileId;
    private final String url;
    private final String text;
    private final String author;
    private final LocalDateTime date;

    public FileMessage(String url, String text, String author){
        this.url = url;
        this.text = text;
        this.author = author;
        this.date = LocalDateTime.now();
        fileId = UUID.randomUUID();
    }

    public FileMessage(String text, String author, LocalDateTime t, UUID messageID) {
        this.text = text;
        this.author = author;
        this.date = t;
        this.url = "";
        this.fileId = messageID;
    }


    public String getUrl() {
        return this.url;
    }

    @Override
    public String getAuthor() {
        return this.author;
    }

    @Override
    public LocalDateTime getDate() {
        return this.date;
    }

    @Override
    public UUID getMessageID() {
        return this.fileId;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof FileMessage)
            return this.fileId.equals(((FileMessage) obj).fileId);
        return false;
    }
}
