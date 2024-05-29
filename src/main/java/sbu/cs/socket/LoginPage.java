package sbu.cs.socket;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginPage extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginPage.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 600);
        scene.getStylesheets().add(getClass().getResource("styles/login-page.css").toExternalForm());

        // Set the controller class and call its shutdown methode upon exiting.
        LoginPageController controller = fxmlLoader.getController();
        stage.setOnHidden(e -> controller.shutdown());

        stage.setTitle("Login Page!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
