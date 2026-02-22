package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
            lblError.setText("OK: " + u.getNombreUsuario()); // provisional para probar
            // TODO: aquí luego cambiamos a la pantalla principal (main.fxml)
        } catch (AuthException e) {
            lblError.setText(e.getMessage());
        }
    }
}
