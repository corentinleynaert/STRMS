package com.application.strms.presentation.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class LayoutController {
    @FXML
    private StackPane contentContainer;

    public void setContent(Node content) {
        contentContainer.getChildren().setAll(content);
    }

    public void addContent(Node content) {
        contentContainer.getChildren().add(content);
    }

    public void clear() {
        contentContainer.getChildren().clear();
    }
}