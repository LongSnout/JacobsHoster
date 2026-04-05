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

import javafx.application.Platform;
import service.AlbergueService;
import config.SesionActual;
import controller.NuevoAlbergueController;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;

    @FXML
    private void initialize() {
        if (lblError != null) {
            lblError.setText("");
        }

        Platform.runLater(() -> {
            try {
                if (AlbergueService.requiereConfiguracionInicial()) {
                    abrirConfiguracionInicial();
                }
            } catch (Exception e) {
                lblError.setText("Error comprobando la configuración inicial");
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void onLogin() {

        lblError.setText("");

        String user = txtUsuario.getText();
        String pass = txtPassword.getText();

        try {
            Usuario u = AuthService.login(user, pass);
            SesionActual.iniciar(u);
            
            // Cambiar a main.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/main.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setTitle("Jacobs Hoster");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

        } catch (AuthException e) {
            lblError.setText(e.getMessage());
        } catch (Exception e) {
            lblError.setText("Error cargando la pantalla principal");
            e.printStackTrace();
        }
    }
    
    private void abrirConfiguracionInicial() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/nuevo_albergue.fxml"));
            Scene scene = new Scene(loader.load());

            NuevoAlbergueController controller = loader.getController();
            controller.activarModoInicial();

            Stage stage = new Stage();
            stage.setTitle("Configuración inicial");
            stage.setScene(scene);
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            lblError.setText("Error abriendo la configuración inicial");
            e.printStackTrace();
        }
    }
}
