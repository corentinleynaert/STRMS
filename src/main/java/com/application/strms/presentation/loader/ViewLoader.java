package com.application.strms.presentation.loader;

import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.net.URL;

public class ViewLoader {
    public static FXMLLoader load(String name) throws IOException {
        String basePath = name.equals("Layout")
                ? "/com/application/strms/presentation/"
                : "/com/application/strms/presentation/pages/";

        String path = basePath + name + ".fxml";
        URL fxmlUrl = ViewLoader.class.getResource(path);

        if (fxmlUrl == null) {
            throw new IOException("FXML file not found: " + path);
        }

        try {
            return new FXMLLoader(fxmlUrl);
        } catch (Exception e) {
            throw new IOException("Failed to load FXML: " + path, e);
        }
    }
}