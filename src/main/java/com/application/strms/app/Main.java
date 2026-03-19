package com.application.strms.app;

import com.application.strms.application.service.AuthService;
import com.application.strms.domain.repository.UserRepository;
import com.application.strms.domain.service.PasswordHasher;
import com.application.strms.infrastructure.persistence.FileHandler;
import com.application.strms.infrastructure.repository.FileUserRepository;
import com.application.strms.infrastructure.security.BCryptPasswordHasher;
import com.application.strms.presentation.LoginController;
import com.application.strms.presentation.loader.ViewLoader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = ViewLoader.load("Login");
        Scene scene = new Scene(loader.load(), 400, 300);

        FileHandler fileHandler = new FileHandler();
        UserRepository userRepository = new FileUserRepository(fileHandler);
        PasswordHasher passwordHasher = new BCryptPasswordHasher();
        AuthService authService = new AuthService(userRepository, passwordHasher);

        LoginController controller = loader.getController();
        controller.setAuthService(authService);

        stage.setTitle("Application");
        stage.setScene(scene);
        stage.show();
    }
}