module security.proj {
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires opencv;
    requires java.desktop;
    requires javafx.swing;

    opens com.sharon.home_security;
    opens com.sharon.home_security.Controllers;
}