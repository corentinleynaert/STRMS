package com.application.strms.presentation.service;

import java.util.List;
import java.util.Optional;

import com.application.strms.domain.model.Task;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class DependencyDialog {

    public static Optional<Task> showSelectDependencyDialog(List<Task> availableTasks, String title,
            String headerText) {
        if (availableTasks == null || availableTasks.isEmpty()) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle(title);
            alert.setHeaderText(headerText);
            alert.setContentText("No tasks available.");
            alert.showAndWait();
            return Optional.empty();
        }

        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);

        ListView<Task> listView = new ListView<>(FXCollections.observableArrayList(availableTasks));
        listView.setPrefHeight(300);
        listView.setPrefWidth(400);
        listView.setCellFactory(_ -> new javafx.scene.control.ListCell<Task>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                setText(empty ? null : task.getTitle() + " [" + task.getStatus().name() + "]");
            }
        });

        ScrollPane scrollPane = new ScrollPane(listView);
        scrollPane.setFitToWidth(true);

        VBox content = new VBox(12);
        content.setPadding(new Insets(12));
        content.getChildren().add(scrollPane);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setContent(content);

        dialog.getDialogPane().getButtonTypes().addAll(
                javafx.scene.control.ButtonType.OK,
                javafx.scene.control.ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == javafx.scene.control.ButtonType.OK) {
                return listView.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        return dialog.showAndWait();
    }
}
