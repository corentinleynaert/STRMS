package com.strms.presentation.controller.components;

import com.strms.presentation.service.UiConstants;
import javafx.fxml.FXML;

public class AdminTopbarController extends TopbarController {
    @FXML
    protected void goToHome() {
        navigateHome();
    }

    @FXML
    protected void goToUsers() {
        navigator.goTo(UiConstants.Pages.USERS);
    }

    @FXML
    protected void goToAnalytics() {
        navigator.goTo(UiConstants.Pages.ANALYTICS_REPORT);
    }

    @FXML
    protected void logout() {
        super.logout();
    }
}