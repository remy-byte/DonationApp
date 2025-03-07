package ro.mpp2024.UI;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ro.mpp2024.IServer;
import ro.mpp2024.MyAppException;
import ro.mpp2024.Voluntar;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;

public class LoginControllerClient {

    public TextField password;
    public TextField username;
    IServer service = null;
    Properties props = null;

    VoluntarController controller = null;
    private Parent parent;

    public void setProps(IServer service){
        this.service = service;
    }

    public void login() throws IOException, SQLException, MyAppException {
        String username = this.username.getText();
        String password = this.password.getText();
        Voluntar voluntar = this.service.login(username, password, controller);
        if ( voluntar != null) {
            System.out.println("Login successful");
            Stage stage = new Stage();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            controller.setVoluntar((Voluntar) voluntar);
            stage.show();
        } else
         System.out.println("Login failed");
    }

    public void setMainController(VoluntarController controller) {
        this.controller = controller;
    }

    public void setParent(Parent root) {
        this.parent = root;
    }
}
