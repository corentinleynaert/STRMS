package com.application.strms.presentation.controller.components;

import com.application.strms.presentation.service.UiConstants;
import javafx.fxml.FXML;

public class EngineerTopbarController extends TopbarController {
    @FXML
    protected void goToHome() {
        navigateHome();
    }

    @FXML
    protected void goToMyTasks() {
        navigator.goTo(UiConstants.Pages.ENGINEER_TASKS);
    }

    @FXML
    protected void logout() {
        super.logout();
    }
}
