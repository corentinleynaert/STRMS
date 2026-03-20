package com.application.strms.application;

import com.application.strms.application.service.AuthService;
import com.application.strms.application.session.SessionManager;
import com.application.strms.domain.repository.UserRepository;

public class ApplicationContext {
    private final AuthService authService;
    private final SessionManager sessionManager;
    private final UserRepository userRepository;

    public ApplicationContext(AuthService authService, SessionManager sessionManager, UserRepository userRepository) {
        this.authService = authService;
        this.sessionManager = sessionManager;
        this.userRepository = userRepository;
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
}