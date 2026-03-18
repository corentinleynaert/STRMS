package com.streat.strms;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.util.regex.Pattern;

public class Login {
    @FXML
    private TextField email_field;
    @FXML
    private PasswordField password_field;
    @FXML
    private Label errors;
    @FXML
    private Button login;

    private String email;
    private String password;

    @FXML
    public void initialize() {
        email_field.textProperty().addListener((observable, oldValue, newValue) -> {
            this.email = newValue;
            enableLogin();
        });

        password_field.textProperty().addListener((observable, oldValue, newValue) -> {
            this.password = newValue;
            enableLogin();
        });
    }

    @FXML
    protected void login() {
        if (!TaskManager.check_user_credentials(this.email, this.password)) {
            errors.setManaged(true);
            errors.setVisible(true);
        } else {
            // Se connecter
        }
    }

    @FXML
    protected void enableLogin() {
        login.setDisable(!(Validator.checkEmail(email) && password != null));
    }
}
