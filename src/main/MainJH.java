package main;

import db.DBInit;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainJH extends Application {

    private static boolean bdNueva = false;

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/login.fxml"));
        Scene scene = new Scene(loader.load());

        stage.setTitle("Jacobs Hoster - Login");
        stage.setScene(scene);
        stage.setMinWidth(500.0);
        stage.setMinHeight(350.0);
        stage.show();

        // Avisar si se creó una BD nueva
        if (bdNueva) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.WARNING);
            alert.setTitle("Base de datos nueva");
            alert.setHeaderText("Se ha creado una base de datos nueva");
            alert.setContentText(
                "No se encontraron datos previos y se ha creado una base de datos en blanco.\n\n" +
                "Si tenías datos anteriores, comprueba que el archivo 'jacobs_hoster.db' " +
                "está en la carpeta 'data/' junto al programa.");
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        bdNueva = DBInit.init();
        launch(args);
    }
}