module com.application.strms {
    requires javafx.controls;
    requires javafx.fxml;
    requires jbcrypt;

    exports com.application.strms.app;
    exports com.application.strms.view;
    exports com.application.strms.utils;

    opens com.application.strms.view to javafx.fxml;
}