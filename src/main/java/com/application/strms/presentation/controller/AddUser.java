package com.application.strms.presentation.controller;

import java.io.IOException;

import com.application.strms.application.result.AddUserResult;
import com.application.strms.application.service.AuthService;
import com.application.strms.application.session.SessionManager;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class AddUser extends BaseController {
    @FXML private TextField name_field;
    @FXML private TextField email_field;
    @FXML private TextField password_field;
    @FXML private Label errors;
    @FXML private ChoiceBox<String> role_choice;
    @FXML private Button add_user_button;

    @FXML
    public void initialize() {
        role_choice.getItems().addAll("ADMIN", "MANAGER", "ENGINEER");
        role_choice.setValue("ENGINEER");

        name_field.textProperty().addListener((_, _, _) -> updateAddUserButtonState());
        email_field.textProperty().addListener((_, _, _) -> updateAddUserButtonState());
        password_field.textProperty().addListener((_, _, _) -> updateAddUserButtonState());
        role_choice.valueProperty().addListener((_, _, _) -> updateAddUserButtonState());

        add_user_button.setOnMouseEntered(_ -> add_user_button.setOpacity(0.9));
        add_user_button.setOnMouseExited(_ -> add_user_button.setOpacity(1.0));

        updateAddUserButtonState();
    }

    @FXML
    protected void goBack() {
        navigator.goTo("Home");
    }

    @FXML
    protected void addUser() {
        AuthService authService = context.getAuthService();
        SessionManager sessionManager = context.getSessionManager();

        String name = name_field.getText().trim();
        String email = email_field.getText().trim();
        String password = password_field.getText();
        String role = role_choice.getValue();

        try {
            AddUserResult result = authService.addUser(
                    sessionManager.getcurrentUser(),
                    name,
                    email,
                    password,
                    role
            );

            if (result.isSuccess()) {
                navigator.notify("User created successfully!");
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

    private void updateAddUserButtonState() {
        boolean isNameEmpty = name_field.getText() == null || name_field.getText().trim().isEmpty();
        boolean isEmailEmpty = email_field.getText() == null || email_field.getText().trim().isEmpty();
        boolean isPasswordEmpty = password_field.getText() == null || password_field.getText().isEmpty();
        boolean isRoleEmpty = role_choice.getValue() == null;

        add_user_button.setDisable(isNameEmpty || isEmailEmpty || isPasswordEmpty || isRoleEmpty);
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