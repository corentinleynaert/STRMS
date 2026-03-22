package com.application.strms.presentation.controller.pages;

import com.application.strms.application.service.TaskManager;
import com.application.strms.domain.model.*;
import com.application.strms.domain.repository.UserRepository;
import com.application.strms.presentation.controller.BaseController;
import com.application.strms.presentation.service.Dashboard;
import com.application.strms.presentation.service.ReportGenerator;
import com.application.strms.presentation.service.NotificationManager;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.time.LocalDateTime;

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
    private Dashboard dashboard;

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
        dashboard = new Dashboard(taskManager, userRepository);

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
        totalTasksLabel.setText(String.valueOf(dashboard.getTotalTasks()));
        todoTasksLabel.setText(String.valueOf(dashboard.getTodoTasks()));
        inProgressTasksLabel.setText(String.valueOf(dashboard.getInProgressTasks()));
        blockedTasksLabel.setText(String.valueOf(dashboard.getBlockedTasks()));
        doneTasksLabel.setText(String.valueOf(dashboard.getDoneTasks()));
        totalUsersLabel.setText(String.valueOf(dashboard.getTotalUsers()));
        engineersLabel.setText(String.valueOf(dashboard.getEngineersCount()));

        LocalDateTime now = LocalDateTime.now();
        long overdueCount = taskManager.getAllTasks().stream()
                .filter(task -> task.getDeadline() != null && task.getDeadline().isBefore(now))
                .filter(task -> task.getStatus() != TaskStatus.DONE)
                .count();
        overdueTasksLabel.setText(String.valueOf(overdueCount));
    }

    private void notifyOverdueTasksIfAny() {
        LocalDateTime now = LocalDateTime.now();

        taskManager.getAllTasks().stream()
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

}
