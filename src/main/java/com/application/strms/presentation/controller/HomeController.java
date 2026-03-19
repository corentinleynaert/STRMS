package com.application.strms.presentation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class HomeController extends BaseController {
    @FXML private Label welcome_label;

    @Override
    protected void onReady() {
        if (context.sessionManager().isAuthenticated()) {
            welcome_label.setText("Welcome, " + context.sessionManager().currentUser().name());
        }
    }

    @FXML
    protected void logout() {
        context.sessionManager().logout();
        navigator.goTo("Login");
    }
}