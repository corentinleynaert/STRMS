package com.application.strms.presentation.controller.pages;

import com.application.strms.presentation.controller.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HomeController extends BaseController {
    @FXML  private Label welcome_label;

    @Override
    protected void onReady() {
        if (context.getSessionManager().isAuthenticated()) {
            welcome_label.setText("Welcome, " + context.getSessionManager().getCurrentUser().getName());
        }
    }
}