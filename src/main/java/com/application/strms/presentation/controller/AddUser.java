package com.application.strms.presentation.controller;

import com.application.strms.application.result.AddUserResult;
import com.application.strms.application.result.LoginResult;
import com.application.strms.application.service.AuthService;
import com.application.strms.application.session.SessionManager;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
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

    @FXML
    public void initialize() {
        role_choice.getItems().addAll("ADMIN", "MANAGER", "ENGINEER");
        role_choice.setValue("ENGINEER");
    }

    @FXML
    protected void goBack() {
        navigator.goTo("Home");
    }

    @FXML protected void addUser() {
            AuthService authService = context.authService();
            SessionManager sessionManager = context.sessionManager();

            String name = name_field.getText().trim();
            String email = email_field.getText().trim();
            String password = password_field.getText();
            String role = role_choice.getValue();

            AddUserResult result = authService.addUser(sessionManager.currentUser(), name, email, password, role);

            if (result.isSuccess()) {
                handleSuccess();
                navigator.goTo("Home");
            } else {
                showError(result.error());
                shakeNode(email_field);
            }

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

    private void handleSuccess() {
        // Notifcation
    }
}
