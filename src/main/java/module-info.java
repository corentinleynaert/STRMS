module com.application.strms {
    requires javafx.controls;
    requires jbcrypt;

    requires transitive javafx.graphics;
    requires transitive javafx.fxml;
    requires de.huxhorn.sulky.ulid;
    requires java.desktop;

    exports com.application.strms.app;
    exports com.application.strms.presentation.navigation;
    exports com.application.strms.infrastructure.persistence;
    exports com.application.strms.infrastructure.security;
    exports com.application.strms.domain.model;
    exports com.application.strms.domain.repository;
    exports com.application.strms.domain.service;
    exports com.application.strms.application.service;
    exports com.application.strms.application.result;
    exports com.application.strms.presentation.loader;
    exports com.application.strms.application;
    exports com.application.strms.application.session;
    exports com.application.strms.presentation.controller;
    exports com.application.strms.presentation.controller.pages;
    exports com.application.strms.presentation.controller.components;

    opens com.application.strms.app to javafx.fxml;
    opens com.application.strms.presentation.loader to javafx.fxml;
    opens com.application.strms.presentation.navigation to javafx.fxml;
    opens com.application.strms.presentation.controller.pages to javafx.fxml;
    opens com.application.strms.presentation.controller to javafx.fxml;
    opens com.application.strms.presentation.controller.components to javafx.fxml;
}
