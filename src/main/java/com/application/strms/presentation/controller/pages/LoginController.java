package com.application.strms.presentation.controller.pages;

import java.io.IOException;

import com.application.strms.application.ApplicationContext;
import com.application.strms.application.result.LoginResult;
import com.application.strms.application.service.AuthService;
import com.application.strms.application.session.SessionManager;
import com.application.strms.presentation.controller.BaseController;
import com.application.strms.presentation.navigation.Navigator;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class LoginController extends BaseController {
    private ApplicationContext context;
    private Navigator navigator;

    @FXML private TextField email_field;
    @FXML private PasswordField password_field;
    @FXML private Label errors;
    @FXML private Button login_button;

    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }

    @FXML
    public void initialize() {
        email_field.textProperty().addListener((_, _, _) -> updateLoginButtonState());
        password_field.textProperty().addListener((_, _, _) -> updateLoginButtonState());

        login_button.setOnMouseEntered(_ -> login_button.setOpacity(0.9));
        login_button.setOnMouseExited(_ -> login_button.setOpacity(1.0));

        updateLoginButtonState();
    }

    @FXML
    protected void login() {
        AuthService authService = context.getAuthService();
        SessionManager sessionManager = context.getSessionManager();

        String email = email_field.getText().trim();
        String password = password_field.getText();

        try {
            LoginResult result = authService.login(email, password);

            if (result.isSuccess()) {
                sessionManager.login(result.user());
                navigator.goTo("Home");
            } else {
                showError("Email or password is incorrect");
                shakeNode(email_field);
                shakeNode(password_field);
            }
        } catch (IOException e) {
            showError("Error accessing user data: " + e.getMessage());
            shakeNode(email_field);
            shakeNode(password_field);
        }
    }

    private void showError(String message) {
        errors.setText(message);
        errors.setManaged(true);
        errors.setVisible(true);
    }

    private void updateLoginButtonState() {
        boolean isEmailEmpty = email_field.getText() == null || email_field.getText().trim().isEmpty();
        boolean isPasswordEmpty = password_field.getText() == null || password_field.getText().isEmpty();

        login_button.setDisable(isEmailEmpty || isPasswordEmpty);
    }

    private void shakeNode(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
        tt.setFromX(0);
        tt.setByX(10);
        tt.setCycleCount(4);
        tt.setAutoReverse(true);
        tt.playFromStart();
    }
}