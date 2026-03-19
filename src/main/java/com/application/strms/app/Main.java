package com.application.strms.app;

import com.application.strms.application.ApplicationContext;
import com.application.strms.application.service.AuthService;
import com.application.strms.application.session.SessionManager;
import com.application.strms.domain.repository.UserRepository;
import com.application.strms.domain.service.PasswordHasher;
import com.application.strms.infrastructure.persistence.FileHandler;
import com.application.strms.infrastructure.repository.FileUserRepository;
import com.application.strms.infrastructure.security.BCryptPasswordHasher;
import com.application.strms.presentation.controller.LayoutController;
import com.application.strms.presentation.navigation.Navigator;
import com.application.strms.presentation.loader.ViewLoader;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        try {
            FileHandler fileHandler = new FileHandler();
            UserRepository userRepository = new FileUserRepository(fileHandler);
            PasswordHasher passwordHasher = new BCryptPasswordHasher();
            AuthService authService = new AuthService(userRepository, passwordHasher);
            SessionManager sessionManager = new SessionManager();
            ApplicationContext applicationContext = new ApplicationContext(authService, sessionManager);

            FXMLLoader loader = ViewLoader.load("Layout");
            Parent root = loader.load();

            LayoutController layoutController = loader.getController();

            Navigator navigator = new Navigator(stage, layoutController, applicationContext);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());

            navigator.goTo("Login");

            stage.setTitle("Application");
            stage.setScene(scene);
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            stage.setMaximized(true);
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.show();

        } catch (IllegalArgumentException e) {
            System.exit(1);
        }
    }
}