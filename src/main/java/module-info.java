module com.streat.strms {
    requires javafx.controls;
    requires javafx.fxml;
    requires jbcrypt;

    opens com.streat.strms to javafx.fxml;
    exports com.streat.strms;
}