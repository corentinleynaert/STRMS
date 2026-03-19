module com.application.strms {
    requires javafx.controls;
    requires javafx.fxml;
    requires jbcrypt;

    exports com.application.strms.app;
    exports com.application.strms.presentation;
    exports com.application.strms.infrastructure.persistence;
    exports com.application.strms.infrastructure.security;
    exports com.application.strms.domain.model;
    exports com.application.strms.domain.repository;
    exports com.application.strms.domain.service;
    exports com.application.strms.application.service;
    exports com.application.strms.application.result;

    opens com.application.strms.presentation to javafx.fxml;
    opens com.application.strms.app to javafx.fxml;
    exports com.application.strms.presentation.loader;
    opens com.application.strms.presentation.loader to javafx.fxml;
}