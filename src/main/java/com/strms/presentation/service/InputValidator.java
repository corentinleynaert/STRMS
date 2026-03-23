package com.strms.presentation.service;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextInputControl;

public final class InputValidator {
    public static void enableDisableButtonOnValidation(
            Button button,
            TextInputControl... textFields) {
        Runnable validator = () -> {
            boolean allFilled = true;
            for (TextInputControl field : textFields) {
                if (UiUtils.isNullOrEmpty(field.getText())) {
                    allFilled = false;
                    break;
                }
            }
            button.setDisable(!allFilled);
        };

        for (TextInputControl field : textFields) {
            field.textProperty().addListener((_, _, _) -> validator.run());
        }

        validator.run();
    }

    public static void enableDisableButtonOnValidation(
            Button button,
            ChoiceBox<?> choiceBox,
            TextInputControl... textFields) {
        Runnable validator = () -> {
            boolean allFilled = choiceBox.getValue() != null;
            if (allFilled) {
                for (TextInputControl field : textFields) {
                    if (UiUtils.isNullOrEmpty(field.getText())) {
                        allFilled = false;
                        break;
                    }
                }
            }
            button.setDisable(!allFilled);
        };

        for (TextInputControl field : textFields) {
            field.textProperty().addListener((_, _, _) -> validator.run());
        }
        choiceBox.valueProperty().addListener((_, _, _) -> validator.run());

        validator.run();
    }
}
