package com.application.strms.presentation.controller.pages;

import com.application.strms.application.service.Dashboard;
import com.application.strms.application.service.ReportGenerator;
import com.application.strms.presentation.controller.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

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
        dashboard = context.getDashboard();

        initializeReportCombo();
        loadStatistics();
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
        overdueTasksLabel.setText(String.valueOf(dashboard.getOverdueTasks()));
    }

    @FXML
    private void generateReport() {
        String selectedReport = reportTypeCombo.getSelectionModel().getSelectedItem();
        if (selectedReport == null) {
            reportTextArea.setText("Please select a report type.");
            return;
        }

        String reportContent = switch (selectedReport) {
            case REPORT_COMPREHENSIVE -> reportGenerator.generateComprehensiveReport(context.getTaskManager(),
                    context.getApplicationUserRepository());
            case REPORT_TASK -> reportGenerator.generateTaskReport(context.getTaskManager());
            case REPORT_USER -> reportGenerator.generateUserReport(context.getApplicationUserRepository());
            case REPORT_OVERDUE -> reportGenerator.generateOverdueTasksReport(context.getTaskManager());
            case REPORT_PRIORITY -> reportGenerator.generateTasksByPriorityReport(context.getTaskManager());
            case REPORT_STATUS -> reportGenerator.generateTasksByStatusReport(context.getTaskManager());
            default -> "No report available.";
        };

        reportTextArea.setText(reportContent);
    }
}
