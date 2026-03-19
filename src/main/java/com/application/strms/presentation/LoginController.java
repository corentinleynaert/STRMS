package com.application.strms.presentation;

import com.application.strms.application.service.AuthService;
import com.application.strms.application.result.LoginResult;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    private AuthService authService;

    @FXML private TextField email_field;
    @FXML private PasswordField password_field;
    @FXML private Label errors;
    @FXML private Button login_button;

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @FXML
    public void initialize() {
        errors.setVisible(false);
        errors.setManaged(false);

        email_field.textProperty().addListener((_, _, _) -> enableLogin());
        password_field.textProperty().addListener((_, _, _) -> enableLogin());

        enableLogin();
    }

    @FXML
    protected void login() {
        String email = email_field.getText();
        String password = password_field.getText();

        LoginResult result = authService.login(email, password);

        switch (result) {
            case SUCCESS -> {
                errors.setText("");
                errors.setManaged(false);
                errors.setVisible(false);

                // go to next screen
            }
            case USER_NOT_FOUND, INVALID_PASSWORD -> {
                errors.setText("Email or password is incorrect");
                errors.setManaged(true);
                errors.setVisible(true);
            }
        }
    }

    @FXML
    protected void enableLogin() {
        String email = email_field.getText();
        String password = password_field.getText();

        boolean canLogin =
                email != null && !email.isBlank() &&
                        password != null && !password.isBlank();

        login_button.setDisable(!canLogin);
    }
}