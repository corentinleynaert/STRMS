package com.application.strms.presentation.controller.components;

import com.application.strms.presentation.controller.BaseController;
import javafx.fxml.FXML;

public class ManagerTopbarController extends BaseController {
    @FXML
    protected void goToHome() {
        navigator.goTo("Home");
    }

    @FXML
    protected void logout() {
        context.getSessionManager().logout();
        navigator.goTo("Login");
    }
}
