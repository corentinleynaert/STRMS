package com.strms.app;

import com.strms.application.ApplicationContext;
import com.strms.application.service.AuthService;
import com.strms.application.service.NotificationService;
import com.strms.application.service.TaskManager;
import com.strms.application.session.SessionManager;
import com.strms.domain.repository.UserRepository;
import com.strms.domain.repository.TaskRepository;
import com.strms.domain.service.PasswordHasher;
import com.strms.infrastructure.notification.NotificationManager;
import com.strms.infrastructure.persistence.FileHandler;
import com.strms.infrastructure.repository.FileUserRepository;
import com.strms.infrastructure.repository.FileTaskRepository;
import com.strms.infrastructure.security.BCryptPasswordHasher;
import com.strms.presentation.controller.LayoutController;
import com.strms.presentation.navigation.Navigator;
import com.strms.presentation.loader.ViewLoader;

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
        FileHandler fileHandler = new FileHandler();
        UserRepository userRepository = new FileUserRepository(fileHandler);
        TaskRepository taskRepository = new FileTaskRepository(fileHandler, userRepository);
        PasswordHasher passwordHasher = new BCryptPasswordHasher();
        AuthService authService = new AuthService(userRepository, passwordHasher);
        NotificationService notificationService = new NotificationManager();
        TaskManager taskManager = new TaskManager(taskRepository, notificationService);
        SessionManager sessionManager = new SessionManager();
        ApplicationContext applicationContext = new ApplicationContext(authService, sessionManager, userRepository,
                taskManager);

        FXMLLoader loader = ViewLoader.load("Layout");
        Parent root = loader.load();

        LayoutController layoutController = loader.getController();

        Navigator navigator = new Navigator(layoutController, applicationContext);

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
    }
}