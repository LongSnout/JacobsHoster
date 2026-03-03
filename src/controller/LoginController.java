package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Usuario;
import service.AuthService;
import exception.AuthException;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;

    @FXML
    private void initialize() {
        if (lblError != null) {
            lblError.setText("");
        }
    }

    @FXML
    private void onLogin() {

        lblError.setText("");

        String user = txtUsuario.getText();
        String pass = txtPassword.getText();

        try {
            Usuario u = AuthService.login(user, pass);

            // Cambiar a main.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/main.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setTitle("Jacobs Hoster");
            stage.setScene(scene);
            stage.show();

        } catch (AuthException e) {
            lblError.setText(e.getMessage());
        } catch (Exception e) {
            lblError.setText("Error cargando la pantalla principal");
            e.printStackTrace();
        }
    }
}
