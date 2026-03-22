package com.application.strms.presentation.controller.pages;

import com.application.strms.application.service.TaskManager;
import com.application.strms.domain.model.*;
import com.application.strms.domain.repository.UserRepository;
import com.application.strms.presentation.controller.BaseController;
import com.application.strms.presentation.service.ReportGenerator;
import com.application.strms.presentation.service.NotificationManager;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.time.LocalDateTime;
import java.util.List;

public class AnalyticsReportController extends BaseController {

    @FXML
    private Label totalTasksLabel;
    @FXML
    private Label inProgressTasksLabel;
    @FXML
    private Label blockedTasksLabel;
    @FXML
    private Label overdueTasksLabel;
    @FXML
    private Label totalUsersLabel;
    @FXML
    private Label engineersLabel;
    @FXML
    private Label todoTasksLabel;
    @FXML
    private Label doneTasksLabel;

    @FXML
    private ComboBox<String> reportTypeCombo;
    @FXML
    private TextArea reportTextArea;

    private ReportGenerator reportGenerator;
    private NotificationManager notificationManager;
    private TaskManager taskManager;
    private UserRepository userRepository;

    private static final String REPORT_COMPREHENSIVE = "Comprehensive Report";
    private static final String REPORT_TASK = "Task Report";
    private static final String REPORT_USER = "User Report";
    private static final String REPORT_OVERDUE = "Overdue Tasks Report";
    private static final String REPORT_PRIORITY = "Tasks by Priority";
    private static final String REPORT_STATUS = "Tasks by Status";

    @Override
    protected void onReady() {
        reportGenerator = context.getReportGenerator();
        notificationManager = context.getNotificationManager();
        taskManager = context.getTaskManager();
        userRepository = context.getApplicationUserRepository();

        initializeReportCombo();
        loadStatistics();
        notifyOverdueTasksIfAny();
    }

    private void initializeReportCombo() {
        reportTypeCombo.getItems().addAll(
                REPORT_COMPREHENSIVE,
                REPORT_TASK,
                REPORT_USER,
                REPORT_OVERDUE,
                REPORT_PRIORITY,
                REPORT_STATUS);
        reportTypeCombo.getSelectionModel().selectFirst();
    }

    private void loadStatistics() {
        List<Task> allTasks = getAllTasks();
        List<User> allUsers = userRepository.getAllUsers();

        long todoCount = taskManager.getReadyTasks().size();
        long inProgressCount = taskManager.getInProgressTasks().size();
        long blockedCount = taskManager.getBlockedTasks().size();
        long doneCount = 0;

        LocalDateTime now = LocalDateTime.now();
        long overdueCount = allTasks.stream()
                .filter(task -> task.getDeadline() != null && task.getDeadline().isBefore(now))
                .filter(task -> task.getStatus() != TaskStatus.DONE)
                .count();

        long engineerCount = allUsers.stream()
                .filter(u -> u instanceof Engineer)
                .count();

        totalTasksLabel.setText(String.valueOf(allTasks.size()));
        inProgressTasksLabel.setText(String.valueOf(inProgressCount));
        blockedTasksLabel.setText(String.valueOf(blockedCount));
        overdueTasksLabel.setText(String.valueOf(overdueCount));
        totalUsersLabel.setText(String.valueOf(allUsers.size()));
        engineersLabel.setText(String.valueOf(engineerCount));
        todoTasksLabel.setText(String.valueOf(todoCount));
        doneTasksLabel.setText(String.valueOf(doneCount));
    }

    private void notifyOverdueTasksIfAny() {
        List<Task> allTasks = getAllTasks();
        LocalDateTime now = LocalDateTime.now();

        allTasks.stream()
                .filter(task -> task.getDeadline() != null && task.getDeadline().isBefore(now))
                .filter(task -> task.getStatus() != TaskStatus.DONE)
                .forEach(notificationManager::notifyDeadline);
    }

    @FXML
    private void generateReport() {
        String selectedReport = reportTypeCombo.getSelectionModel().getSelectedItem();
        if (selectedReport == null) {
            reportTextArea.setText("Please select a report type.");
            return;
        }

        String reportContent = switch (selectedReport) {
            case REPORT_COMPREHENSIVE -> reportGenerator.generateComprehensiveReport(taskManager, userRepository);
            case REPORT_TASK -> reportGenerator.generateTaskReport(taskManager);
            case REPORT_USER -> reportGenerator.generateUserReport(userRepository);
            case REPORT_OVERDUE -> reportGenerator.generateOverdueTasksReport(taskManager);
            case REPORT_PRIORITY -> reportGenerator.generateTasksByPriorityReport(taskManager);
            case REPORT_STATUS -> reportGenerator.generateTasksByStatusReport(taskManager);
            default -> reportGenerator.generateReport();
        };

        reportTextArea.setText(reportContent);
    }

    private List<Task> getAllTasks() {
        List<Task> allTasks = taskManager.getReadyTasks();
        allTasks.addAll(taskManager.getInProgressTasks());
        allTasks.addAll(taskManager.getBlockedTasks());
        return allTasks;
    }
}
