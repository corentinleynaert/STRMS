package com.application.strms.presentation.controller.components;

import com.application.strms.presentation.controller.BaseController;
import javafx.fxml.FXML;

public class AdminTopbarController extends BaseController {
    @FXML
    protected void goToHome() {
        navigator.goTo("Home");
    }

    @FXML
    protected void goToAddUser() {
        navigator.goTo("AddUser");
    }

    @FXML
    protected void logout() {
        context.getSessionManager().logout();
        navigator.goTo("Login");
    }
}