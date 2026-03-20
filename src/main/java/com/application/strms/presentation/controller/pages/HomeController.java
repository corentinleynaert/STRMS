package com.application.strms.presentation.controller.pages;

import com.application.strms.presentation.controller.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HomeController extends BaseController {
    @FXML private Label welcomeLabel;

    @Override
    protected void onReady() {
        if (context.getSessionManager().isAuthenticated()) {
            String userName = context.getSessionManager().getCurrentUser().getName();
            welcomeLabel.setText("Welcome, " + userName);
        }
    }
}