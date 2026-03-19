package com.application.strms.app;

import com.application.strms.infrastructure.repository.UserRepository;
import com.application.strms.domain.service.AuthService;
import com.application.strms.utils.FXMLHandler;
import com.application.strms.view.LoginController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = FXMLHandler.load("Login");
        Scene scene = new Scene(loader.load(), 400, 300);

        UserRepository userRepository = new UserRepository();
        AuthService authService = new AuthService(userRepository);

        LoginController controller = loader.getController();
        controller.setAuthService(authService);

        stage.setTitle("Application");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}