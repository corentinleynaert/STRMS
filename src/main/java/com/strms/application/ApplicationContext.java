package com.strms.application;

import com.strms.application.service.AuthService;
import com.strms.application.service.Dashboard;
import com.strms.application.service.ReportGenerator;
import com.strms.application.service.TaskManager;
import com.strms.application.session.SessionManager;
import com.strms.domain.repository.UserRepository;

public class ApplicationContext {
    private final AuthService authService;
    private final SessionManager sessionManager;
    private final UserRepository userRepository;
    private final TaskManager taskManager;
    private final Dashboard dashboard;
    private final ReportGenerator reportGenerator;

    public ApplicationContext(AuthService authService, SessionManager sessionManager, UserRepository userRepository,
            TaskManager taskManager) {
        this.authService = authService;
        this.sessionManager = sessionManager;
        this.userRepository = userRepository;
        this.taskManager = taskManager;
        this.dashboard = new Dashboard(taskManager, userRepository);
        this.reportGenerator = new ReportGenerator();
    }

    public AuthService getAuthService() {
        return authService;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public UserRepository getApplicationUserRepository() {
        return userRepository;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public ReportGenerator getReportGenerator() {
        return reportGenerator;
    }
}