package com.application.strms.presentation.controller.pages;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.List;

import com.application.strms.application.result.UpdateTaskResult;
import com.application.strms.application.service.TaskManager;
import com.application.strms.domain.model.Task;
import com.application.strms.domain.model.TaskHistoryEntry;
import com.application.strms.domain.model.TaskStatus;
import com.application.strms.domain.model.User;
import com.application.strms.presentation.controller.BaseController;
import com.application.strms.presentation.service.UiConstants;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class EngineerTaskDetailsController extends BaseController {
    @FXML
    private Label taskTitleLabel;
    @FXML
    private Label titleValue;
    @FXML
    private Label statusValue;
    @FXML
    private Label priorityValue;
    @FXML
    private Label categoryValue;
    @FXML
    private Label deadlineValue;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private ListView<Task> dependenciesList;
    @FXML
    private Label noDependenciesLabel;
    @FXML
    private ListView<TaskHistoryEntry> historyList;
    @FXML
    private Label noHistoryLabel;
    @FXML
    private Button startButton;
    @FXML
    private Button completeButton;

    private Task currentTask;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {
        setupDependenciesListView();
        setupHistoryListView();
    }

    public void setTaskToDisplay(Task task) {
        this.currentTask = task;
        if (currentTask != null) {
            try {
                displayTaskDetails();
                updateButtonStates();
            } catch (Exception e) {
                navigator.notify("Error loading task details: " + e.getMessage());
            }
        }
    }

    private void displayTaskDetails() {
        taskTitleLabel.setText(currentTask.getTitle());
        titleValue.setText(currentTask.getTitle());
        statusValue.setText(currentTask.getStatus().name());
        priorityValue.setText(currentTask.getPriority().name());
        categoryValue.setText(currentTask.getCategory().name());

        if (currentTask.getDeadline() != null) {
            deadlineValue.setText(currentTask.getDeadline().format(DATE_FORMATTER));
        } else {
            deadlineValue.setText("No deadline");
        }

        descriptionArea.setText(currentTask.getDescription());

        dependenciesList.getItems().clear();
        List<Task> dependencies = currentTask.getDependencies();
        if (dependencies != null && !dependencies.isEmpty()) {
            dependenciesList.getItems().addAll(dependencies);
            dependenciesList.setVisible(true);
            dependenciesList.setManaged(true);
            noDependenciesLabel.setVisible(false);
            noDependenciesLabel.setManaged(false);
        } else {
            dependenciesList.setVisible(false);
            dependenciesList.setManaged(false);
            noDependenciesLabel.setVisible(true);
            noDependenciesLabel.setManaged(true);
        }

        historyList.getItems().clear();
        List<TaskHistoryEntry> history = currentTask.getHistory();
        if (history != null && !history.isEmpty()) {
            historyList.getItems().addAll(history);
            historyList.setVisible(true);
            historyList.setManaged(true);
            noHistoryLabel.setVisible(false);
            noHistoryLabel.setManaged(false);
        } else {
            historyList.setVisible(false);
            historyList.setManaged(false);
            noHistoryLabel.setVisible(true);
            noHistoryLabel.setManaged(true);
        }
    }

    private void setupDependenciesListView() {
        dependenciesList.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setText(null);
                } else {
                    setText(String.format("%s (%s)", task.getTitle(), task.getStatus()));
                }
            }
        });
    }

    private void setupHistoryListView() {
        historyList.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(TaskHistoryEntry entry, boolean empty) {
                super.updateItem(entry, empty);
                if (empty || entry == null) {
                    setText(null);
                } else {
                    String userName = entry.getPerformedBy() != null ? entry.getPerformedBy().getName() : "Unknown";
                    String timestamp = entry.getTimestamp() != null ? entry.getTimestamp().format(DATE_FORMATTER)
                            : "No timestamp";
                    setText(String.format("[%s] %s - %s", timestamp, entry.getAction(), userName));
                }
            }
        });
    }

    @FXML
    protected void handleStartTask() {
        try {
            User currentUser = context.getSessionManager().getCurrentUser();
            if (currentUser == null) {
                navigator.notify("Error: Not authenticated");
                return;
            }

            if (currentTask.getStatus() == TaskStatus.DONE) {
                navigator.notify("Task is already done");
                return;
            }

            if (!areDependenciesCompleted()) {
                navigator.notify("Cannot start task: dependencies not completed");
                return;
            }

            TaskManager taskManager = context.getTaskManager();
            UpdateTaskResult result = taskManager.changeTaskStatus(currentTask.getUlid(), TaskStatus.IN_PROGRESS,
                    currentUser);

            if (result.isSuccess()) {
                navigator.notify("Task started successfully!");
                currentTask = taskManager.findTaskById(currentTask.getUlid());
                displayTaskDetails();
                updateButtonStates();
            } else {
                navigator.notify("Error: " + result.getMessage());
            }
        } catch (Exception e) {
            navigator.notify("Error starting task: " + e.getMessage());
        }
    }

    @FXML
    protected void handleCompleteTask() {
        try {
            User currentUser = context.getSessionManager().getCurrentUser();
            if (currentUser == null) {
                navigator.notify("Error: Not authenticated");
                return;
            }

            if (currentTask.getStatus() == TaskStatus.DONE) {
                navigator.notify("Task is already done");
                return;
            }

            if (!areDependenciesCompleted()) {
                navigator.notify("Cannot complete task: dependencies not completed");
                return;
            }

            TaskManager taskManager = context.getTaskManager();
            UpdateTaskResult result = taskManager.markTaskAsDone(currentTask.getUlid(), currentUser);

            if (result.isSuccess()) {
                navigator.notify("Task completed successfully!");
                currentTask = taskManager.findTaskById(currentTask.getUlid());
                displayTaskDetails();
                updateButtonStates();
            } else {
                navigator.notify("Error: " + result.getMessage());
            }
        } catch (Exception e) {
            navigator.notify("Error completing task: " + e.getMessage());
        }
    }

    private boolean areDependenciesCompleted() {
        List<Task> dependencies = currentTask.getDependencies();
        if (dependencies == null || dependencies.isEmpty()) {
            return true;
        }

        for (Task dependency : dependencies) {
            if (dependency.getStatus() != TaskStatus.DONE) {
                return false;
            }
        }
        return true;
    }

    private void updateButtonStates() {
        TaskStatus currentStatus = currentTask.getStatus();
        boolean depsCompleted = areDependenciesCompleted();

        startButton.setDisable(
                currentStatus == TaskStatus.DONE || currentStatus == TaskStatus.IN_PROGRESS || !depsCompleted);

        completeButton.setDisable(currentStatus != TaskStatus.IN_PROGRESS || !depsCompleted);
    }

    @FXML
    protected void handleAddNote() {
        try {
            User currentUser = context.getSessionManager().getCurrentUser();
            if (currentUser == null) {
                navigator.notify("Error: Not authenticated");
                return;
            }

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Add Note");
            dialog.setHeaderText("Add a note to this task");

            TextArea noteArea = new TextArea();
            noteArea.setWrapText(true);
            noteArea.setPrefRowCount(6);
            noteArea.getStyleClass().add("note-area");
            noteArea.setPromptText("Enter your note here...");

            VBox content = new VBox(10);
            content.getStyleClass().add("dialog-content");
            content.getChildren().add(noteArea);
            content.setPrefWidth(500);

            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().addAll(
                    ButtonType.OK,
                    ButtonType.CANCEL);

            var result = dialog.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                String noteText = noteArea.getText().trim();
                if (noteText.isEmpty()) {
                    navigator.notify("Note cannot be empty");
                    return;
                }

                TaskHistoryEntry entry = new TaskHistoryEntry(
                        "NOTE: " + noteText,
                        currentUser,
                        LocalDateTime.now());

                TaskManager taskManager = context.getTaskManager();
                UpdateTaskResult addResult = taskManager.addTaskHistoryEntry(currentTask.getUlid(), entry);

                if (addResult.isSuccess()) {
                    navigator.notify("Note added successfully!");
                    currentTask = taskManager.findTaskById(currentTask.getUlid());
                    displayTaskDetails();
                } else {
                    navigator.notify("Error adding note: " + addResult.getMessage());
                }
            }
        } catch (Exception e) {
            navigator.notify("Error: " + e.getMessage());
        }
    }

    @FXML
    protected void goBack() {
        navigator.goTo(UiConstants.Pages.ENGINEER_TASKS);
    }
}
