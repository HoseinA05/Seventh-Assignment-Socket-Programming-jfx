package sbu.cs.socket;

import java.time.LocalDateTime;
import java.util.UUID;

public interface Message {

    public String getText();

    public String getAuthor();

    public LocalDateTime getDate();

    public UUID getMessageID();

}
