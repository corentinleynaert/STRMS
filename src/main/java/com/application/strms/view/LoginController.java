package com.application.strms.view;

import com.application.strms.domain.service.AuthService;
import com.application.strms.utils.Validator;
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

    @FXML public void initialize() {
        errors.setVisible(false);
        errors.setManaged(false);

        email_field.textProperty().addListener(
                (observable, oldValue, newValue) -> enableLogin()
        );
        password_field.textProperty().addListener(
                (observable, oldValue, newValue) -> enableLogin()
        );

        enableLogin();
    }

    @FXML
    protected void login() {
        String email = email_field.getText();
        String password = password_field.getText();

        if (!authService.login(email, password)) {
            errors.setText("Email or password is incorrect");
            errors.setManaged(true);
            errors.setVisible(true);
        } else {
            errors.setText("");
            errors.setManaged(false);
            errors.setVisible(false);

            // Login
        }
    }

    @FXML
    protected void enableLogin() {
        String email = email_field.getText();
        String password = password_field.getText();

        boolean canLogin = Validator
                            .checkEmail(email) &&
                            password != null &&
                            !password.isBlank();

        login_button.setDisable(!canLogin);
    }
}