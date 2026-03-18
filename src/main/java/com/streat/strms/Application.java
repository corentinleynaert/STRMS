package com.streat.strms;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        TaskManager.load_data();

        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("homepage.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Streat");
        stage.setScene(scene);
        stage.show();
    }
}
