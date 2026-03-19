package com.application.strms.app;

import com.application.strms.application.ApplicationContext;
import com.application.strms.application.service.AuthService;
import com.application.strms.application.session.SessionManager;
import com.application.strms.domain.repository.UserRepository;
import com.application.strms.domain.service.PasswordHasher;
import com.application.strms.infrastructure.persistence.FileHandler;
import com.application.strms.infrastructure.repository.FileUserRepository;
import com.application.strms.infrastructure.security.BCryptPasswordHasher;
import com.application.strms.presentation.controller.LoginController;
import com.application.strms.presentation.navigation.Navigator;
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
        SessionManager sessionManager = new SessionManager();
        ApplicationContext applicationContext = new ApplicationContext(authService, sessionManager);
        Navigator navigator = new Navigator(stage, applicationContext);

        LoginController controller = loader.getController();
        controller.setApplicationContext(applicationContext);
        controller.setNavigator(navigator);

        stage.setTitle("Application");
        stage.setScene(scene);
        stage.show();
    }
}