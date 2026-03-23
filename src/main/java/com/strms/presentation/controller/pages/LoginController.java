package com.strms.presentation.controller.pages;

import java.io.IOException;

import com.strms.application.result.LoginResult;
import com.strms.application.service.AuthService;
import com.strms.application.session.SessionManager;
import com.strms.presentation.controller.BaseController;
import com.strms.presentation.service.InputValidator;
import com.strms.presentation.service.UiConstants;
import com.strms.presentation.service.UiUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController extends BaseController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

    @FXML
    public void initialize() {
        InputValidator.enableDisableButtonOnValidation(loginButton, emailField, passwordField);
        UiUtils.addButtonHoverEffect(loginButton);
    }

    @FXML
    protected void login() {
        AuthService authService = context.getAuthService();
        SessionManager sessionManager = context.getSessionManager();

        String email = UiUtils.trim(emailField.getText());
        String password = passwordField.getText();

        try {
            LoginResult result = authService.login(email, password);

            if (result.isSuccess()) {
                sessionManager.login(result.user());
                navigator.goTo(UiConstants.Pages.HOME);
            } else {
                UiUtils.showError(errorLabel, UiConstants.Messages.INVALID_CREDENTIALS);
                UiUtils.shakeNode(emailField);
                UiUtils.shakeNode(passwordField);
            }
        } catch (IOException e) {
            UiUtils.showError(errorLabel, UiConstants.Messages.ERROR_ACCESSING_DATA + e.getMessage());
            UiUtils.shakeNode(emailField);
            UiUtils.shakeNode(passwordField);
        }
    }
}