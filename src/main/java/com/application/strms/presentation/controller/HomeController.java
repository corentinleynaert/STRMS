package com.application.strms.presentation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class HomeController extends BaseController {
    @FXML  private Label welcome_label;
    @FXML  private VBox admin_panel;

    @Override
    protected void onReady() {
        if (context.getSessionManager().isAuthenticated()) {
            welcome_label.setText("Welcome, " + context.getSessionManager().getcurrentUser().getName());
        }

        if (context.getSessionManager().getcurrentUser().isAdmin()) {
            admin_panel.setManaged(true);
            admin_panel.setVisible(true);
        }
    }

    @FXML
    protected void logout() {
        context.getSessionManager().logout();
        navigator.goTo("Login");
    }

    @FXML
    protected void goToAddUser() {
        if (this.context.getSessionManager().getcurrentUser().isAdmin()) {
            navigator.goTo("AddUser");
        } else {
            throw new IllegalStateException("Only admins can access the add user page.");
        }
    }
}