package com.application.strms.presentation.controller.pages;

import java.io.IOException;

import com.application.strms.application.result.AddUserResult;
import com.application.strms.application.service.AuthService;
import com.application.strms.application.session.SessionManager;
import com.application.strms.presentation.controller.BaseController;
import com.application.strms.presentation.service.InputValidator;
import com.application.strms.presentation.service.UiConstants;
import com.application.strms.presentation.service.UiUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AddUserController extends BaseController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private ChoiceBox<String> roleChoiceBox;
    @FXML private Button addUserButton;

    @FXML
    public void initialize() {
        roleChoiceBox.getItems().addAll(
                UiConstants.Roles.ADMIN,
                UiConstants.Roles.MANAGER,
                UiConstants.Roles.ENGINEER);
        roleChoiceBox.setValue(UiConstants.Roles.ENGINEER);

        InputValidator.enableDisableButtonOnValidation(addUserButton, roleChoiceBox, nameField, emailField, passwordField);
        UiUtils.addButtonHoverEffect(addUserButton);
    }

    @FXML
    protected void goBack() {
        navigator.goTo(UiConstants.Pages.HOME);
    }

    @FXML
    protected void addUser() {
        AuthService authService = context.getAuthService();
        SessionManager sessionManager = context.getSessionManager();

        String name = UiUtils.trim(nameField.getText());
        String email = UiUtils.trim(emailField.getText());
        String password = passwordField.getText();
        String role = roleChoiceBox.getValue();

        try {
            AddUserResult result = authService.addUser(
                    sessionManager.getCurrentUser(),
                    name,
                    email,
                    password,
                    role);

            if (result.isSuccess()) {
                navigator.notify(UiConstants.Messages.USER_CREATED);
                navigator.goTo(UiConstants.Pages.HOME);
            } else {
                UiUtils.showError(errorLabel, result.toString());
                UiUtils.shakeNode(emailField);
            }

        } catch (IOException e) {
            UiUtils.showError(errorLabel, UiConstants.Messages.ERROR_ACCESSING_DATA + e.getMessage());
            UiUtils.shakeNode(emailField);
        }
    }
}