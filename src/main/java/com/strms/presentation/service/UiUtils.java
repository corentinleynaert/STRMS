package com.strms.presentation.service;

import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.util.Duration;

public final class UiUtils {

    private UiUtils() {
        throw new AssertionError("Utility class");
    }

    public static void shakeNode(Node node) {
        TranslateTransition transition = new TranslateTransition(
                Duration.millis(UiConstants.Animations.SHAKE_DURATION_MS),
                node);
        transition.setFromX(0);
        transition.setByX(UiConstants.Animations.SHAKE_DISTANCE);
        transition.setCycleCount(UiConstants.Animations.SHAKE_CYCLES);
        transition.setAutoReverse(true);
        transition.playFromStart();
    }

    public static void addButtonHoverEffect(Button button) {
        button.setOnMouseEntered(_ -> button.setOpacity(UiConstants.Animations.BUTTON_HOVERED_OPACITY));
        button.setOnMouseExited(_ -> button.setOpacity(UiConstants.Animations.BUTTON_NORMAL_OPACITY));
    }

    public static void addSelectAllOnFocus(TextInputControl textInput) {
        textInput.focusedProperty().addListener((_, _, newValue) -> {
            if (newValue) {
                textInput.selectAll();
            }
        });
    }

    public static void showError(Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setManaged(true);
        errorLabel.setVisible(true);
    }

    public static void setVisibility(Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }

    public static String trim(String value) {
        return value != null ? value.trim() : "";
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
