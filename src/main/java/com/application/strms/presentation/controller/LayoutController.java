package com.application.strms.presentation.controller;

import com.application.strms.domain.model.User;
import com.application.strms.presentation.controller.components.AdminTopbarController;
import com.application.strms.presentation.controller.components.EngineerTopbarController;
import com.application.strms.presentation.controller.components.ManagerTopbarController;
import com.application.strms.presentation.service.UiConstants;
import com.application.strms.presentation.service.UiUtils;
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
        for (BaseController topbarController : new BaseController[] {
                adminTopbarController,
                managerTopbarController,
                engineerTopbarController
        }) {
            if (topbarController != null) {
                topbarController.setApplicationContext(context);
                topbarController.setNavigator(navigator);
            }
        }
    }

    public void setContent(Node content) {
        contentContainer.getChildren().setAll(content);
    }

    public void updateTopbar() {
        hideAllTopbars();

        if (context == null || !context.getSessionManager().isAuthenticated()) {
            UiUtils.setVisibility(topbarContainerWrapper, false);
            return;
        }

        UiUtils.setVisibility(topbarContainerWrapper, true);
        User currentUser = context.getSessionManager().getCurrentUser();
        String roleIdentifier = currentUser.getRole().getIdentifier();

        switch (roleIdentifier) {
            case "ADMIN" -> UiUtils.setVisibility(adminTopbar, true);
            case "MANAGER" -> UiUtils.setVisibility(managerTopbar, true);
            case "ENGINEER" -> UiUtils.setVisibility(engineerTopbar, true);
        }
    }

    private void hideAllTopbars() {
        UiUtils.setVisibility(adminTopbar, false);
        UiUtils.setVisibility(managerTopbar, false);
        UiUtils.setVisibility(engineerTopbar, false);
    }

    public void showNotification(String message) {
        Label notification = new Label(message);
        notification.getStyleClass().add(UiConstants.Styles.NOTIFICATION);
        notification.setOpacity(0);

        notificationContainer.getChildren().add(notification);

        FadeTransition fadeIn = new FadeTransition(
                Duration.millis(UiConstants.Animations.FADE_TRANSITION_MS),
                notification);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition stay = new PauseTransition(
                Duration.millis(UiConstants.Animations.PAUSE_BEFORE_REMOVE_MS));

        FadeTransition fadeOut = new FadeTransition(
                Duration.millis(UiConstants.Animations.FADE_TRANSITION_MS),
                notification);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        fadeOut.setOnFinished(_ -> notificationContainer.getChildren().remove(notification));

        new SequentialTransition(fadeIn, stay, fadeOut).play();
    }
}