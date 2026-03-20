package com.application.strms.presentation.controller.components;

import javafx.fxml.FXML;

public class ManagerTopbarController extends TopbarController {
    @FXML
    protected void goToHome() {
        navigateHome();
    }

    @FXML
    protected void logout() {
        super.logout();
    }
}
