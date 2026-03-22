package com.application.strms.presentation.controller.pages;

import java.util.List;

import com.application.strms.application.result.UpdateTaskResult;
import com.application.strms.application.service.TaskManager;
import com.application.strms.domain.model.Engineer;
import com.application.strms.domain.model.Task;
import com.application.strms.domain.model.TaskStatus;
import com.application.strms.domain.model.User;
import com.application.strms.presentation.controller.BaseController;
import com.application.strms.presentation.model.TaskDisplay;
import com.application.strms.presentation.service.UiConstants;
import com.application.strms.presentation.service.UiUtils;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

public class EngineerTasksController extends BaseController {
    @FXML
    private TableView<TaskDisplay> tasksTable;
    @FXML
    private Label emptyStateLabel;

    @FXML
    public void initialize() {
        addActionColumn();
    }

    @Override
    protected void onReady() {
        if (context != null && context.getSessionManager().isAuthenticated()) {
            loadAssignedTasks();
        }
    }

    @FXML
    protected void refreshTasks() {
        tasksTable.getItems().clear();
        loadAssignedTasks();
    }

    private void loadAssignedTasks() {
        try {
            User currentUser = context.getSessionManager().getCurrentUser();

            if (!(currentUser instanceof Engineer currentEngineer)) {
                showEmptyState();
                return;
            }

            TaskManager taskManager = context.getTaskManager();
            List<Task> allTasks = taskManager.getAllTasks();

            List<Task> assignedTasks = allTasks.stream()
                    .filter(task -> task.getAssignedEngineer() != null)
                    .filter(task -> task.getAssignedEngineer().getId().equals(currentEngineer.getId()))
                    .toList();

            if (assignedTasks.isEmpty()) {
                showEmptyState();
            } else {
                tasksTable.getItems().setAll(
                        assignedTasks.stream()
                                .map(TaskDisplay::new)
                                .toList());

                UiUtils.setVisibility(tasksTable, true);
                UiUtils.setVisibility(emptyStateLabel, false);
            }
        } catch (Exception e) {
            showEmptyState();
            navigator.notify("Error loading assigned tasks: " + e.getMessage());
        }
    }

    private void addActionColumn() {
        TableColumn<TaskDisplay, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(280);
        actionsCol.setCellFactory(_ -> new TableCell<>() {
            private final HBox hbox = new HBox(8);

            {
                hbox.setAlignment(Pos.CENTER);

                Button startBtn = new Button("Start");
                startBtn.getStyleClass().add("primary-button");
                startBtn.setPrefWidth(70);
                startBtn.setOnAction(_ -> {
                    TaskDisplay taskDisplay = getTableView().getItems().get(getIndex());
                    if (taskDisplay != null) {
                        handleStartTask(taskDisplay.getTask());
                    }
                });

                Button completeBtn = new Button("Complete");
                completeBtn.getStyleClass().add("primary-button");
                completeBtn.setPrefWidth(100);
                completeBtn.setOnAction(_ -> {
                    TaskDisplay taskDisplay = getTableView().getItems().get(getIndex());
                    if (taskDisplay != null) {
                        handleCompleteTask(taskDisplay.getTask());
                    }
                });

                Button detailsBtn = new Button("Details");
                detailsBtn.getStyleClass().add("secondary-button");
                detailsBtn.setOnAction(_ -> {
                    TaskDisplay taskDisplay = getTableView().getItems().get(getIndex());
                    if (taskDisplay != null) {
                        showTaskDetails(taskDisplay.getTask());
                    }
                });

                hbox.getChildren().addAll(startBtn, completeBtn, detailsBtn);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });

        tasksTable.getColumns().add(actionsCol);
    }

    private void handleStartTask(Task task) {
        try {
            User currentUser = context.getSessionManager().getCurrentUser();
            if (currentUser == null) {
                navigator.notify("Error: Not authenticated");
                return;
            }

            if (task.getStatus() == TaskStatus.DONE) {
                navigator.notify("Task is already done");
                return;
            }

            if (areDependenciesNotCompleted(task)) {
                navigator.notify("Cannot start task: dependencies not completed");
                return;
            }

            try {
                TaskManager taskManager = context.getTaskManager();
                UpdateTaskResult result = taskManager.changeTaskStatus(task.getUlid(), TaskStatus.IN_PROGRESS,
                        currentUser);

                if (result.isSuccess()) {
                    navigator.notify("Task started successfully!");
                    refreshTasks();
                } else {
                    navigator.notify("Error: " + result.getMessage());
                }
            } catch (IllegalArgumentException e) {
                navigator.notify("Error: " + e.getMessage());
            }
        } catch (Exception e) {
            navigator.notify("Error starting task: " + e.getMessage());
        }
    }

    private void handleCompleteTask(Task task) {
        try {
            User currentUser = context.getSessionManager().getCurrentUser();
            if (currentUser == null) {
                navigator.notify("Error: Not authenticated");
                return;
            }

            if (task.getStatus() == TaskStatus.DONE) {
                navigator.notify("Task is already done");
                return;
            }

            if (areDependenciesNotCompleted(task)) {
                navigator.notify("Cannot complete task: dependencies not completed");
                return;
            }

            try {
                TaskManager taskManager = context.getTaskManager();
                UpdateTaskResult result = taskManager.markTaskAsDone(task.getUlid(), currentUser);

                if (result.isSuccess()) {
                    navigator.notify("Task completed successfully!");
                    refreshTasks();
                } else {
                    navigator.notify("Error: " + result.getMessage());
                }
            } catch (IllegalArgumentException e) {
                navigator.notify("Error: " + e.getMessage());
            }
        } catch (Exception e) {
            navigator.notify("Error completing task: " + e.getMessage());
        }
    }

    private boolean areDependenciesNotCompleted(Task task) {
        List<Task> dependencies = task.getDependencies();
        if (dependencies == null || dependencies.isEmpty()) {
            return false;
        }

        for (Task dependency : dependencies) {
            if (dependency.getStatus() != TaskStatus.DONE) {
                return true;
            }
        }
        return false;
    }

    private void showTaskDetails(Task task) {
        try {
            navigator.goTo(UiConstants.Pages.ENGINEER_TASK_DETAILS, controller -> {
                if (controller instanceof EngineerTaskDetailsController detailsController) {
                    detailsController.setTaskToDisplay(task);
                }
            });
        } catch (Exception e) {
            navigator.notify("Error opening task details: " + e.getMessage());
        }
    }

    private void showEmptyState() {
        UiUtils.setVisibility(tasksTable, false);
        UiUtils.setVisibility(emptyStateLabel, true);
    }
}
