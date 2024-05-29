package sbu.cs.socket;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import sbu.cs.socket.Client.Client;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class LoginPageController {
    private Client client;
    @FXML
    public ScrollPane chatAreaScrollPane;
    @FXML
    public VBox chatArea;
    @FXML
    public TextField messageBox;
    @FXML
    public VBox groupArea;
    @FXML
    public HBox nameBox;
    @FXML
    public TextField username;

    @FXML
    public void initialize() {
        chatArea.heightProperty().addListener(observable -> chatAreaScrollPane.setVvalue(1D));

        groupArea.setVisible(false);
        groupArea.setManaged(false);
    }

    @FXML
    public void nameTextField(ActionEvent actionEvent) {
        // Todo: Make a proper action when the server was not available at the moment.
        client = new Client(username.getText());
        nameBox.setVisible(false);
        nameBox.setManaged(false);

        // Unhidden The Chat Area Section
        groupArea.setVisible(true);
        groupArea.setManaged(true);

        createHistoryFileMessages(client.getAllFileMessages());
        createHistoryTextMessages(client.getAllTextMessages());
    }

    @FXML
    public void sendMessage(ActionEvent actionEvent) {
        if(client.sendMessage(messageBox.getText()))
            createMessage(new TextMessage(messageBox.getText(), client.getUsername()), false);
    }

    public void createHistoryFileMessages(ArrayList<FileMessage> fileMessages) {
        for (FileMessage fm : fileMessages) {
            createMessage(fm, true);
        }

    }

    public void createHistoryTextMessages(ArrayList<TextMessage> TextMessages){
        for (TextMessage m: TextMessages) {
            createMessage(m, false);
        }

        client.handleNewMessages(this::createMessage);
    }

    public void createMessage(Message m, boolean isFile) {
        HBox container = new HBox();

        Pane p = new Pane();
        HBox.setHgrow(p, Priority.ALWAYS);

        VBox vBox = new VBox();
        vBox.getStyleClass().add("msg-container");
        if (m.getAuthor().equals(client.getUsername())) vBox.getStyleClass().add("msg-bg-sender");
        else vBox.getStyleClass().add("msg-bg-receiver");

        // Label For Message Author
        Label l1 = new Label(m.getAuthor());
        l1.getStyleClass().add("msg-sender");

        // Label For Message Text
        Label l2 = new Label(m.getText());
        l2.getStyleClass().add("msg-text");

        // if it's A file message:
        HBox fileMessageContainer = null;
        if (isFile) {
            fileMessageContainer = new HBox();
            // Download Button:
            Button downloadBtn = new Button("Download");
            downloadBtn.getStyleClass().add("dn-file-btn");
            downloadBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    client.receiveFile(m.getMessageID().toString());
                }
            });

            HBox.setMargin(fileMessageContainer, new Insets(2, 0, 0, 2));
            fileMessageContainer.getChildren().addAll(downloadBtn, l2);
        }

        // Label For Message Date
        Label l3 = new Label(m.getDate().format(DateTimeFormatter.ofPattern("H:mm:ss")));
        l3.getStyleClass().add("msg-date");


        if (isFile)
            vBox.getChildren().addAll(l1, fileMessageContainer, l3);
        else
            vBox.getChildren().addAll(l1, l2, l3);

        HBox.setMargin(vBox, new Insets(0, 10, 10, 10));

        if (m.getAuthor().equals(client.getUsername()))
            container.getChildren().addAll(p, vBox);
        else
            container.getChildren().addAll(vBox, p);

        chatArea.getChildren().add(container);

        // Scroll The Page Down And Clear The Input.
        // chatAreaScrollPane.setVvalue(1D);
        messageBox.clear();
    }

    public void shutdown(){
        System.out.println("[CLIENT] Program Closed. (Client : " + client.getUsername() + ")");
        client.shutdown();
    }
}
