package com.application.strms.application;

import com.application.strms.application.service.AuthService;
import com.application.strms.application.service.TaskManager;
import com.application.strms.application.session.SessionManager;
import com.application.strms.domain.repository.UserRepository;
import com.application.strms.presentation.service.NotificationManager;
import com.application.strms.presentation.service.ReportGenerator;

public class ApplicationContext {
    private final AuthService authService;
    private final SessionManager sessionManager;
    private final UserRepository userRepository;
    private final TaskManager taskManager;
    private final NotificationManager notificationManager;
    private final ReportGenerator reportGenerator;

    public ApplicationContext(AuthService authService, SessionManager sessionManager, UserRepository userRepository,
            TaskManager taskManager, NotificationManager notificationManager) {
        this.authService = authService;
        this.sessionManager = sessionManager;
        this.userRepository = userRepository;
        this.taskManager = taskManager;
        this.notificationManager = notificationManager;
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

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public ReportGenerator getReportGenerator() {
        return reportGenerator;
    }
}