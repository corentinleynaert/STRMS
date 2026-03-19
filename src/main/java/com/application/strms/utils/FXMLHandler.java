package com.application.strms.utils;

import javafx.fxml.FXMLLoader;
import java.net.URL;

public class FXMLHandler {

    public static FXMLLoader load(String name) {
        String path = "/com/application/strms/view/" + name + ".fxml";
        URL fxmlUrl = FXMLHandler.class.getResource(path);

        if (fxmlUrl == null) {
            throw new RuntimeException("Can't find FXML file: " + path);
        }

        return new FXMLLoader(fxmlUrl);
    }
}