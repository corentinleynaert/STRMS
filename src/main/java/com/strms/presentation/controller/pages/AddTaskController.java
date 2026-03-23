package com.strms.presentation.controller.pages;

import java.io.IOException;
import java.time.LocalDateTime;

import com.strms.application.result.CreateTaskResult;
import com.strms.application.service.TaskManager;
import com.strms.domain.model.PriorityLevel;
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

public class AddTaskController extends BaseController {
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
    private Button addTaskButton;

    @FXML
    public void initialize() {
        priorityChoiceBox.getItems().addAll(
                PriorityLevel.LOW.name(),
                PriorityLevel.MEDIUM.name(),
                PriorityLevel.HIGH.name(),
                PriorityLevel.CRITICAL.name());
        priorityChoiceBox.setValue(PriorityLevel.MEDIUM.name());

        categoryChoiceBox.getItems().addAll(
                TaskCategory.BUGFIX.name(),
                TaskCategory.FEATURE.name(),
                TaskCategory.DOCUMENTATION.name(),
                TaskCategory.RESEARCH.name());
        categoryChoiceBox.setValue(TaskCategory.FEATURE.name());

        Runnable validator = () -> {
            boolean isValid = !UiUtils.isNullOrEmpty(titleField.getText()) &&
                    !UiUtils.isNullOrEmpty(descriptionArea.getText()) &&
                    priorityChoiceBox.getValue() != null &&
                    categoryChoiceBox.getValue() != null &&
                    deadlinePicker.getValue() != null;
            addTaskButton.setDisable(!isValid);
        };

        titleField.textProperty().addListener((_, _, _) -> validator.run());
        descriptionArea.textProperty().addListener((_, _, _) -> validator.run());
        priorityChoiceBox.valueProperty().addListener((_, _, _) -> validator.run());
        categoryChoiceBox.valueProperty().addListener((_, _, _) -> validator.run());
        deadlinePicker.valueProperty().addListener((_, _, _) -> validator.run());

        validator.run();
        UiUtils.addButtonHoverEffect(addTaskButton);

        UiUtils.addSelectAllOnFocus(titleField);
        UiUtils.addSelectAllOnFocus(descriptionArea);
    }

    @FXML
    protected void goBack() {
        navigator.goTo(UiConstants.Pages.TASKS);
    }

    @FXML
    protected void addTask() {
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

            CreateTaskResult result = taskManager.createTask(
                    title,
                    description,
                    priority,
                    category,
                    deadline,
                    context.getSessionManager().getCurrentUser());

            if (result.isSuccess()) {
                navigator.notify(UiConstants.Messages.TASK_CREATED);
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
