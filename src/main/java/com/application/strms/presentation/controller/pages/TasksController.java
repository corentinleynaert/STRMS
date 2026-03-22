package com.application.strms.presentation.controller.pages;

import java.util.List;
import java.util.stream.Collectors;

import com.application.strms.application.result.DeleteTaskResult;
import com.application.strms.application.result.UpdateTaskResult;
import com.application.strms.application.service.TaskManager;
import com.application.strms.domain.model.Engineer;
import com.application.strms.domain.model.Task;
import com.application.strms.domain.model.User;
import com.application.strms.presentation.controller.BaseController;
import com.application.strms.presentation.model.TaskDisplay;
import com.application.strms.presentation.service.UiConstants;
import com.application.strms.presentation.service.UiUtils;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TasksController extends BaseController {
    @FXML
    private TableView<TaskDisplay> tasksTable;
    @FXML
    private Button editButton;
    @FXML
    private Label emptyStateLabel;
    @FXML
    private ChoiceBox<String> filterChoiceBox;

    @FXML
    public void initialize() {
        editButton.setDisable(true);
        tasksTable.getSelectionModel().selectedItemProperty()
                .addListener((_, _, newValue) -> editButton.setDisable(newValue == null));

        initializeFilterChoiceBox();
        addDependencyColumns();
    }

    private void initializeFilterChoiceBox() {
        filterChoiceBox.getItems().addAll("ALL", "READY", "IN_PROGRESS", "BLOCKED");
        filterChoiceBox.setValue("ALL");
        filterChoiceBox.valueProperty().addListener((_, _, newValue) -> {
            if (newValue != null) {
                applyFilter(newValue);
            }
        });
    }

    private void applyFilter(String filterValue) {
        try {
            TaskManager taskManager = context.getTaskManager();
            List<Task> tasks = switch (filterValue) {
                case "READY" -> taskManager.getReadyTasks();
                case "IN_PROGRESS" -> taskManager.getInProgressTasks();
                case "BLOCKED" -> taskManager.getBlockedTasks();
                default -> taskManager.getAllTasks();
            };
            loadTasksInTable(tasks);
        } catch (Exception e) {
            showEmptyState();
            navigator.notify("Error applying filter: " + e.getMessage());
        }
    }

    @Override
    protected void onReady() {
        if (context != null && context.getSessionManager().isAuthenticated()) {
            loadTasks();
        }
    }

    private void loadTasks() {
        try {
            TaskManager taskManager = context.getTaskManager();
            loadTasksInTable(taskManager.getAllTasks());
        } catch (Exception e) {
            showEmptyState();
            navigator.notify("Error loading tasks: " + e.getMessage());
        }
    }

    private void loadTasksInTable(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            showEmptyState();
        } else {
            tasksTable.getItems().setAll(
                    tasks.stream()
                            .map(TaskDisplay::new)
                            .toList());
            UiUtils.setVisibility(tasksTable, true);
            UiUtils.setVisibility(emptyStateLabel, false);
        }
    }

    private void refreshTasksWithCurrentFilter() {
        String currentFilter = filterChoiceBox.getValue();
        if (currentFilter != null) {
            applyFilter(currentFilter);
        } else {
            loadTasks();
        }
    }

    private void addDependencyColumns() {
        TableColumn<TaskDisplay, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(330);
        actionsCol.setCellFactory(_ -> new TableCell<>() {
            private final HBox hbox = new HBox(8);

            {
                hbox.setAlignment(Pos.CENTER);
                Button viewBtn = new Button("View");
                viewBtn.getStyleClass().add("popup-button");
                viewBtn.setOnAction(_ -> {
                    TaskDisplay taskDisplay = getTableView().getItems().get(getIndex());
                    if (taskDisplay != null) {
                        showDependenciesPopup(viewBtn, taskDisplay.getTask());
                    }
                });

                Button addBtn = new Button("+ Add");
                addBtn.getStyleClass().add("popup-button");
                addBtn.setOnAction(_ -> {
                    TaskDisplay taskDisplay = getTableView().getItems().get(getIndex());
                    if (taskDisplay != null) {
                        showAddDependencyPopup(addBtn, taskDisplay.getTask());
                    }
                });

                Button removeBtn = new Button("- Remove");
                removeBtn.getStyleClass().add("popup-button");
                removeBtn.setOnAction(_ -> {
                    TaskDisplay taskDisplay = getTableView().getItems().get(getIndex());
                    if (taskDisplay != null) {
                        showRemoveDependencyPopup(removeBtn, taskDisplay.getTask());
                    }
                });

                Button assignBtn = new Button("Assign");
                assignBtn.getStyleClass().add("secondary-button");
                assignBtn.setOnAction(_ -> {
                    TaskDisplay taskDisplay = getTableView().getItems().get(getIndex());
                    if (taskDisplay != null) {
                        showAssignEngineerPopup(assignBtn, taskDisplay.getTask());
                    }
                });

                Button unassignBtn = new Button("Unassign");
                unassignBtn.getStyleClass().add("secondary-button");
                unassignBtn.setOnAction(_ -> {
                    TaskDisplay taskDisplay = getTableView().getItems().get(getIndex());
                    if (taskDisplay != null) {
                        handleUnassignTask(taskDisplay.getTask());
                    }
                });

                Button deleteBtn = new Button("Delete");
                deleteBtn.getStyleClass().add("delete-button");
                deleteBtn.setOnAction(_ -> {
                    TaskDisplay taskDisplay = getTableView().getItems().get(getIndex());
                    if (taskDisplay != null) {
                        handleDeleteTask(taskDisplay.getTask());
                    }
                });

                hbox.getChildren().addAll(viewBtn, addBtn, removeBtn, assignBtn, unassignBtn, deleteBtn);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });

        tasksTable.getColumns().add(actionsCol);
    }

    private void showDependenciesPopup(Button sourceButton, Task task) {
        List<Task> dependencies = task.getDependencies();
        if (dependencies == null || dependencies.isEmpty()) {
            navigator.notify("No dependencies");
            return;
        }

        showSelectionPopup(sourceButton, dependencies, _ -> {
        });
    }

    private void showAddDependencyPopup(Button sourceButton, Task task) {
        try {
            TaskManager taskManager = context.getTaskManager();
            List<Task> allTasks = taskManager.getAllTasks();

            List<Task> validCandidates = allTasks.stream()
                    .filter(t -> !t.getUlid().equals(task.getUlid()))
                    .filter(t -> !task.getDependencies().contains(t))
                    .filter(t -> !t.getDependencies().contains(task))
                    .collect(Collectors.toList());

            if (validCandidates.isEmpty()) {
                navigator.notify("No available dependencies");
                return;
            }

            showSelectionPopup(sourceButton, validCandidates, selectedTask -> {
                try {
                    var result = taskManager.addDependency(task.getUlid(), selectedTask.getUlid(),
                            context.getSessionManager().getCurrentUser());

                    if (result.isSuccess()) {
                        navigator.notify(UiConstants.Messages.TASK_DEPENDENCY_ADDED);
                        refreshTasksWithCurrentFilter();
                    } else {
                        navigator.notify("Failed to add dependency");
                    }
                } catch (Exception e) {
                    navigator.notify("Error: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            navigator.notify("Error: " + e.getMessage());
        }
    }

    private void showRemoveDependencyPopup(Button sourceButton, Task task) {
        List<Task> dependencies = task.getDependencies();
        if (dependencies == null || dependencies.isEmpty()) {
            navigator.notify("No dependencies to remove");
            return;
        }

        showSelectionPopup(sourceButton, dependencies, selectedTask -> {
            try {
                TaskManager taskManager = context.getTaskManager();
                var result = taskManager.removeDependency(task.getUlid(), selectedTask.getUlid(),
                        context.getSessionManager().getCurrentUser());

                if (result.isSuccess()) {
                    navigator.notify(UiConstants.Messages.TASK_DEPENDENCY_REMOVED);
                    refreshTasksWithCurrentFilter();
                } else {
                    navigator.notify("Failed to remove dependency");
                }
            } catch (Exception e) {
                navigator.notify("Error: " + e.getMessage());
            }
        });
    }

    private void showSelectionPopup(Button sourceButton, List<Task> tasks, TaskSelectionCallback callback) {
        ListView<Task> listView = new ListView<>();
        listView.setPrefHeight(Math.min(tasks.size() * 30 + 20, 300));
        listView.setPrefWidth(250);
        listView.getItems().addAll(tasks);

        VBox popupContent = new VBox(listView);
        popupContent.getStyleClass().add("table-cell-styled");

        PopupControl popup = new PopupControl() {
            {
                getScene().setRoot(popupContent);
            }
        };
        popup.setAutoHide(true);
        popup.setAutoFix(true);

        final PopupControl finalPopup = popup;

        listView.setCellFactory(_ -> new ListCell<>() {
            private final Button selectBtn = new Button();

            {
                selectBtn.getStyleClass().add("popup-button");
                selectBtn.setMaxWidth(Double.MAX_VALUE);
            }

            @Override
            protected void updateItem(Task item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    selectBtn.setText(item.getTitle());
                    selectBtn.setOnAction(_ -> {
                        callback.onTaskSelected(item);
                        finalPopup.hide();
                    });
                    setGraphic(selectBtn);
                }
            }
        });

        Bounds bounds = sourceButton.localToScreen(sourceButton.getBoundsInLocal());
        popup.show(sourceButton.getScene().getWindow(), bounds.getCenterX(), bounds.getCenterY() + 30);
    }

    @FunctionalInterface
    private interface TaskSelectionCallback {
        void onTaskSelected(Task task);
    }

    private void showEmptyState() {
        UiUtils.setVisibility(tasksTable, false);
        UiUtils.setVisibility(emptyStateLabel, true);
    }

    @FXML
    protected void handleEdit() {
        TaskDisplay selectedTaskDisplay = tasksTable.getSelectionModel().getSelectedItem();
        if (selectedTaskDisplay != null) {
            navigateToUpdateTask(selectedTaskDisplay.getTask());
        }
    }

    @FXML
    protected void goToAddTask() {
        navigator.goTo(UiConstants.Pages.ADD_TASK);
    }

    @FXML
    protected void goBack() {
        navigator.goTo(UiConstants.Pages.HOME);
    }

    private void navigateToUpdateTask(Task task) {
        try {
            navigator.goTo(UiConstants.Pages.UPDATE_TASK, controller -> {
                if (controller instanceof UpdateTaskController updateController) {
                    updateController.setTaskToUpdate(task);
                }
            });
        } catch (Exception e) {
            navigator.notify("Error navigating to task update: " + e.getMessage());
        }
    }

    private void handleDeleteTask(Task task) {
        try {
            User currentUser = context.getSessionManager().getCurrentUser();
            if (currentUser == null) {
                navigator.notify("Error: Not authenticated");
                return;
            }

            TaskManager taskManager = context.getTaskManager();
            DeleteTaskResult result = taskManager.deleteTask(task.getUlid(), currentUser);

            if (result.isSuccess()) {
                navigator.notify("Task successfully deleted!");
                refreshTasksWithCurrentFilter();
            } else {
                navigator.notify("Error: " + result.getMessage());
            }
        } catch (Exception e) {
            navigator.notify("Error deleting task: " + e.getMessage());
        }
    }

    private void showAssignEngineerPopup(Button sourceButton, Task task) {
        try {
            List<User> users = context.getApplicationUserRepository().getAllUsers();
            List<Engineer> engineers = users.stream()
                    .filter(user -> user instanceof Engineer)
                    .map(user -> (Engineer) user)
                    .collect(Collectors.toList());

            if (engineers.isEmpty()) {
                navigator.notify("No engineers available");
                return;
            }

            showEngineerSelectionPopup(sourceButton, engineers, selectedEngineer -> {
                try {
                    TaskManager taskManager = context.getTaskManager();
                    UpdateTaskResult result = taskManager.assignTask(task.getUlid(), selectedEngineer,
                            context.getSessionManager().getCurrentUser());

                    if (result.isSuccess()) {
                        navigator.notify("Task assigned to: " + selectedEngineer.getName());
                        refreshTasksWithCurrentFilter();
                    } else {
                        navigator.notify("Failed to assign task");
                    }
                } catch (Exception e) {
                    navigator.notify("Error: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            navigator.notify("Error: " + e.getMessage());
        }
    }

    private void showEngineerSelectionPopup(Button sourceButton, List<Engineer> engineers,
            EngineerSelectionCallback callback) {
        ListView<Engineer> listView = new ListView<>();
        listView.setPrefHeight(Math.min(engineers.size() * 30 + 20, 300));
        listView.setPrefWidth(250);
        listView.getItems().addAll(engineers);

        VBox popupContent = new VBox(listView);
        popupContent.getStyleClass().add("table-cell-styled");

        PopupControl popup = new PopupControl() {
            {
                getScene().setRoot(popupContent);
            }
        };
        popup.setAutoHide(true);
        popup.setAutoFix(true);

        final PopupControl finalPopup = popup;

        listView.setCellFactory(_ -> new ListCell<>() {
            private final Button selectBtn = new Button();

            {
                selectBtn.getStyleClass().add("popup-button");
                selectBtn.setMaxWidth(Double.MAX_VALUE);
            }

            @Override
            protected void updateItem(Engineer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    selectBtn.setText(item.getName());
                    selectBtn.setOnAction(_ -> {
                        callback.onEngineerSelected(item);
                        finalPopup.hide();
                    });
                    setGraphic(selectBtn);
                }
            }
        });

        Bounds bounds = sourceButton.localToScreen(sourceButton.getBoundsInLocal());
        popup.show(sourceButton.getScene().getWindow(), bounds.getCenterX(), bounds.getCenterY() + 30);
    }

    private void handleUnassignTask(Task task) {
        try {
            User currentUser = context.getSessionManager().getCurrentUser();
            if (currentUser == null) {
                navigator.notify("Error: Not authenticated");
                return;
            }

            TaskManager taskManager = context.getTaskManager();
            UpdateTaskResult result = taskManager.unassignTask(task.getUlid(), currentUser);

            if (result.isSuccess()) {
                navigator.notify("Task unassigned");
                refreshTasksWithCurrentFilter();
            } else {
                navigator.notify("Error: " + result.getMessage());
            }
        } catch (Exception e) {
            navigator.notify("Error: " + e.getMessage());
        }
    }

    @FunctionalInterface
    private interface EngineerSelectionCallback {
        void onEngineerSelected(Engineer engineer);
    }
}
