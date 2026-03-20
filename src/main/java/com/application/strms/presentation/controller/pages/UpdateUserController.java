package com.application.strms.presentation.controller.pages;

import java.io.IOException;

import com.application.strms.application.result.UpdateUserResult;
import com.application.strms.application.service.AuthService;
import com.application.strms.application.session.SessionManager;
import com.application.strms.domain.model.User;
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

public class UpdateUserController extends BaseController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;
    @FXML
    private ChoiceBox<String> roleChoiceBox;
    @FXML
    private Button updateUserButton;

    private User userToUpdate;

    @FXML
    public void initialize() {
        roleChoiceBox.getItems().addAll(
                UiConstants.Roles.ADMIN,
                UiConstants.Roles.MANAGER,
                UiConstants.Roles.ENGINEER);

        InputValidator.enableDisableButtonOnValidation(updateUserButton, roleChoiceBox, nameField, emailField,
                passwordField);
        UiUtils.addButtonHoverEffect(updateUserButton);

        UiUtils.addSelectAllOnFocus(nameField);
        UiUtils.addSelectAllOnFocus(emailField);
        UiUtils.addSelectAllOnFocus(passwordField);
    }

    @Override
    protected void onReady() {
        if (context != null && context.getSessionManager().isAuthenticated()) {
            loadUserData();
        }
    }

    private void loadUserData() {
        this.userToUpdate = context.getSessionManager().getCurrentUser();
        if (userToUpdate != null) {
            populateFields(userToUpdate);
        }
    }

    public void setUserToUpdate(User user) {
        this.userToUpdate = user;
        if (userToUpdate != null) {
            populateFields(userToUpdate);
        }
    }

    private void populateFields(User user) {
        nameField.setText(user.getName());
        emailField.setText(user.getEmail().toString());
        roleChoiceBox.setValue(user.getRole().getIdentifier());
        passwordField.clear();
    }

    @FXML
    protected void goBack() {
        navigator.goTo(UiConstants.Pages.USERS);
    }

    @FXML
    protected void updateUser() {
        if (userToUpdate == null) {
            UiUtils.showError(errorLabel, UiConstants.Messages.NO_USER_SELECTED);
            return;
        }

        AuthService authService = context.getAuthService();
        SessionManager sessionManager = context.getSessionManager();

        String name = UiUtils.trim(nameField.getText());
        String email = UiUtils.trim(emailField.getText());
        String password = passwordField.getText();
        String role = roleChoiceBox.getValue();

        try {
            UpdateUserResult result = authService.editUser(
                    sessionManager.getCurrentUser(),
                    userToUpdate.getId(),
                    name,
                    email,
                    role,
                    password);

            if (result.isSuccess()) {
                navigator.notify(UiConstants.Messages.USER_UPDATED);
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
