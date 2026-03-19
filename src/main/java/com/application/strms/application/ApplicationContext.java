package com.application.strms.application;

import com.application.strms.application.service.AuthService;
import com.application.strms.application.session.SessionManager;

public class ApplicationContext {
    private final AuthService authService;
    private final SessionManager sessionManager;

    public ApplicationContext(AuthService authService, SessionManager sessionManager) {
        this.authService = authService;
        this.sessionManager = sessionManager;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }
}