package com.application.strms.presentation.controller.pages;

import java.io.IOException;

import com.application.strms.application.result.UpdateUserResult;
import com.application.strms.application.service.AuthService;
import com.application.strms.application.session.SessionManager;
import com.application.strms.domain.model.User;
import com.application.strms.presentation.controller.BaseController;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.util.Duration;

public class UpdateUserController extends BaseController {
    @FXML
    private TextField name_field;
    @FXML
    private TextField email_field;
    @FXML
    private PasswordField password_field;
    @FXML
    private Label errors;
    @FXML
    private ChoiceBox<String> role_choice;
    @FXML
    private Button update_user_button;

    private User userToUpdate;

    @FXML
    public void initialize() {
        role_choice.getItems().addAll("ADMIN", "MANAGER", "ENGINEER");

        name_field.textProperty().addListener((_, _, _) -> updateUpdateUserButtonState());
        email_field.textProperty().addListener((_, _, _) -> updateUpdateUserButtonState());
        password_field.textProperty().addListener((_, _, _) -> updateUpdateUserButtonState());
        role_choice.valueProperty().addListener((_, _, _) -> updateUpdateUserButtonState());

        addSelectAllOnFocus(name_field);
        addSelectAllOnFocus(email_field);
        addSelectAllOnFocus(password_field);

        update_user_button.setOnMouseEntered(_ -> update_user_button.setOpacity(0.9));
        update_user_button.setOnMouseExited(_ -> update_user_button.setOpacity(1.0));

        updateUpdateUserButtonState();
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
            name_field.setText(userToUpdate.getName());
            email_field.setText(userToUpdate.getEmail().toString());
            role_choice.setValue(userToUpdate.getRole());

            password_field.clear();

            updateUpdateUserButtonState();
        }
    }

    public void setUserToUpdate(User user) {
        this.userToUpdate = user;
        if (userToUpdate != null) {
            name_field.setText(userToUpdate.getName());
            email_field.setText(userToUpdate.getEmail().toString());
            role_choice.setValue(userToUpdate.getRole());
            password_field.clear();
            updateUpdateUserButtonState();
        }
    }

    @FXML
    protected void goBack() {
        navigator.goTo("Home");
    }

    @FXML
    protected void updateUser() {
        if (userToUpdate == null) {
            showError("No user selected for update");
            return;
        }

        AuthService authService = context.getAuthService();
        SessionManager sessionManager = context.getSessionManager();

        String name = name_field.getText().trim();
        String email = email_field.getText().trim();
        String password = password_field.getText();
        String role = role_choice.getValue();

        try {
            UpdateUserResult result = authService.editUser(
                    sessionManager.getCurrentUser(),
                    userToUpdate.getId(),
                    name,
                    email,
                    role,
                    password);

            if (result.isSuccess()) {
                navigator.notify("User updated successfully!");
                navigator.goTo("Home");
            } else {
                showError(result.toString());
                shakeNode(email_field);
            }

        } catch (IOException e) {
            showError("Error accessing user data: " + e.getMessage());
            shakeNode(email_field);
        }
    }

    private void updateUpdateUserButtonState() {
        boolean isNameEmpty = name_field.getText() == null || name_field.getText().trim().isEmpty();
        boolean isEmailEmpty = email_field.getText() == null || email_field.getText().trim().isEmpty();
        boolean isPasswordEmpty = password_field.getText() == null || password_field.getText().isEmpty();
        boolean isRoleEmpty = role_choice.getValue() == null;

        update_user_button.setDisable(isNameEmpty || isEmailEmpty || isPasswordEmpty || isRoleEmpty);
    }

    private void addSelectAllOnFocus(TextInputControl textInput) {
        textInput.focusedProperty().addListener((_, _, newValue) -> {
            if (newValue) {
                textInput.selectAll();
            }
        });
    }

    private void shakeNode(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
        tt.setFromX(0);
        tt.setByX(10);
        tt.setCycleCount(4);
        tt.setAutoReverse(true);
        tt.playFromStart();
    }

    private void showError(String message) {
        errors.setText(message);
        errors.setManaged(true);
        errors.setVisible(true);
    }
}
