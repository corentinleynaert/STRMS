package com.application.strms.presentation.controller;

import com.application.strms.domain.model.Admin;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class HomeController extends BaseController {
    @FXML private Label welcome_label;
    @FXML private VBox admin_panel;

    @Override
    protected void onReady() {
        if (context.sessionManager().isAuthenticated()) {
            welcome_label.setText("Welcome, " + context.sessionManager().currentUser().name());
        }

        if(context.sessionManager().currentUser().isAdmin()) {
            admin_panel.setManaged(true);
            admin_panel.setVisible(true);
        }
    }

    @FXML
    protected void logout() {
        context.sessionManager().logout();
        navigator.goTo("Login");
    }

    @FXML
    protected void goToAddUser() {
        if (this.context.sessionManager().currentUser().isAdmin()) {
            navigator.goTo("AddUser");
        } else {
            throw new IllegalStateException("Only admins can access the add user page.");
        }
    }
}