module sbu.cs.socket {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens sbu.cs.socket to javafx.fxml;
    exports sbu.cs.socket;
}