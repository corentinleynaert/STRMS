package com.strms.presentation.controller.pages;

import java.io.IOException;
import java.time.LocalDateTime;

import com.strms.application.result.UpdateTaskResult;
import com.strms.application.service.TaskManager;
import com.strms.domain.model.PriorityLevel;
import com.strms.domain.model.Task;
import com.strms.domain.model.TaskCategory;
import com.strms.presentation.controller.BaseController;
import com.strms.presentation.service.UiConstants;
import com.strms.presentation.service.UiUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class UpdateTaskController extends BaseController {
    @FXML
    private TextField titleField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private ChoiceBox<String> priorityChoiceBox;
    @FXML
    private ChoiceBox<String> categoryChoiceBox;
    @FXML
    private DatePicker deadlinePicker;
    @FXML
    private Label errorLabel;
    @FXML
    private Button updateTaskButton;

    private Task taskToUpdate;

    @FXML
    public void initialize() {
        priorityChoiceBox.getItems().addAll(
                PriorityLevel.LOW.name(),
                PriorityLevel.MEDIUM.name(),
                PriorityLevel.HIGH.name(),
                PriorityLevel.CRITICAL.name());

        categoryChoiceBox.getItems().addAll(
                TaskCategory.BUGFIX.name(),
                TaskCategory.FEATURE.name(),
                TaskCategory.DOCUMENTATION.name(),
                TaskCategory.RESEARCH.name());

        Runnable validator = () -> {
            boolean isValid = !UiUtils.isNullOrEmpty(titleField.getText()) &&
                    !UiUtils.isNullOrEmpty(descriptionArea.getText()) &&
                    priorityChoiceBox.getValue() != null &&
                    categoryChoiceBox.getValue() != null &&
                    deadlinePicker.getValue() != null;
            updateTaskButton.setDisable(!isValid);
        };

        titleField.textProperty().addListener((_, _, _) -> validator.run());
        descriptionArea.textProperty().addListener((_, _, _) -> validator.run());
        priorityChoiceBox.valueProperty().addListener((_, _, _) -> validator.run());
        categoryChoiceBox.valueProperty().addListener((_, _, _) -> validator.run());
        deadlinePicker.valueProperty().addListener((_, _, _) -> validator.run());

        validator.run();
        UiUtils.addButtonHoverEffect(updateTaskButton);

        UiUtils.addSelectAllOnFocus(titleField);
        UiUtils.addSelectAllOnFocus(descriptionArea);
    }

    public void setTaskToUpdate(Task task) {
        this.taskToUpdate = task;
        if (taskToUpdate != null) {
            populateFields(taskToUpdate);
        }
    }

    private void populateFields(Task task) {
        titleField.setText(task.getTitle());
        descriptionArea.setText(task.getDescription());
        priorityChoiceBox.setValue(task.getPriority().name());
        categoryChoiceBox.setValue(task.getCategory().name());
        if (task.getDeadline() != null) {
            deadlinePicker.setValue(task.getDeadline().toLocalDate());
        }
    }

    @FXML
    protected void goBack() {
        navigator.goTo(UiConstants.Pages.TASKS);
    }

    @FXML
    protected void updateTask() {
        if (taskToUpdate == null) {
            UiUtils.showError(errorLabel, UiConstants.Messages.NO_TASK_SELECTED);
            return;
        }

        TaskManager taskManager = context.getTaskManager();

        String title = UiUtils.trim(titleField.getText());
        String description = UiUtils.trim(descriptionArea.getText());
        String priorityStr = priorityChoiceBox.getValue();
        String categoryStr = categoryChoiceBox.getValue();
        LocalDateTime deadline = deadlinePicker.getValue() != null
                ? deadlinePicker.getValue().atStartOfDay()
                : null;

        try {
            PriorityLevel priority = PriorityLevel.valueOf(priorityStr);
            TaskCategory category = TaskCategory.valueOf(categoryStr);

            UpdateTaskResult result = taskManager.updateTask(
                    taskToUpdate.getUlid(),
                    title,
                    description,
                    priority,
                    category,
                    deadline,
                    context.getSessionManager().getCurrentUser());

            if (result.isSuccess()) {
                navigator.notify(UiConstants.Messages.TASK_UPDATED);
                navigator.goTo(UiConstants.Pages.TASKS);
            } else {
                UiUtils.showError(errorLabel, result.toString());
                UiUtils.shakeNode(titleField);
            }

        } catch (IOException e) {
            UiUtils.showError(errorLabel, UiConstants.Messages.ERROR_ACCESSING_TASK_DATA + e.getMessage());
            UiUtils.shakeNode(titleField);
        }
    }
}
