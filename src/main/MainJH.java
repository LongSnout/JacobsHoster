package main;

import db.DBInit;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainJH extends Application {

    @Override
    public void start(Stage stage) throws Exception {
    	

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/login.fxml"));
        Scene scene = new Scene(loader.load());

        stage.setTitle("Jacobs Hoster - Login");
        stage.setScene(scene);
        stage.show();
        
    }

    public static void main(String[] args) {
    	
    	
        DBInit.init();     // inicialización de BD
        
        
        launch(args);      // lanza JavaFX y llama a start()
        
    }
}

