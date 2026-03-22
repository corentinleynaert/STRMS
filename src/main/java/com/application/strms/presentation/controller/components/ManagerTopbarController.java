package com.application.strms.presentation.controller.components;

import com.application.strms.presentation.service.UiConstants;
import javafx.fxml.FXML;

public class ManagerTopbarController extends TopbarController {
    @FXML
    protected void goToHome() {
        navigateHome();
    }

    @FXML
    protected void goToTasks() {
        navigator.goTo(UiConstants.Pages.TASKS);
    }

    @FXML
    protected void logout() {
        super.logout();
    }
}
