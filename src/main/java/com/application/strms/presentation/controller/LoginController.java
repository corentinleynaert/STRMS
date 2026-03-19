package com.application.strms.presentation.controller;

import com.application.strms.application.ApplicationContext;
import com.application.strms.application.result.LoginResult;
import com.application.strms.application.service.AuthService;
import com.application.strms.application.session.SessionManager;
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
        hideError();

        email_field.textProperty().addListener((obs, oldValue, newValue) -> updateLoginButtonState());
        password_field.textProperty().addListener((obs, oldValue, newValue) -> updateLoginButtonState());

        setupButtonEffects();
        updateLoginButtonState();
    }

    @FXML
    protected void login() {
        AuthService authService = context.authService();
        SessionManager sessionManager = context.sessionManager();

        String email = email_field.getText().trim();
        String password = password_field.getText();

        LoginResult result = authService.login(email, password);

        if (result.isSuccess()) {
            sessionManager.login(result.user());
            handleSuccess();
            navigator.goTo("Home");
        } else {
            showError("Email or password is incorrect");
            shakeNode(email_field);
            shakeNode(password_field);
        }
    }

    private void handleSuccess() {
        hideError();
    }

    private void showError(String message) {
        errors.setText(message);
        errors.setManaged(true);
        errors.setVisible(true);
    }

    private void hideError() {
        errors.setText("");
        errors.setManaged(false);
        errors.setVisible(false);
    }

    private void updateLoginButtonState() {
        boolean isEmailEmpty = email_field.getText() == null || email_field.getText().trim().isEmpty();
        boolean isPasswordEmpty = password_field.getText() == null || password_field.getText().isEmpty();

        login_button.setDisable(isEmailEmpty || isPasswordEmpty);
    }

    @FXML
    protected void enableLogin() {
        updateLoginButtonState();
    }

    private void shakeNode(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
        tt.setFromX(0);
        tt.setByX(10);
        tt.setCycleCount(4);
        tt.setAutoReverse(true);
        tt.playFromStart();
    }

    private void setupButtonEffects() {
        login_button.setOnMouseEntered(_ -> login_button.setOpacity(0.9));
        login_button.setOnMouseExited(_ -> login_button.setOpacity(1.0));
    }
}