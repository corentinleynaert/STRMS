package com.application.strms.presentation.controller;

import com.application.strms.application.ApplicationContext;
import com.application.strms.application.result.LoginResult;
import com.application.strms.application.service.AuthService;
import com.application.strms.application.session.SessionManager;
import com.application.strms.presentation.navigation.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
        errors.setVisible(false);
        errors.setManaged(false);

        email_field.textProperty().addListener((_, _, _) -> enableLogin());
        password_field.textProperty().addListener((_, _, _) -> enableLogin());

        enableLogin();
    }

    @FXML
    protected void login() {
        AuthService authService = context.authService();
        SessionManager sessionManager = context.sessionManager();

        LoginResult result = authService.login(
                email_field.getText(),
                password_field.getText()
        );

        if (result.isSuccess()) {
            sessionManager.login(result.user());

            errors.setText("");
            errors.setManaged(false);
            errors.setVisible(false);

            navigator.goTo("Home");
        } else {
            errors.setText("Email or password is incorrect");
            errors.setManaged(true);
            errors.setVisible(true);
        }
    }

    @FXML
    protected void enableLogin() {
        boolean canLogin =
                email_field.getText() != null && !email_field.getText().isBlank() &&
                        password_field.getText() != null && !password_field.getText().isBlank();

        login_button.setDisable(!canLogin);
    }
}