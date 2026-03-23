module com.application.strms {
    requires java.desktop;
    requires transitive javafx.controls;
    requires jbcrypt;
    requires de.huxhorn.sulky.ulid;

    requires transitive javafx.fxml;
    requires transitive javafx.graphics;

    exports com.strms.app;
    exports com.strms.application;
    exports com.strms.application.result;
    exports com.strms.application.service;
    exports com.strms.application.session;
    exports com.strms.domain.exception;
    exports com.strms.domain.model;
    exports com.strms.domain.repository;
    exports com.strms.domain.service;
    exports com.strms.infrastructure.persistence;
    exports com.strms.infrastructure.repository;
    exports com.strms.infrastructure.security;
    exports com.strms.presentation.controller;
    exports com.strms.presentation.controller.components;
    exports com.strms.presentation.controller.pages;
    exports com.strms.presentation.loader;
    exports com.strms.presentation.navigation;
    exports com.strms.presentation.service;

    opens com.strms.app to javafx.fxml;
    opens com.strms.application to javafx.fxml;
    opens com.strms.application.session to javafx.fxml;
    opens com.strms.presentation.model to javafx.base;
    opens com.strms.presentation.controller to javafx.fxml;
    opens com.strms.presentation.controller.components to javafx.fxml;
    opens com.strms.presentation.controller.pages to javafx.fxml;
    opens com.strms.presentation.loader to javafx.fxml;
    opens com.strms.presentation.navigation to javafx.fxml;
}
