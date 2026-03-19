package com.application.strms.presentation.loader;

import javafx.fxml.FXMLLoader;
import java.net.URL;

public class ViewLoader {
    public static FXMLLoader load(String name) {
        String path = "/com/application/strms/presentation/" + name + ".fxml";
        URL fxmlUrl = ViewLoader.class.getResource(path);

        if (fxmlUrl == null) {
            throw new RuntimeException("Can't find FXML file: " + path);
        }

        return new FXMLLoader(fxmlUrl);
    }
}