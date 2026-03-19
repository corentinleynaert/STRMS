package com.application.strms.presentation.controller;

import com.application.strms.domain.model.User;
import com.application.strms.presentation.controller.components.AdminTopbarController;
import com.application.strms.presentation.controller.components.EngineerTopbarController;
import com.application.strms.presentation.controller.components.ManagerTopbarController;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class LayoutController extends BaseController {

    @FXML private StackPane topbarContainerWrapper;
    @FXML private BorderPane adminTopbar;
    @FXML private BorderPane managerTopbar;
    @FXML private BorderPane engineerTopbar;
    @FXML private StackPane contentContainer;
    @FXML private VBox notificationContainer;

    @FXML private AdminTopbarController adminTopbarController;
    @FXML private ManagerTopbarController managerTopbarController;
    @FXML private EngineerTopbarController engineerTopbarController;

    @Override
    protected void onReady() {
        injectChildrenDependencies();
        updateTopbar();
    }

    private void injectChildrenDependencies() {
        if (adminTopbarController != null) {
            adminTopbarController.setApplicationContext(context);
            adminTopbarController.setNavigator(navigator);
        }

        if (managerTopbarController != null) {
            managerTopbarController.setApplicationContext(context);
            managerTopbarController.setNavigator(navigator);
        }

        if (engineerTopbarController != null) {
            engineerTopbarController.setApplicationContext(context);
            engineerTopbarController.setNavigator(navigator);
        }
    }

    public void setContent(Node content) {
        contentContainer.getChildren().setAll(content);
    }

    public void updateTopbar() {
        adminTopbar.setVisible(false);
        adminTopbar.setManaged(false);

        managerTopbar.setVisible(false);
        managerTopbar.setManaged(false);

        engineerTopbar.setVisible(false);
        engineerTopbar.setManaged(false);

        if (context == null || !context.getSessionManager().isAuthenticated()) {
            topbarContainerWrapper.setVisible(false);
            topbarContainerWrapper.setManaged(false);
            return;
        }

        topbarContainerWrapper.setVisible(true);
        topbarContainerWrapper.setManaged(true);

        User currentUser = context.getSessionManager().getCurrentUser();

        if (currentUser.isAdmin()) {
            adminTopbar.setVisible(true);
            adminTopbar.setManaged(true);
        } else if (currentUser.isManager()) {
            managerTopbar.setVisible(true);
            managerTopbar.setManaged(true);
        } else if (currentUser.isEngineer()) {
            engineerTopbar.setVisible(true);
            engineerTopbar.setManaged(true);
        }
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