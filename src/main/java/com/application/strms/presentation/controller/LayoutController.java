package com.application.strms.presentation.controller;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class LayoutController {
    @FXML private StackPane contentContainer;
    @FXML private VBox notificationContainer;

    public void setContent(Node content) {
        contentContainer.getChildren().setAll(content);
    }

    public void showNotification(String message) {
        Label notification = new Label(message);
        notification.getStyleClass().add("notification");

        notification.setOpacity(0);

        notificationContainer.getChildren().add(notification);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), notification);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition stay = new PauseTransition(Duration.seconds(2.5));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), notification);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        fadeOut.setOnFinished(_ -> notificationContainer.getChildren().remove(notification));

        new SequentialTransition(fadeIn, stay, fadeOut).play();
    }
}